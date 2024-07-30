package com.seeease.flywheel.web.controller.express.strategy;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.doudian.open.api.logistics_newCreateOrder.LogisticsNewCreateOrderRequest;
import com.doudian.open.api.logistics_newCreateOrder.LogisticsNewCreateOrderResponse;
import com.doudian.open.api.logistics_newCreateOrder.param.Address;
import com.doudian.open.api.logistics_newCreateOrder.param.Contact;
import com.doudian.open.api.logistics_newCreateOrder.param.LogisticsNewCreateOrderParam;
import com.doudian.open.api.logistics_newCreateOrder.param.SenderInfo;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.sale.result.PrintOptionResult;
import com.seeease.flywheel.sf.IExpressOrderFacade;
import com.seeease.flywheel.sf.request.ExpressOrderCreateRequest;
import com.seeease.flywheel.sf.request.ExpressOrderEditRequest;
import com.seeease.flywheel.sf.result.ExpressOrderCreateResult;
import com.seeease.flywheel.storework.IStoreWorkFacade;
import com.seeease.flywheel.storework.request.StoreWorkEditRequest;
import com.seeease.flywheel.storework.result.StoreWorkDetailResult;
import com.seeease.flywheel.web.controller.express.ExpressStrategy;
import com.seeease.flywheel.web.controller.express.request.ExpressCreateRequest;
import com.seeease.flywheel.web.controller.express.result.ExpressCreateResult;
import com.seeease.flywheel.web.entity.DouyinPrintMapping;
import com.seeease.flywheel.web.infrastructure.service.DouyinPrintMappingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/9/1 16:28
 */
@Slf4j
public abstract class DdSfExpressStrategy implements ExpressStrategy<ExpressCreateRequest, ExpressCreateResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IExpressOrderFacade expressOrderFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkFacade storeWorkFacade;

    public static final Pattern PATTERN = Pattern.compile("(?<province>[^省]+省|.+自治区|[^澳门]+澳门|[^香港]+香港|[^市]+市)?(?<city>[^自治州]+自治州|[^特别行政区]+特别行政区|[^市]+市|.*?地区|.*?行政单位|.+盟|市辖区|[^县]+县)(?<county>[^县]+县|[^市]+市|[^镇]+镇|[^区]+区|[^乡]+乡|.+场|.+旗|.+海域|.+岛)?(?<address>.*)");

    @Resource
    private DouyinPrintMappingService douyinPrintMappingService;

    @Override
    public void createExpressOrder(ExpressCreateRequest expressCreateRequest) {

        ExpressOrderCreateResult expressOrderCreateResult = expressOrderFacade.create(ExpressOrderCreateRequest.builder()
                .serialNo(expressCreateRequest.getPrintOptionResult().getSerialNo())
                .sonSerialNo(expressCreateRequest.getPrintOptionResult().getSerialNo())
                .douYinShopId(expressCreateRequest.getDouYinShopMapping().getDouYinShopId())
                //抖音渠道
                .expressChannel(2)
                .requestId(expressCreateRequest.getRequestId()).build());

        expressCreateRequest.setExpressOrderCreateResult(expressOrderCreateResult);

    }

    @Override
    public void packageSender(ExpressCreateRequest expressCreateRequest) {

        PrintOptionResult printOptionResult = expressCreateRequest.getPrintOptionResult();

        DouyinPrintMapping douyinPrintMapping = douyinPrintMappingService.getOne(Wrappers.<DouyinPrintMapping>lambdaQuery().eq(DouyinPrintMapping::getShopId, printOptionResult.getShopId()));
        expressCreateRequest.setDouyinPrintMapping(douyinPrintMapping);
        LogisticsNewCreateOrderRequest logisticsNewCreateOrderRequest = expressCreateRequest.getLogisticsNewCreateOrderRequest();

        //发货人
        SenderInfo senderInfo = new SenderInfo();

        Address address = new Address();
        address.setProvinceName(douyinPrintMapping.getProvinceName());
        address.setCityName(douyinPrintMapping.getCityName());
        address.setDistrictName(douyinPrintMapping.getDistrictName());
        address.setStreetName(douyinPrintMapping.getStreetName());
        address.setDetailAddress(douyinPrintMapping.getDetailAddress());
        address.setCountryCode("CHN");

        Contact contact = new Contact();
        contact.setName(douyinPrintMapping.getName());
        contact.setMobile(douyinPrintMapping.getMobile());
        senderInfo.setAddress(address);
        senderInfo.setContact(contact);

        LogisticsNewCreateOrderParam param = logisticsNewCreateOrderRequest.getParam();
        param.setSenderInfo(senderInfo);
    }

    @Override
    public ExpressCreateResult execute(ExpressCreateRequest expressCreateRequest) {

        LogisticsNewCreateOrderRequest logisticsNewCreateOrderRequest = expressCreateRequest.getLogisticsNewCreateOrderRequest();
        LogisticsNewCreateOrderParam param = logisticsNewCreateOrderRequest.getParam();
        param.setLogisticsCode("shunfeng");

        log.info("商家ERP/ISV 向字节电子面单系统获取单号和打印信息:{}", logisticsNewCreateOrderRequest.toString());
        LogisticsNewCreateOrderResponse logisticsNewCreateOrderResponse = logisticsNewCreateOrderRequest.execute(expressCreateRequest.getAccessToken());
        log.info("商家ERP/ISV 向字节电子面单系统获取单号和打印信息:{}", logisticsNewCreateOrderResponse.toString());

        if (ObjectUtils.isNotEmpty(logisticsNewCreateOrderResponse) && "10000".equals(logisticsNewCreateOrderResponse.getCode())) {
            expressCreateRequest.getExpressOrderCreateResult().setWaybillNo(logisticsNewCreateOrderResponse.getData().getEbillInfos().get(FlywheelConstant.INDEX).getTrackNo());
        }
        return ExpressCreateResult.builder().logisticsNewCreateOrderResponse(logisticsNewCreateOrderResponse).build();
    }

    @Override
    public void editExpressOrder(ExpressCreateRequest expressCreateRequest) {

        ExpressOrderCreateResult expressOrderCreateResult = expressCreateRequest.getExpressOrderCreateResult();

        if (ObjectUtils.isEmpty(expressOrderCreateResult) || StringUtils.isBlank(expressOrderCreateResult.getWaybillNo())) {
            //更新物流日志 下单失败
            expressOrderFacade.edit(ExpressOrderEditRequest.builder().expressState(3)
                    .id(expressCreateRequest.getExpressOrderCreateResult().getId())
                    .build());
            return;
        }

        List<StoreWorkDetailResult> resultList = expressCreateRequest.getResultList();

        //写入收货信息 更新到发货单
        resultList.forEach(e -> {
            StoreWorkEditRequest storeWorkEditRequest = new StoreWorkEditRequest();
            storeWorkEditRequest.setWorkId(e.getId());
            storeWorkEditRequest.setDeliveryExpressNumber(expressCreateRequest.getExpressOrderCreateResult().getWaybillNo());
            storeWorkFacade.edit(storeWorkEditRequest);
        });

        //更新物流日志 下单成功
        expressOrderFacade.edit(ExpressOrderEditRequest.builder().expressState(2).
                id(expressCreateRequest.getExpressOrderCreateResult().getId())
                .expressNo(expressCreateRequest.getExpressOrderCreateResult().getWaybillNo()).build());

    }


}
