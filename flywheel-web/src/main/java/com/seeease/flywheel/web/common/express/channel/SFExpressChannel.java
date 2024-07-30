package com.seeease.flywheel.web.common.express.channel;

import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.web.common.express.client.SFExpressClient;
import com.seeease.flywheel.web.common.express.client.SfExpressCancelOrderRequest;
import com.seeease.flywheel.web.common.express.client.SfExpressCreateOrderRequest;
import com.seeease.flywheel.web.common.express.client.SfExpressCreateOrderResult;
import com.seeease.flywheel.web.entity.ExpressOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/9/19
 */
@Component
public class SFExpressChannel implements ExpressChannel {

    @Resource
    private SFExpressClient client;

    @Override
    public ExpressChannelTypeEnum getChanelType() {
        return ExpressChannelTypeEnum.SF;
    }

    @Override
    public ExpressPlaceOrderResult placeOrder(ExpressPlaceOrder order) {
        //发件人信息
        ExpressPlaceOrder.ContactsInfo senderInfo = order.getSenderInfo();
        //收件人信息
        ExpressPlaceOrder.ContactsInfo receiverInfo = order.getReceiverInfo();

        List<SfExpressCreateOrderRequest.ContactInfoListDTO> contactInfoListDTOList = Arrays.asList(
                SfExpressCreateOrderRequest.ContactInfoListDTO.builder()
                        .company(senderInfo.getCompany())
                        .contactType(ContactType.SENDER.value)
                        .province(senderInfo.getProvince())
                        .city(senderInfo.getCity())
                        .county(senderInfo.getTown())
                        .address(Optional.ofNullable(senderInfo.getStreet()).orElse(StringUtils.EMPTY) + senderInfo.getAddressDetail())
                        .contact(senderInfo.getContactName())
                        .mobile(senderInfo.getContactTel())
                        .build(),
                SfExpressCreateOrderRequest.ContactInfoListDTO.builder()
                        .company(receiverInfo.getCompany())
                        .contactType(ContactType.RECEIVER.value)
                        .province(receiverInfo.getProvince())
                        .city(receiverInfo.getCity())
                        .county(receiverInfo.getTown())
                        .address(Optional.ofNullable(receiverInfo.getStreet()).orElse(StringUtils.EMPTY) + receiverInfo.getAddressDetail())
                        .contact(receiverInfo.getContactName())
                        .mobile(receiverInfo.getContactTel())
                        .build());


        SfExpressCreateOrderRequest request = SfExpressCreateOrderRequest.builder()
                .orderId(order.getBusinessNo())
                .language(FlywheelConstant.LANGUAGE)
                //订单货物总重量（郑州空港海关必填）， 若为子母件必填， 单位千克， 精确到小数点后3位，如果提供此值， 必须>0 (子母件需>6)
                .totalWeight(2.0)
                //包裹数，一个包裹对应一个运单号；若包裹数大于1，则返回一个母运单号和N-1个子运单号
                .parcelQty(1)
//                //付款方式，支持以下值： 1:寄方付 2:收方付 3:第三方付
//                .payMethod(1)
//                //快件自取，支持以下值： 1：客户同意快件自取 0：客户不同意快件自取
//                .isOneselfPickup(1)
                //扩展属性
                .extraInfoList(Arrays.asList())
                //2 顺丰标快 快件产品类别， 支持附录 《快件产品类别表》 的产品编码值，仅可使用与顺丰销售约定的快件产品
                .expressTypeId(2)
                .contactInfoList(contactInfoListDTOList)
                .cargoDetails(order.getOrderInfo().getGoodsInfoList()
                        .stream()
                        .map(t -> SfExpressCreateOrderRequest.CargoDetailsDTO.builder()
                                .name(StringUtils.join(t.getInfo()))
                                .build())
                        .collect(Collectors.toList())).build();

        SfExpressCreateOrderResult res = client.createOrder(request);
        return ExpressPlaceOrderResult.builder()
                .success(true)
                .businessNo(order.getBusinessNo())
                .orderNo(order.getOrderInfo().getOrderNo())
                .expressNumber(res.getMsgData().getWaybillNoInfoList().get(FlywheelConstant.INDEX).getWaybillNo())
                .build();
    }

    @Override
    public ExpressRecoveryOrderResult recoveryOrder(ExpressOrder order) {
        client.cancelOrder(SfExpressCancelOrderRequest.builder()
                .language(FlywheelConstant.LANGUAGE)
                .dealType(2)
                .orderId(order.getSonSerialNo())
                .totalWeight(2)
                .waybillNoInfoList(Lists.newArrayList(order.getExpressNo()))
                .build());
        return ExpressRecoveryOrderResult.builder()
                .success(true)
                .build();
    }

    @Getter
    @AllArgsConstructor
    enum ContactType {
        /**
         * 发件方
         */
        SENDER(1),
        /**
         * 收件方
         */
        RECEIVER(2),
        ;
        int value;
    }


}