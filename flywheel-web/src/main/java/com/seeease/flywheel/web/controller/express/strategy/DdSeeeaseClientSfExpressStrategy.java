package com.seeease.flywheel.web.controller.express.strategy;

import com.doudian.open.api.logistics_newCreateOrder.LogisticsNewCreateOrderRequest;
import com.doudian.open.api.logistics_newCreateOrder.param.*;
import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.request.SaleOrderDetailsRequest;
import com.seeease.flywheel.sale.result.SaleOrderDetailsResult;
import com.seeease.flywheel.web.controller.express.request.ExpressCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;

/**
 * @Author Mr. Du
 * @Description 线下发到客户
 * @Date create in 2023/9/1 17:01
 */
@Component
@Slf4j
public class DdSeeeaseClientSfExpressStrategy extends DdSfExpressStrategy {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleOrderFacade saleOrderFacade;

    @Override
    public Integer getReceiverType() {
        return 3;
    }

    @Override
    public void packageOrder(ExpressCreateRequest expressCreateRequest) {
        LogisticsNewCreateOrderRequest logisticsNewCreateOrderRequest = expressCreateRequest.getLogisticsNewCreateOrderRequest();
        LogisticsNewCreateOrderParam param = logisticsNewCreateOrderRequest.getParam();
        OrderInfosItem orderInfosItem = new OrderInfosItem();
        orderInfosItem.setProductType("2");
        orderInfosItem.setOrderId(StringUtils.split(expressCreateRequest.getPrintOptionResult().getBizOrderCode(), ",")[0]);
        param.setOrderInfos(Arrays.asList(orderInfosItem));
    }

    @Override
    public void packageReceiver(ExpressCreateRequest expressCreateRequest) {

        SaleOrderDetailsResult saleOrderDetailsResult = saleOrderFacade.details(SaleOrderDetailsRequest.builder().serialNo(expressCreateRequest.getPrintOptionResult().getSerialNo()).build());

        if (ObjectUtils.isEmpty(saleOrderDetailsResult)) {
            return;
        }

        LogisticsNewCreateOrderRequest logisticsNewCreateOrderRequest = expressCreateRequest.getLogisticsNewCreateOrderRequest();
        for (OrderInfosItem orderInfo : logisticsNewCreateOrderRequest.getParam().getOrderInfos()) {
            ReceiverInfo receiverInfo = new ReceiverInfo();
            Address_4_4 address_4_4 = new Address_4_4();
            Contact contact_4_4 = new Contact();
            //2、创建匹配规则
            Matcher m = PATTERN.matcher(saleOrderDetailsResult.getContactAddress());
            m.find();
            if (m.groupCount() == 0) {
                log.error("地址匹配不规则:{}", saleOrderDetailsResult.getContactAddress());
                return;
            }
            //加入省
            address_4_4.setProvinceName(m.group("province").trim());
            //加入市
            address_4_4.setCityName(m.group("city").trim());
            //加入区
            address_4_4.setDistrictName(m.group("county").trim());
            //详细地址
            address_4_4.setStreetName(m.group("address").trim());
            address_4_4.setDetailAddress(saleOrderDetailsResult.getContactAddress());
            address_4_4.setCountryCode("CHN");

            contact_4_4.setName(saleOrderDetailsResult.getContactName());
            contact_4_4.setMobile(saleOrderDetailsResult.getContactPhone());

            receiverInfo.setAddress(address_4_4);
            receiverInfo.setContact(contact_4_4);

            orderInfo.setReceiverInfo(receiverInfo);
            ItemsItem itemsItem = new ItemsItem();
            itemsItem.setItemName(expressCreateRequest.getPrintOptionResult().getPrintProductName());
            itemsItem.setItemCount(1);

            //商品信息
            orderInfo.setItems(Arrays.asList(itemsItem));
        }
    }
}
