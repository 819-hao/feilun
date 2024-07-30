package com.seeease.flywheel.web.common.express.channel;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.doudian.open.api.logistics_cancelOrder.LogisticsCancelOrderRequest;
import com.doudian.open.api.logistics_cancelOrder.LogisticsCancelOrderResponse;
import com.doudian.open.api.logistics_cancelOrder.param.LogisticsCancelOrderParam;
import com.kuaishou.merchant.open.api.KsMerchantApiException;
import com.kuaishou.merchant.open.api.client.AccessTokenKsMerchantClient;
import com.kuaishou.merchant.open.api.domain.express.AddressDTO;
import com.kuaishou.merchant.open.api.domain.express.Contract;
import com.kuaishou.merchant.open.api.domain.express.GetEbillOrderRequest;
import com.kuaishou.merchant.open.api.domain.express.ItemDTO;
import com.kuaishou.merchant.open.api.request.express.OpenExpressEbillGetRequest;
import com.kuaishou.merchant.open.api.response.express.OpenExpressEbillGetResponse;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.web.common.context.DouYinConfig;
import com.seeease.flywheel.web.common.context.KuaiShouConfig;
import com.seeease.flywheel.web.entity.ExpressOrder;
import com.seeease.flywheel.web.entity.KuaishouAppInfo;
import com.seeease.flywheel.web.infrastructure.service.KuaishouAppInfoService;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.exception.e.OperationRejectedExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/9/19
 */
@Slf4j
@Component
public class KuaiShouSFExpressChannel implements ExpressChannel {

    /**
     * 国家编码（默认CHN，目前只有国内业务）
     */
    private static final String COUNTRY_CODE = "CHN";
    /**
     * 物流服务商编码
     */
    private static final String LOGISTICS_CODE = "SF";

    @Resource
    private KuaishouAppInfoService kuaishouAppInfoService;


    @Override
    public ExpressChannelTypeEnum getChanelType() {
        return ExpressChannelTypeEnum.KS_SF;
    }

    @Override
    public ExpressPlaceOrderResult placeOrder(ExpressPlaceOrder order) {

        KuaishouAppInfo kuaishouAppInfo = kuaishouAppInfoService.list(Wrappers.<KuaishouAppInfo>lambdaQuery()
                .eq(KuaishouAppInfo::getOpenShopId, order.getOrderInfo().getKuaiShouShopId())
        ).stream().findFirst().orElse(null);

        Optional.ofNullable(kuaishouAppInfo).orElseThrow(() -> new OperationRejectedException((OperationRejectedExceptionCode) () -> "快手对应的配置项不存在"));

        AccessTokenKsMerchantClient client = new AccessTokenKsMerchantClient(kuaishouAppInfo.getAppId(), kuaishouAppInfo.getSignSecret());

        //请求下单接口
        OpenExpressEbillGetRequest request = new OpenExpressEbillGetRequest();
        request.setAccessToken(KuaiShouConfig.getAccessToken(Arrays.asList(kuaishouAppInfo.getAppId(), order.getSellerOpenId()).stream().collect(Collectors.joining(":"))));

        GetEbillOrderRequest getEbillOrderRequest = new GetEbillOrderRequest();
        getEbillOrderRequest.setMerchantCode(order.getSellerOpenId());
        getEbillOrderRequest.setMerchantName(order.getKuaiShouShopName());
        //包裹 == 订单号
        getEbillOrderRequest.setPackageCode(order.getOrderInfo().getKuaiShouOrderId());

        getEbillOrderRequest.setTotalPackageQuantity(1L);

        ItemDTO itemDTO = new ItemDTO();
        //
        itemDTO.setItemTitle(order.getItemTitle());
        itemDTO.setItemQuantity(1L);

        getEbillOrderRequest.setItemList(Arrays.asList(itemDTO));

        //收件人
        Contract receiverContract = new Contract();
        ExpressPlaceOrder.ContactsInfo receiverInfo = order.getReceiverInfo();
        receiverContract.setName(receiverInfo.getContactName());
        receiverContract.setMobile(receiverInfo.getContactTel());
        getEbillOrderRequest.setReceiverContract(receiverContract);

        //发件人
        Contract senderContract = new Contract();
        ExpressPlaceOrder.ContactsInfo senderInfo = order.getSenderInfo();
        senderContract.setName(senderInfo.getContactName());
        senderContract.setMobile(senderInfo.getContactTel());
        getEbillOrderRequest.setSenderContract(senderContract);

        //收货地址
        AddressDTO receiverAddress = new AddressDTO();
        receiverAddress.setProvinceName(receiverInfo.getProvince());
        receiverAddress.setCityName(receiverInfo.getCity());
        receiverAddress.setDistrictName(receiverInfo.getTown());

        if (StringUtils.isNotBlank(receiverInfo.getStreet())) {
            receiverAddress.setStreetName(receiverInfo.getStreet());
        }

        receiverAddress.setDetailAddress(receiverInfo.getAddressDetail());

        getEbillOrderRequest.setReceiverAddress(receiverAddress);

        //发货地址
        AddressDTO senderAddress = new AddressDTO();
        senderAddress.setProvinceName(senderInfo.getProvince());
        senderAddress.setCityName(senderInfo.getCity());
        senderAddress.setDistrictName(senderInfo.getTown());
        senderAddress.setStreetName(senderInfo.getStreet());
        senderAddress.setDetailAddress(senderInfo.getAddressDetail());
        getEbillOrderRequest.setSenderAddress(senderAddress);


        getEbillOrderRequest.setExpressCompanyCode(LOGISTICS_CODE);
        getEbillOrderRequest.setOrderChannel("KUAI_SHOU");
        //https://docs.qingque.cn/d/home/eZQCstmJ4XYNo4WQdS1sPlw9E?identityId=1oEFwmDizx5#section=h.kb2i44jbl6if
        //	○ settleAccount（客户编码）：
        //		■ 必传，和商家订购服务 中的保持一致，本质为月结卡号；
        getEbillOrderRequest.setSettleAccount("5717175232");

        getEbillOrderRequest.setTradeOrderCode(order.getOrderInfo().getKuaiShouOrderId());

        getEbillOrderRequest.setExtData(JSONObject.toJSONString(new HashMap<String, String>() {
            {
                put("isvClientCode", "XYWLKPnPx_WDKJ");
            }
        }));

        getEbillOrderRequest.setPayMethod(1);
        getEbillOrderRequest.setExpressProductCode(order.getSfProductCode().getValue());

        getEbillOrderRequest.setRequestId(order.getRequestID());

        request.setGetEbillOrderRequest(Arrays.asList(getEbillOrderRequest));
        OpenExpressEbillGetResponse response = null;

        log.info("商家ERP/ISV 向快手电子面单系统获取单号和打印信息:{}", JSONObject.toJSONString(request.getGetEbillOrderRequest()));
        try {
            response = client.execute(request);
        } catch (KsMerchantApiException e) {
            log.error(e.getErrorMsg(), e);
        }

        Optional.ofNullable(response).orElseThrow(() -> new OperationRejectedException((OperationRejectedExceptionCode) () -> "快手下顺丰单失败"));
        log.info("商家ERP/ISV 向快手电子面单系统获取单号和打印信息:{}", JSONObject.toJSONString(response));
        log.info("商家ERP/ISV 向快手电子面单系统获取单号和打印信息:{}", JSONObject.toJSONString(response.getData()));

        if (response.getResult() == 1) {
            return ExpressPlaceOrderResult.builder()
                    .success(true)
                    .businessNo(order.getBusinessNo())
                    .orderNo(order.getOrderInfo().getOrderNo())
                    .expressNumber(response.getData().get(FlywheelConstant.INDEX).getData().get(FlywheelConstant.INDEX).getWaybillCode())
                    .build();
        }

        return ExpressPlaceOrderResult.builder()
                .success(false)
                .businessNo(order.getBusinessNo())
                .orderNo(order.getOrderInfo().getOrderNo())
                .errMsg(response.getErrorMsg())
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

    //        //重复下单可以补打面单


//    //补打测试 只支持加盟快递型更新电子面单
//    OpenExpressEbillUpdateRequest request = new OpenExpressEbillUpdateRequest();
//        request.setAccessToken("ChFvYXV0aC5hY2Nlc3NUb2tlbhJw_UX1UInIUCg8eNbuNMkBh6uA86Q4_uPm2zNP8WeVvJR0nH9tIAHxVcsrc8iiL2MI02AbslqbWhrU_rpO1H1xHmTdiSFFYgLuLOQRj15phxng03mjh9nwA8mk0Rhb7M4f0trJJuaF4g-VrRDRmgKDzhoSNobUQXjSSbKFNqfffV1RYgsfIiD9eE2T0o73UlxRyrT4yVq-KUIOLHFMvo_0O-Uag4PLxCgFMAE");
//
//        request.setWaybillCode("SF1695881783482");
//        request.setExpressCompanyCode("SF");
//
//    OpenExpressEbillUpdateResponse response = client.execute(request);
//
//        System.out.println(JSONObject.toJSONString(response));
//
//        System.out.println(JSONObject.toJSONString(response.getData()));
}
