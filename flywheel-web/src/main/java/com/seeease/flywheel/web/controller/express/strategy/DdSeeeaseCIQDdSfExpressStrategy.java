package com.seeease.flywheel.web.controller.express.strategy;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.doudian.open.api.logistics_newCreateOrder.LogisticsNewCreateOrderRequest;
import com.doudian.open.api.logistics_newCreateOrder.param.*;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.web.controller.express.request.ExpressCreateRequest;
import com.seeease.flywheel.web.entity.DouYinOrder;
import com.seeease.flywheel.web.entity.DouYinOrderLine;
import com.seeease.flywheel.web.infrastructure.service.DouYinOrderLineService;
import com.seeease.flywheel.web.infrastructure.service.DouYinOrderService;
import com.seeease.flywheel.web.infrastructure.service.DouyinPrintMappingService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 飞轮发国检
 * @Date create in 2023/9/1 17:01
 */
@Component
public class DdSeeeaseCIQDdSfExpressStrategy extends DdSfExpressStrategy {

    @NacosValue(value = "${express.sf.pullAddress:7551234567}", autoRefreshed = true)
    private String pullAddress;
    @NacosValue(value = "${express.sf.pullProvince:7551234567}", autoRefreshed = true)
    private String pullProvince;
    @NacosValue(value = "${express.sf.pullCity:7551234567}", autoRefreshed = true)
    private String pullCity;
    @NacosValue(value = "${express.sf.pullCounty:7551234567}", autoRefreshed = true)
    private String pullCounty;
    @NacosValue(value = "${express.sf.pullContact:7551234567}", autoRefreshed = true)
    private String pullContact;
    @NacosValue(value = "${express.sf.pullMobile:7551234567}", autoRefreshed = true)
    private String pullMobile;

    @Resource
    private DouyinPrintMappingService douyinPrintMappingService;

    @Resource
    private DouYinOrderService douYinOrderService;

    @Resource
    private DouYinOrderLineService douYinOrderLineService;

    @Override
    public Integer getReceiverType() {
        return 1;
    }

    @Override
    public void packageOrder(ExpressCreateRequest expressCreateRequest) {
        LogisticsNewCreateOrderRequest logisticsNewCreateOrderRequest = expressCreateRequest.getLogisticsNewCreateOrderRequest();
        LogisticsNewCreateOrderParam param = logisticsNewCreateOrderRequest.getParam();
        OrderInfosItem orderInfosItem = new OrderInfosItem();
        orderInfosItem.setProductType("2");
        orderInfosItem.setOrderId(expressCreateRequest.getPrintOptionResult().getSerialNo());
        param.setOrderChannel("54");
        param.setOrderInfos(Arrays.asList(orderInfosItem));
    }

    @Override
    public void packageReceiver(ExpressCreateRequest expressCreateRequest) {

        DouYinOrder douYinOrder = douYinOrderService.list(Wrappers.<DouYinOrder>lambdaQuery().in(DouYinOrder::getOrderId, StringUtils.split(expressCreateRequest.getPrintOptionResult().getBizOrderCode(), ","))).get(FlywheelConstant.INDEX);

        if (ObjectUtils.isEmpty(douYinOrder)) {
            return;
        }

        LogisticsNewCreateOrderRequest logisticsNewCreateOrderRequest = expressCreateRequest.getLogisticsNewCreateOrderRequest();

        for (OrderInfosItem orderInfo : logisticsNewCreateOrderRequest.getParam().getOrderInfos()) {
            ReceiverInfo receiverInfo = new ReceiverInfo();
            Address_4_4 address_4_4 = new Address_4_4();
            Contact contact_4_4 = new Contact();

            //加入省
            address_4_4.setProvinceName(pullProvince);
            //加入市
            address_4_4.setCityName(pullCity);
            //加入区
            address_4_4.setDistrictName(pullCounty);
            //详细地址
            address_4_4.setStreetName(pullAddress);
            address_4_4.setDetailAddress(pullAddress);
            address_4_4.setCountryCode("CHN");

            contact_4_4.setName(pullContact);
            contact_4_4.setMobile(pullMobile);

            receiverInfo.setAddress(address_4_4);
            receiverInfo.setContact(contact_4_4);

            orderInfo.setReceiverInfo(receiverInfo);

            //商品信息
            orderInfo.setItems(douYinOrderLineService.list(Wrappers.<DouYinOrderLine>lambdaQuery().eq(DouYinOrderLine::getOrderId, douYinOrder.getId())).stream().map(douYinOrderLine -> {
                ItemsItem itemsItem = new ItemsItem();
                itemsItem.setItemName(douYinOrderLine.getProductName());
                itemsItem.setItemCount(1);
                return itemsItem;
            }).collect(Collectors.toList()));
        }
    }

}
