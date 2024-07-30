package com.seeease.flywheel.web.infrastructure.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.request.SaleOrderCancelRequest;
import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.sale.request.SaleOrderDetailsRequest;
import com.seeease.flywheel.sale.request.SaleReturnOrderCreateRequest;
import com.seeease.flywheel.sale.result.SaleOrderDetailsResult;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.executor.CancelCmdExe;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.entity.ThirdPartyCommodityRelationship;
import com.seeease.flywheel.web.entity.tmall.*;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.flywheel.web.infrastructure.mapper.ThirdPartyCommodityRelationshipMapper;
import com.seeease.flywheel.web.infrastructure.service.TMallService;
import com.seeease.springframework.utils.BigDecimalUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/3/24
 */
@Slf4j
@Service
public class TMallServiceImpl implements TMallService {
    @Resource
    private ThirdPartyCommodityRelationshipMapper thirdPartyCommodityRelationshipMapper;
    @Resource
    private CreateCmdExe createCmdExe;
    @Resource
    private CancelCmdExe cancelCmdExe;

    @DubboReference(check = false, version = "1.0.0")
    private ISaleOrderFacade saleOrderFacade;

    private static final Integer TIME_SHOP_ID = 17;

    /**
     * 创建订单
     *
     * @param order
     */
    @Override
    public void createOrder(TMallConsignOrderNotify order) {
        TMallReceiverInfo receiverInfo = Objects.requireNonNull(order.getReceiverInfo());

        Map<String, ThirdPartyCommodityRelationship> commodityMap = thirdPartyCommodityRelationshipMapper.selectList(Wrappers.<ThirdPartyCommodityRelationship>lambdaQuery()
                        .in(ThirdPartyCommodityRelationship::getThirdPartyGoodsId, order.getOrderItems().getOrderItem()
                                .stream().map(TMallOrderItems.OrderItem::getScItemId).collect(Collectors.toList())))
                .stream()
                .collect(Collectors.toMap(ThirdPartyCommodityRelationship::getThirdPartyGoodsId, Function.identity()));

        //组装创建参数
        SaleOrderCreateRequest request = SaleOrderCreateRequest.builder()
                .bizOrderCode(order.getBizOrderCode())
                .saleType(SaleOrderTypeEnum.TO_C_XS.value)
                .saleMode(SaleOrderModeEnum.ON_LINE.value)
                .saleChannel(SaleOrderChannelEnum.T_MALL.value)
                .receiverInfo(SaleOrderCreateRequest.ReceiverInfo.builder()
                        .receiverName(receiverInfo.getReceiverName())
                        .receiverMobile(receiverInfo.getReceiverMobile())
                        .receiverAddress(StringUtils.defaultString(receiverInfo.getReceiverProvince()) // 省
                                + StringUtils.defaultString(receiverInfo.getReceiverCity()) //市
                                + StringUtils.defaultString(receiverInfo.getReceiverArea()) // 区
                                + StringUtils.defaultString(receiverInfo.getReceiveTown()) //街道
                                + StringUtils.defaultString(receiverInfo.getReceiverAddress()) //地址
                        )
                        .build())
                .paymentMethod(SaleOrderPaymentMethodEnum.ALIPAY.value)
                .owner("WangYun")
                .shopId(TIME_SHOP_ID) // 天猫下单门店为天猫门店
                .details(order.getOrderItems()
                        .getOrderItem()
                        .stream()
                        .map(t -> {
                            ThirdPartyCommodityRelationship commodity = commodityMap.get(t.getScItemId());
                            if (Objects.isNull(commodity)) {
                                throw new RuntimeException("商品不存在");
                            }
                            return SaleOrderCreateRequest.BillSaleOrderLineDto
                                    .builder()
                                    .model(commodity.getSnModelStatus() == 0 ? commodity.getGoodsWatchModel() : null)
                                    .stockSn(commodity.getSnModelStatus() == 1 ? commodity.getGoodsWatchModel() : null)
                                    .clinchPrice(BigDecimalUtil.centToYuan(t.getItemAmount()))
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .build();

        //组装创建命令
        CreateCmd<SaleOrderCreateRequest> cmd = new CreateCmd<>();
        cmd.setBizCode(BizCode.SALE);
        cmd.setUseCase(UseCase.PROCESS_CREATE);
        cmd.setRequest(request);

        //创建销售单
        Object res = createCmdExe.create(cmd);
        log.info("[天猫订单创建: order={}| cmd={} | res={}]", JSONObject.toJSONString(order), JSONObject.toJSON(cmd), JSONObject.toJSONString(res));
    }

    /**
     * 取消订单
     *
     * @param order
     */
    @Override
    public void cancelOrder(TMallConsignOrderCancel order) {
        //查销售单
        SaleOrderDetailsRequest detailsRequest = SaleOrderDetailsRequest.builder()
                .bizOrderCode(order.getBizOrderCode())
                .build();
        SaleOrderDetailsResult details = saleOrderFacade.details(detailsRequest);

        switch (SaleOrderStateEnum.fromCode(details.getSaleState())) {
            case CANCEL_WHOLE:
                log.info("[天猫订单已取消: order={}]", JSONObject.toJSONString(order));
                return;
            case UN_CONFIRMED:
            case UN_STARTED:
                SaleOrderCancelRequest request = SaleOrderCancelRequest.builder()
                        .bizOrderCode(order.getBizOrderCode())
                        .build();

                CancelCmd<SaleOrderCancelRequest> cmd = new CancelCmd<>();
                cmd.setBizCode(BizCode.SALE);
                cmd.setUseCase(UseCase.CANCEL);
                cmd.setRequest(request);

                Object res = cancelCmdExe.cancel(cmd);
                log.info("[天猫订单取消: order={}| cmd={} | res={}]", JSONObject.toJSONString(order), JSONObject.toJSON(cmd), JSONObject.toJSONString(res));
                break;

            case COMPLETE:
                //创建销退单
                Object reverseOrderRes = reverseOrder(details, order.getBizOrderCode());
                log.info("[天猫销退订单创建: order={} | res={}]", JSONObject.toJSONString(order), JSONObject.toJSONString(reverseOrderRes));
                break;

            default:
                throw new RuntimeException("进行中无法取消");
        }
    }

    /**
     * 销退订单
     *
     * @param order
     */
    @Override
    public void reverseOrder(TMallReverseOrderInStorageNotify order) {
        //查销售单
        SaleOrderDetailsRequest detailsRequest = SaleOrderDetailsRequest.builder()
                .bizOrderCode(order.getForwardOrderCode())
                .build();
        SaleOrderDetailsResult details = saleOrderFacade.details(detailsRequest);

        //创建销退单
        Object res = reverseOrder(details, order.getBizOrderCode());
        log.info("[天猫销退订单创建: order={} | res={}]", JSONObject.toJSONString(order), JSONObject.toJSONString(res));
    }

    /**
     * 退货
     *
     * @param details
     * @param returnBizOrderCode
     * @return
     */
    private Object reverseOrder(SaleOrderDetailsResult details, String returnBizOrderCode) {

        SaleReturnOrderCreateRequest request = SaleReturnOrderCreateRequest.builder()
                .saleReturnType(details.getSaleType())
                .bizOrderCode(returnBizOrderCode)
                .shopId(TIME_SHOP_ID)
                .details(details.getLines().stream()
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


    @Getter
    @AllArgsConstructor
    enum SaleOrderChannelEnum {
        T_MALL(2, "天猫国际"),
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

    @Getter
    @AllArgsConstructor
    enum SaleOrderPaymentMethodEnum {
        ALIPAY(3, "支付宝"),
        ;
        private Integer value;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    public enum SaleOrderStateEnum {
        UN_CONFIRMED(0, "待确认"),
        UN_STARTED(1, "待开始"),
        UNDER_WAY(2, "进行中"),
        COMPLETE(4, "已完成"),
        CANCEL_WHOLE(3, "全部取消"),
        ;
        private Integer value;
        private String desc;

        public static SaleOrderStateEnum fromCode(int value) {
            return Arrays.stream(SaleOrderStateEnum.values())
                    .filter(t -> value == t.getValue())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("枚举异常"));
        }
    }
}