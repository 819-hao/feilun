package com.seeease.flywheel.web.common.express.channel;

import com.doudian.open.api.logistics_cancelOrder.LogisticsCancelOrderRequest;
import com.doudian.open.api.logistics_cancelOrder.LogisticsCancelOrderResponse;
import com.doudian.open.api.logistics_cancelOrder.param.LogisticsCancelOrderParam;
import com.doudian.open.api.logistics_newCreateOrder.LogisticsNewCreateOrderRequest;
import com.doudian.open.api.logistics_newCreateOrder.LogisticsNewCreateOrderResponse;
import com.doudian.open.api.logistics_newCreateOrder.data.ErrInfosItem;
import com.doudian.open.api.logistics_newCreateOrder.param.*;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.web.common.context.DouYinConfig;
import com.seeease.flywheel.web.entity.ExpressOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/9/19
 */
@Slf4j
@Component
public class DouYinSFExpressChannel implements ExpressChannel {

    /**
     * 国家编码（默认CHN，目前只有国内业务）
     */
    private static final String COUNTRY_CODE = "CHN";
    /**
     * 物流服务商编码
     */
    private static final String LOGISTICS_CODE = "shunfeng";


    @Override
    public ExpressChannelTypeEnum getChanelType() {
        return ExpressChannelTypeEnum.DY_SF;
    }

    @Override
    public ExpressPlaceOrderResult placeOrder(ExpressPlaceOrder order) {
        //发件人信息
        ExpressPlaceOrder.ContactsInfo sender = order.getSenderInfo();
        //收件人信息
        ExpressPlaceOrder.ContactsInfo receiver = order.getReceiverInfo();

        //发货人地址信息
        Address address = new Address();
        address.setProvinceName(sender.getProvince());
        address.setCityName(sender.getCity());
        address.setDistrictName(sender.getTown());
        address.setStreetName(sender.getStreet());
        address.setDetailAddress(sender.getAddressDetail());
        address.setCountryCode(COUNTRY_CODE);
        //发货人信息
        Contact contact = new Contact();
        contact.setName(sender.getContactName());
        contact.setMobile(sender.getContactTel());
        //发货信息
        SenderInfo senderInfo = new SenderInfo();
        senderInfo.setAddress(address);
        senderInfo.setContact(contact);

        //收件人地址信息
        Address_4_4 address_4_4 = new Address_4_4();
        Contact contact_4_4 = new Contact();
        address_4_4.setProvinceName(receiver.getProvince());
        address_4_4.setCityName(receiver.getCity());
        address_4_4.setDistrictName(receiver.getTown());
        address_4_4.setStreetName(receiver.getStreet());
        address_4_4.setDetailAddress(receiver.getAddressDetail());
        address_4_4.setCountryCode(COUNTRY_CODE);
        //收件人
        contact_4_4.setName(receiver.getContactName());
        contact_4_4.setMobile(receiver.getContactTel());
        //收货方信息
        ReceiverInfo receiverInfo = new ReceiverInfo();
        receiverInfo.setAddress(address_4_4);
        receiverInfo.setContact(contact_4_4);

        //订单信息
        OrderInfosItem orderInfosItem = new OrderInfosItem();
        orderInfosItem.setProductType(order.getSfProductCode().getValue());
        orderInfosItem.setOrderId(order.getOrderInfo().getDouYinOrderId());
        //设置收货方信息
        orderInfosItem.setReceiverInfo(receiverInfo);
        //商品信息
        orderInfosItem.setItems(order.getOrderInfo().getGoodsInfoList()
                .stream()
                .map(t -> {
                    ItemsItem itemsItem = new ItemsItem();
                    itemsItem.setItemName(t.getInfo());
                    itemsItem.setItemCount(NumberUtils.INTEGER_ONE);
                    return itemsItem;
                }).collect(Collectors.toList()));

        //构建请求参数
        LogisticsNewCreateOrderRequest logisticsNewCreateOrderRequest = new LogisticsNewCreateOrderRequest();
        LogisticsNewCreateOrderParam param = logisticsNewCreateOrderRequest.getParam();
        //设置发货信息
        param.setSenderInfo(senderInfo);
        //设置订单信息
        param.setOrderInfos(Arrays.asList(orderInfosItem));
        //物流服务商编码
        param.setLogisticsCode(LOGISTICS_CODE);

        log.info("商家ERP/ISV 向字节电子面单系统获取单号和打印信息:{}", logisticsNewCreateOrderRequest.toString());
        LogisticsNewCreateOrderResponse logisticsNewCreateOrderResponse = logisticsNewCreateOrderRequest.execute(DouYinConfig.getAccessToken(order.getOrderInfo().getDouYinShopId()));
        log.info("商家ERP/ISV 向字节电子面单系统获取单号和打印信息:{}", logisticsNewCreateOrderResponse.toString());

        if (ObjectUtils.isNotEmpty(logisticsNewCreateOrderResponse) && "10000".equals(logisticsNewCreateOrderResponse.getCode())) {

            if (CollectionUtils.isEmpty(logisticsNewCreateOrderResponse.getData().getEbillInfos())) {
                return ExpressPlaceOrderResult.builder()
                        .success(false)
                        .businessNo(order.getBusinessNo())
                        .orderNo(order.getOrderInfo().getOrderNo())
                        .errMsg(Optional.ofNullable(logisticsNewCreateOrderResponse.getData().getErrInfos())
                                .map(t -> t.stream()
                                        .map(ErrInfosItem::getErrMsg)
                                        .findFirst()
                                        .orElse(StringUtils.EMPTY))
                                .orElse(StringUtils.EMPTY))
                        .build();
            }
            return ExpressPlaceOrderResult.builder()
                    .success(true)
                    .businessNo(order.getBusinessNo())
                    .orderNo(order.getOrderInfo().getOrderNo())
                    .expressNumber(logisticsNewCreateOrderResponse.getData().getEbillInfos().get(FlywheelConstant.INDEX).getTrackNo())
                    .build();
        }

        return ExpressPlaceOrderResult.builder()
                .success(false)
                .businessNo(order.getBusinessNo())
                .orderNo(order.getOrderInfo().getOrderNo())
                .errMsg(logisticsNewCreateOrderResponse.getMsg())
                .build();
    }

    @Override
    public ExpressRecoveryOrderResult recoveryOrder(ExpressOrder order) {
        LogisticsCancelOrderRequest logisticsCancelOrderRequest = new LogisticsCancelOrderRequest();
        LogisticsCancelOrderParam param = logisticsCancelOrderRequest.getParam();
        param.setLogisticsCode(LOGISTICS_CODE);
        param.setTrackNo(order.getExpressNo());
        log.info("用于ISV/商家ERP系统 端发起取消已获取的电子面单号:{}", logisticsCancelOrderRequest.toString());
        LogisticsCancelOrderResponse logisticsCancelOrderResponse = logisticsCancelOrderRequest.execute(DouYinConfig.getAccessToken(order.getDouYinShopId()));
        log.info("用于ISV/商家ERP系统 端发起取消已获取的电子面单号:{}", logisticsCancelOrderResponse.toString());

        if ("10000".equals(logisticsCancelOrderResponse.getCode())) {
            return ExpressRecoveryOrderResult.builder()
                    .success(true)
                    .build();
        }
        return ExpressRecoveryOrderResult.builder()
                .success(false)
                .errMsg(logisticsCancelOrderResponse.getMsg())
                .build();
    }
}
