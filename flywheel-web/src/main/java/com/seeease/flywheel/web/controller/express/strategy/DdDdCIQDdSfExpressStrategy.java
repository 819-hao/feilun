package com.seeease.flywheel.web.controller.express.strategy;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.doudian.open.api.logistics_newCreateOrder.LogisticsNewCreateOrderRequest;
import com.doudian.open.api.logistics_newCreateOrder.param.*;
import com.seeease.flywheel.web.controller.express.request.ExpressCreateRequest;
import com.seeease.flywheel.web.entity.DouYinOrder;
import com.seeease.flywheel.web.entity.DouYinOrderLine;
import com.seeease.flywheel.web.infrastructure.service.DouYinOrderLineService;
import com.seeease.flywheel.web.infrastructure.service.DouYinOrderService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 抖店发国检
 * @Date create in 2023/9/1 17:01
 */
@Component
public class DdDdCIQDdSfExpressStrategy extends DdSfExpressStrategy {

    @Resource
    private DouYinOrderService douYinOrderService;

    @Resource
    private DouYinOrderLineService douYinOrderLineService;

    @Override
    public Integer getReceiverType() {
        return 2;
    }

    @Override
    public void packageOrder(ExpressCreateRequest expressCreateRequest) {
        LogisticsNewCreateOrderRequest logisticsNewCreateOrderRequest = expressCreateRequest.getLogisticsNewCreateOrderRequest();
        LogisticsNewCreateOrderParam param = logisticsNewCreateOrderRequest.getParam();
        OrderInfosItem orderInfosItem = new OrderInfosItem();
        orderInfosItem.setOrderId(StringUtils.split(expressCreateRequest.getPrintOptionResult().getBizOrderCode(), ",")[0]);
        orderInfosItem.setProductType("2");
        param.setOrderInfos(Arrays.asList(orderInfosItem));
    }

    @Override
    public void packageReceiver(ExpressCreateRequest expressCreateRequest) {

        List<DouYinOrder> douYinOrderList = douYinOrderService.list(Wrappers.<DouYinOrder>lambdaQuery().in(DouYinOrder::getOrderId, StringUtils.split(expressCreateRequest.getPrintOptionResult().getBizOrderCode(), ",")));

        if (CollectionUtils.isEmpty(douYinOrderList)) {
            return;
        }

        for (DouYinOrder douYinOrder : douYinOrderList) {

            List<DouYinOrderLine> douYinOrderLineList = douYinOrderLineService.list(Wrappers.<DouYinOrderLine>lambdaQuery().eq(DouYinOrderLine::getOrderId, douYinOrder.getId()));

            for (DouYinOrderLine douYinOrderLine : douYinOrderLineList) {

                if (ObjectUtils.isNotEmpty(douYinOrderLine.getScId())) {

                    LogisticsNewCreateOrderParam param = expressCreateRequest.getLogisticsNewCreateOrderRequest().getParam();

                    for (OrderInfosItem orderInfo : param.getOrderInfos()) {

                        //查询到抖店质检地址
                        //构建收货地址
                        ReceiverInfo receiverInfo = new ReceiverInfo();
                        Address_4_4 address_4_4 = new Address_4_4();
                        Contact contact_4_4 = new Contact();
                        //加入省
                        address_4_4.setProvinceName(douYinOrderLine.getScProvince());
                        //加入市
                        address_4_4.setCityName(douYinOrderLine.getScCity());
                        //加入区
                        address_4_4.setDistrictName(douYinOrderLine.getScDistrict());
                        //详细地址
                        address_4_4.setStreetName(douYinOrderLine.getScStreet());
                        address_4_4.setDetailAddress(douYinOrderLine.getScAddress());
                        address_4_4.setCountryCode("CHN");

                        contact_4_4.setName(douYinOrderLine.getScName());
                        contact_4_4.setMobile(douYinOrderLine.getScPhone());

                        receiverInfo.setAddress(address_4_4);
                        receiverInfo.setContact(contact_4_4);

                        orderInfo.setOrderId(douYinOrder.getOrderId());

                        orderInfo.setReceiverInfo(receiverInfo);

                        //构建商品信息
                        ItemsItem itemsItem = new ItemsItem();
                        itemsItem.setItemName(douYinOrderLine.getProductName());
                        itemsItem.setItemCount(1);
                        orderInfo.setItems(Arrays.asList(itemsItem));

                        return;
                    }
                    return;
                }
            }
        }
        return;
    }

}
