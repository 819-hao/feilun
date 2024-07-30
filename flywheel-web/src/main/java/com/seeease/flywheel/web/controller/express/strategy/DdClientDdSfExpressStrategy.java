package com.seeease.flywheel.web.controller.express.strategy;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.doudian.open.api.logistics_newCreateOrder.LogisticsNewCreateOrderRequest;
import com.doudian.open.api.logistics_newCreateOrder.param.*;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.web.controller.express.request.ExpressCreateRequest;
import com.seeease.flywheel.web.entity.DouYinOrder;
import com.seeease.flywheel.web.entity.DouYinOrderLine;
import com.seeease.flywheel.web.infrastructure.service.DouYinOrderLineService;
import com.seeease.flywheel.web.infrastructure.service.DouYinOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 抖店发到客户
 * @Date create in 2023/9/1 17:01
 */
@Component
@Slf4j
public class DdClientDdSfExpressStrategy extends DdSfExpressStrategy {

    @Resource
    private DouYinOrderService douYinOrderService;

    @Resource
    private DouYinOrderLineService douYinOrderLineService;

    @Override
    public Integer getReceiverType() {
        return 0;
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

        DouYinOrder douYinOrder = douYinOrderService.list(Wrappers.<DouYinOrder>lambdaQuery().in(DouYinOrder::getOrderId, StringUtils.split(expressCreateRequest.getPrintOptionResult().getBizOrderCode(), ","))).get(FlywheelConstant.INDEX);

        if (ObjectUtils.isEmpty(douYinOrder)) {
            return;
        }

        LogisticsNewCreateOrderRequest logisticsNewCreateOrderRequest = expressCreateRequest.getLogisticsNewCreateOrderRequest();
        for (OrderInfosItem orderInfo : logisticsNewCreateOrderRequest.getParam().getOrderInfos()) {
            ReceiverInfo receiverInfo = new ReceiverInfo();
            Address_4_4 address_4_4 = new Address_4_4();
            Contact contact_4_4 = new Contact();
            //2、创建匹配规则
            Matcher m = PATTERN.matcher(douYinOrder.getEncryptAddrArea());
            m.find();
            //加入省
            address_4_4.setProvinceName(Optional.ofNullable(douYinOrder.getProvince())
                    .filter(StringUtils::isNotBlank)
                    .orElseGet(() -> StringUtils.trim(m.group("province"))));
            //加入市
            address_4_4.setCityName(Optional.ofNullable(douYinOrder.getCity())
                    .filter(StringUtils::isNotBlank)
                    .orElseGet(() -> StringUtils.trim(m.group("city"))));
            //加入区
            address_4_4.setDistrictName(Optional.ofNullable(douYinOrder.getTown())
                    .filter(StringUtils::isNotBlank)
                    .orElseGet(() -> StringUtils.trim(m.group("county"))));
            //详细地址
            address_4_4.setStreetName(Optional.ofNullable(douYinOrder.getStreet())
                    .filter(StringUtils::isNotBlank)
                    .orElseGet(() -> StringUtils.trim(m.group("address"))));
            address_4_4.setDetailAddress(douYinOrder.getEncryptDetail());
            address_4_4.setCountryCode("CHN");

            contact_4_4.setName(douYinOrder.getEncryptPostReceiver());
            contact_4_4.setMobile(douYinOrder.getEncryptPostTel());

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
