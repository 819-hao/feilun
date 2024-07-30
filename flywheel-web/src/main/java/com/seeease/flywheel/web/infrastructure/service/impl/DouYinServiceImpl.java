package com.seeease.flywheel.web.infrastructure.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.doudian.open.api.order_batchDecrypt.OrderBatchDecryptRequest;
import com.doudian.open.api.order_batchDecrypt.OrderBatchDecryptResponse;
import com.doudian.open.api.order_batchDecrypt.data.DecryptInfosItem;
import com.doudian.open.api.order_batchDecrypt.data.OrderBatchDecryptData;
import com.doudian.open.api.order_batchDecrypt.param.CipherInfosItem;
import com.doudian.open.api.order_batchDecrypt.param.OrderBatchDecryptParam;
import com.doudian.open.core.DoudianOpResponse;
import com.google.common.collect.Lists;
import com.seeease.flywheel.customer.ICustomerFacade;
import com.seeease.flywheel.customer.request.CustomerUpdateRequest;
import com.seeease.flywheel.maindata.IUserFacade;
import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.request.SaleOrderCancelRequest;
import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.sale.request.SaleOrderDetailsRequest;
import com.seeease.flywheel.sale.request.SaleReturnOrderCreateRequest;
import com.seeease.flywheel.sale.result.SaleOrderCreateResult;
import com.seeease.flywheel.sale.result.SaleOrderDetailsResult;
import com.seeease.flywheel.storework.IWmsWorkInterceptFacade;
import com.seeease.flywheel.storework.request.WmsWorkInterceptRequest;
import com.seeease.flywheel.storework.result.WmsWorkInterceptResult;
import com.seeease.flywheel.web.common.context.DouYinConfig;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.executor.CancelCmdExe;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.entity.DouYinOrder;
import com.seeease.flywheel.web.entity.DouYinOrderLine;
import com.seeease.flywheel.web.entity.DouYinOrderRefund;
import com.seeease.flywheel.web.entity.DouYinShopMapping;
import com.seeease.flywheel.web.entity.douyin.DouYinRefundCreatedData;
import com.seeease.flywheel.web.entity.request.DouYinCustomerDecryptionRequest;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.flywheel.web.infrastructure.mapper.DouYinOrderMapper;
import com.seeease.flywheel.web.infrastructure.service.DouYinService;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.utils.BigDecimalUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author Tiro
 * @date 2023/4/27
 */
@Service
@Slf4j
public class DouYinServiceImpl implements DouYinService {
    @Resource
    private CreateCmdExe createCmdExe;
    @Resource
    private CancelCmdExe cancelCmdExe;

    @Resource
    private DouYinOrderMapper douYinOrderMapper;

    @DubboReference(check = false, version = "1.0.0")
    private ISaleOrderFacade saleOrderFacade;

    @DubboReference(check = false, version = "1.0.0")
    private ICustomerFacade customerFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IUserFacade userFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IWmsWorkInterceptFacade wmsWorkInterceptFacade;

    /**
     * 抖音订单创建
     *
     * @param douYinOrder
     * @param lineList
     * @param shopMapping
     * @return
     */
    @Override
    public String create(DouYinOrder douYinOrder, List<DouYinOrderLine> lineList, DouYinShopMapping shopMapping) {
        List<String> ownerList = Optional.ofNullable(shopMapping.getOrderOwner())
                .map(t -> Arrays.asList(t.split(",")))
                .orElse(null);
        //创建人默认取第一个所有者
        SaleOrderCreateRequest.PrescriptiveCreator create = Optional.ofNullable(ownerList)
                .map(userFacade::listUser)
                .map(t -> t.stream()
                        .sorted(Comparator.comparing(u -> ownerList.indexOf(u.getUserid())))
                        .findFirst()
                        .orElse(null))
                .map(t -> SaleOrderCreateRequest.PrescriptiveCreator.builder()
                        .createdId(t.getId())
                        .createdBy(t.getName())
                        .build())
                .orElse(null);

        //创建飞轮订单
        SaleOrderCreateRequest saleOrderCreateRequest = SaleOrderCreateRequest.builder()
                .bizOrderCode(douYinOrder.getOrderId())
                .saleType(SaleOrderTypeEnum.TO_C_XS.value)
                .saleMode(SaleOrderModeEnum.ON_LINE.value)
                .saleChannel(SaleOrderChannelEnum.DOU_YIN.value)
                .receiverInfo(SaleOrderCreateRequest.ReceiverInfo.builder()
                        .receiverName(Optional.ofNullable(douYinOrder.getDecryptPostReceiver()).orElse(douYinOrder.getMaskPostReceiver()))
                        .receiverMobile(Optional.ofNullable(douYinOrder.getDecryptPostTel()).orElse(douYinOrder.getMaskPostTel()))
                        .receiverAddress(Optional.ofNullable(douYinOrder.getDecryptAddrDetail()).orElse(douYinOrder.getMaskDetail()))
                        .build())
                // 0：货到付款，1：微信，2：支付宝，4：银行卡，5：抖音零钱，7：无需支付，8：DOU分期，9：新卡支付，12：先用后付，13：组合支付。
                .paymentMethod(SaleOrderPaymentMethodEnum.DOU_YIN.value)
                .shopId(shopMapping.getShopId())
                .creator(create)
                .details(lineList
                        .stream()
                        .filter(t -> BigDecimalUtil.gtZero(t.getOrderAmount()))
                        .map(t -> {
                            List<String> goodsModel = t.getGoodsModel();
                            if (CollectionUtils.isEmpty(goodsModel)) {
                                throw new RuntimeException("商品不存在");
                            }
                            BigDecimal subOrderAmount = t.getOrderAmount()
                                    .divide(new BigDecimal(goodsModel.size() * t.getItemNum()))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP);

                            List<SaleOrderCreateRequest.BillSaleOrderLineDto> res = goodsModel.stream()
                                    .map(model -> LongStream.range(0, t.getItemNum())
                                            .mapToObj(num -> SaleOrderCreateRequest.BillSaleOrderLineDto
                                                    .builder()
                                                    .subOrderCode(t.getDouYinSubOrderId())
                                                    .model(model)
                                                    .clinchPrice(subOrderAmount)
                                                    .build())
                                            .collect(Collectors.toList())
                                    ).flatMap(Collection::stream)
                                    .collect(Collectors.toList());

                            res.get(0).setClinchPrice(t.getOrderAmount()
                                    .subtract(res.stream()
                                            .skip(1)
                                            .map(SaleOrderCreateRequest.BillSaleOrderLineDto::getClinchPrice)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add)));
                            return res;
                        })
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .build();

        //组装创建命令
        CreateCmd<SaleOrderCreateRequest> cmd = new CreateCmd<>();
        cmd.setBizCode(BizCode.SALE);
        cmd.setUseCase(UseCase.PROCESS_CREATE);
        cmd.setRequest(saleOrderCreateRequest);

        //创建销售单
        SaleOrderCreateResult res = (SaleOrderCreateResult) createCmdExe.create(cmd);
        log.info("[飞轮抖音订单完成: order={}| cmd={} | res={}]", JSONObject.toJSONString(douYinOrder), JSONObject.toJSON(cmd), JSONObject.toJSONString(res));
        return res.getOrders().get(0).getSerialNo();
    }

    /**
     * 抖音订单取消
     *
     * @param douYinOrder
     */
    @Override
    public void cancelOrder(DouYinOrder douYinOrder) {
        //查销售单
        SaleOrderDetailsRequest detailsRequest = SaleOrderDetailsRequest.builder()
                .serialNo(douYinOrder.getSerialNo())
                .bizOrderCode(douYinOrder.getOrderId())
                .build();
        SaleOrderDetailsResult details = saleOrderFacade.details(detailsRequest);
        if (Objects.isNull(details)) {
            return;
        }
        switch (TMallServiceImpl.SaleOrderStateEnum.fromCode(details.getSaleState())) {
            case CANCEL_WHOLE:
                log.info("[飞轮抖音订单已取消: order={}]", JSONObject.toJSONString(douYinOrder));
                return;
            case UN_CONFIRMED:
            case UN_STARTED:
                SaleOrderCancelRequest request = SaleOrderCancelRequest.builder()
                        .bizOrderCode(douYinOrder.getOrderId())
                        .build();

                CancelCmd<SaleOrderCancelRequest> cmd = new CancelCmd<>();
                cmd.setBizCode(BizCode.SALE);
                cmd.setUseCase(UseCase.CANCEL);
                cmd.setRequest(request);

                Object res = cancelCmdExe.cancel(cmd);
                log.info("[飞轮抖音订单取消: order={}| cmd={} | res={}]", JSONObject.toJSONString(douYinOrder), JSONObject.toJSON(cmd), JSONObject.toJSONString(res));
                break;

            case COMPLETE:
                //创建销退单
                Object reverseOrderRes = reverseOrder(details, douYinOrder.getOrderId(), details.getLines()
                        .stream()
                        .map(SaleOrderDetailsResult.SaleOrderLineVO::getSubOrderCode)
                        .collect(Collectors.toList()));
                log.info("[飞轮抖音订单销退订单创建: order={} | res={}]", JSONObject.toJSONString(douYinOrder), JSONObject.toJSONString(reverseOrderRes));
                break;

            default:
                throw new RuntimeException("进行中无法取消");
        }
    }


    /**
     * 抖音订单退货
     *
     * @param douYinOrderRefund
     */
    @Override
    public String refundOrder(DouYinOrderRefund douYinOrderRefund, DouYinOrder order) {
        //查销售单
        SaleOrderDetailsRequest detailsRequest = SaleOrderDetailsRequest.builder()
                .serialNo(order.getSerialNo())
                .bizOrderCode(douYinOrderRefund.getOrderId())
                .build();
        SaleOrderDetailsResult details = saleOrderFacade.details(detailsRequest);

        //创建销退单
        Object res = reverseOrder(details, douYinOrderRefund.getRefundOrderId(), Lists.newArrayList(douYinOrderRefund.getOrderSubId()));
        log.info("[抖音销退订单创建: douYinOrderRefund={} | res={}]", JSONObject.toJSONString(douYinOrderRefund), JSONObject.toJSONString(res));
        return null;
    }


    /**
     * 销退订单
     *
     * @param details
     * @param returnBizOrderCode
     * @param subOrderCodeList
     * @return
     */
    private Object reverseOrder(SaleOrderDetailsResult details, String returnBizOrderCode, List<String> subOrderCodeList) {
        SaleReturnOrderCreateRequest request = SaleReturnOrderCreateRequest.builder()
                .saleReturnType(details.getSaleType())
                .bizOrderCode(returnBizOrderCode)
                .creator(SaleReturnOrderCreateRequest.PrescriptiveCreator.builder()
                        .createdId(details.getCreatedId())
                        .createdBy(details.getCreatedBy())
                        .build())
                .shopId(details.getShopId())
                .details(details.getLines()
                        .stream()
                        .filter(t -> subOrderCodeList.contains(t.getSubOrderCode()))
                        .map(t -> SaleReturnOrderCreateRequest.BillSaleReturnOrderLineDto.builder()
                                .saleId(details.getId())
                                .saleLineId(t.getId())
                                .returnPrice(t.getClinchPrice())
                                .build()
                        )
                        .collect(Collectors.toList())
                ).build();

        //组装创建命令
        CreateCmd<SaleReturnOrderCreateRequest> cmd = new CreateCmd<>();
        cmd.setBizCode(BizCode.TO_C_SALE_RETURN);
        cmd.setUseCase(UseCase.PROCESS_CREATE);
        cmd.setRequest(request);

        //创建销退单
        return createCmdExe.create(cmd);
    }


    @Override
    public void customerDecryption(DouYinCustomerDecryptionRequest request) {
        DouYinOrder order = douYinOrderMapper.selectOne(Wrappers.<DouYinOrder>lambdaQuery()
                .eq(DouYinOrder::getOrderId, request.getDouYinOrderId()));
        if (Objects.isNull(order)) {
            return;
        }
        if (Objects.nonNull(order.getDecryptPostTel())
                && Objects.nonNull(order.getDecryptPostTel())
                && Objects.nonNull(order.getDecryptPostTel())) {
            return;
        }
        //解密订单密文
        CipherInfosItem postTel = new CipherInfosItem();
        postTel.setAuthId(order.getOrderId());
        postTel.setCipherText(order.getEncryptPostTel());

        CipherInfosItem postReceiver = new CipherInfosItem();
        postReceiver.setAuthId(order.getOrderId());
        postReceiver.setCipherText(order.getEncryptPostReceiver());

        CipherInfosItem detail = new CipherInfosItem();
        detail.setAuthId(order.getOrderId());
        detail.setCipherText(order.getEncryptDetail());

        //解密订单密文
        OrderBatchDecryptRequest decryptRequest = new OrderBatchDecryptRequest();
        OrderBatchDecryptParam decryptParam = decryptRequest.getParam();
        decryptParam.setCipherInfos(Arrays.asList(postTel, postReceiver, detail));

        OrderBatchDecryptResponse decryptResponse = decryptRequest.execute(DouYinConfig.getAccessToken(order.getDouYinShopId()));

        if (!Optional.ofNullable(decryptResponse)
                .map(DoudianOpResponse::getData)
                .map(OrderBatchDecryptData::getDecryptInfos)
                .isPresent()) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.DECRYPTION_FAILED, "抖音接口异常");
        }
        //解密校验
        Optional.ofNullable(decryptResponse.getData())
                .map(OrderBatchDecryptData::getDecryptInfos)
                .map(t -> t.stream().map(DecryptInfosItem::getErrMsg)
                        .filter(StringUtils::isNotBlank)
                        .findFirst()
                        .orElse(null))
                .ifPresent(msg -> {
                    throw new OperationRejectedException(OperationExceptionCodeEnum.DECRYPTION_FAILED, msg);
                });

        Map<String, String> decryptMap = decryptResponse.getData()
                .getDecryptInfos()
                .stream()
                .collect(Collectors.toMap(DecryptInfosItem::getCipherText, DecryptInfosItem::getDecryptText));

        //客户信息
        String customerName = decryptMap.get(order.getEncryptPostReceiver());
        String phone = decryptMap.get(order.getEncryptPostTel());
        String address = order.getEncryptAddrArea() + decryptMap.get(order.getEncryptDetail());

        //更新订单
        DouYinOrder upOrder = new DouYinOrder();
        upOrder.setId(order.getId());
        upOrder.setDecryptPostTel(phone);
        upOrder.setDecryptAddrDetail(address);
        upOrder.setDecryptPostReceiver(customerName);
        douYinOrderMapper.updateById(order);

        //更新客户信息
        customerFacade.update(CustomerUpdateRequest.builder()
                .customerId(request.getCustomerId())
                .customerContactsId(request.getCustomerContactsId())
                .customerName(customerName)
                .name(customerName)
                .phone(phone)
                .address(address)
                .build()
        );
    }

    @Override
    public void updateCustomerInfo(DouYinOrder douYinOrder) {
        //查销售单
        SaleOrderDetailsRequest detailsRequest = SaleOrderDetailsRequest.builder()
                .serialNo(douYinOrder.getSerialNo())
                .bizOrderCode(douYinOrder.getOrderId())
                .build();
        SaleOrderDetailsResult details = saleOrderFacade.details(detailsRequest);
        if (Objects.isNull(details)) {
            return;
        }

        //更新客户信息
        customerFacade.update(CustomerUpdateRequest.builder()
                .customerId(details.getCustomerId())
                .customerContactsId(details.getCustomerContactId())
                .customerName(Optional.ofNullable(douYinOrder.getDecryptPostReceiver()).orElse(douYinOrder.getMaskPostReceiver()))
                .name(Optional.ofNullable(douYinOrder.getDecryptPostReceiver()).orElse(douYinOrder.getMaskPostReceiver()))
                .phone(Optional.ofNullable(douYinOrder.getDecryptPostTel()).orElse(douYinOrder.getMaskPostTel()))
                .address(Optional.ofNullable(douYinOrder.getDecryptAddrDetail())
                        .map(t -> StringUtils.join(Arrays.asList(douYinOrder.getProvince(), douYinOrder.getCity(), douYinOrder.getTown(), douYinOrder.getStreet() + t), "/"))
                        .orElse(douYinOrder.getMaskDetail()))
                .build());
    }

    @Getter
    @AllArgsConstructor
    enum SaleOrderChannelEnum {
        DOU_YIN(3, "抖音"),
        ;
        private Integer value;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum SaleOrderPaymentMethodEnum {
        DOU_YIN(2, "抖音"),
        ;
        private Integer value;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum SaleOrderModeEnum {
        ON_LINE(5, "平台"),
        ;
        private Integer value;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum SaleOrderTypeEnum {
        TO_B_JS(1, "同行寄售"),
        TO_C_XS(2, "个人销售"),
        ;
        private Integer value;
        private String desc;
    }

    @Override
    public void saleIntercept(DouYinRefundCreatedData request) {
        try {
            DouYinOrder order = douYinOrderMapper.selectOne(Wrappers.<DouYinOrder>lambdaQuery()
                    .eq(DouYinOrder::getOrderId, request.getPId().toString()));
            if (Objects.isNull(order)) {
                return;
            }
            WmsWorkInterceptResult res = wmsWorkInterceptFacade.intercept(WmsWorkInterceptRequest.builder()
                    .intercept(request.isSaleIntercept())
                    .bizOrderCode(request.getPId().toString())
                    .build());
            log.info("抖音发货拦截成功：{},{}", JSONObject.toJSONString(request), JSONObject.toJSON(res));
        } catch (Exception e) {
            log.error("抖音发货拦截异常：{}", e.getMessage(), e);
        }
    }

}
