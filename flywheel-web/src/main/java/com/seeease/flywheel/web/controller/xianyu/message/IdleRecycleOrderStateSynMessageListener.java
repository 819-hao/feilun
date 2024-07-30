package com.seeease.flywheel.web.controller.xianyu.message;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.controller.xianyu.FlywheelXianYuClient;
import com.seeease.flywheel.web.controller.xianyu.enums.XianYuMessageTopicEnum;
import com.seeease.flywheel.web.controller.xianyu.enums.XianYuOrderStatusEnum;
import com.seeease.flywheel.web.controller.xianyu.request.IdleRecycleOrderStateSynRequest;
import com.seeease.flywheel.web.entity.XyRecycleOrder;
import com.seeease.flywheel.web.entity.enums.XyRecycleOrderStateEnum;
import com.seeease.flywheel.web.infrastructure.service.XyRecycleOrderService;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.utils.BigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Tiro
 * @date 2023/10/16
 */
@Slf4j
@Component
public class IdleRecycleOrderStateSynMessageListener implements MessageListener {
    @Resource
    private XyRecycleOrderService recycleOrderService;
    @Resource
    private FlywheelXianYuClient flywheelXianYuClient;

    @Override
    public XianYuMessageTopicEnum getTopic() {
        return XianYuMessageTopicEnum.IDLE_RECYCLE_ORDER_STATE_SYN;
    }

    @Override
    public void handle(String content) throws RuntimeException {
        IdleRecycleOrderStateSynRequest request = JSONObject.parseObject(content, IdleRecycleOrderStateSynRequest.class);
        switch (XianYuOrderStatusEnum.findByCode(request.getOrder_status())) {
            case CREATE_DONE:
                this.createOrder(request);
                break;
            case CHANGE_ADDRESS_11:
                this.changeAddress(request);
                break;
            case APPLY_REFUND_GOODS:
                this.applyRefundGoods(request);
                break;
            case SELLER_APPLY_SECOND_CHECK_3_31:
                this.secondCheck31(request);
                break;
            case SELLER_CANCLE_ORDER:
            case PACKED_TIME_OUT_107:
                this.cancelOrder(request);
                break;
            case SELLER_ORDER_CONFIRMED:
                this.sellerOrderConfirmed(request);
                break;
            case SELLER_ORDER_RATED:
                this.sellerOrderRated(request);
                break;

        }
    }

    /**
     * 创建闲鱼回收单
     *
     * @param request
     */
    public void createOrder(IdleRecycleOrderStateSynRequest request) {
        log.info("闲鱼订单-创建:{}", JSONObject.toJSONString(request));

        XyRecycleOrder quoteOrder = Optional.ofNullable(recycleOrderService.getByQuoteId(request.getApprize_id()))
                .orElseThrow(() -> new OperationRejectedException(OperationExceptionCodeEnum.QUOTE_RECORD_NOT_EXIST));

        XyRecycleOrder order = new XyRecycleOrder();
        order.setId(quoteOrder.getId());
        order.setBizOrderId(request.getBiz_order_id());
        order.setApprizeAmount(BigDecimalUtil.centToYuan(request.getApprize_amount()));
        order.setOrderStatus(request.getOrder_status());
        order.setPlaceOrderTime(Objects.isNull(quoteOrder.getPlaceOrderTime()) ? new Date() : null);

        order.setShipType(request.getShip_type());
        order.setSellerNick(request.getSeller_nick());
        order.setSellerRealName(request.getSeller_real_name());
        order.setSellerPhone(request.getSeller_phone());
        order.setSellerAlipayUserId(request.getSeller_alipay_user_id());

        order.setShipTime(request.getShip_time());
        order.setProvince(request.getProvince());
        order.setCity(request.getCity());
        order.setArea(request.getArea());
        order.setSellerAddress(request.getSeller_address());

        recycleOrderService.updateState(order, XyRecycleOrderStateEnum.TransitionEnum.PLACE_ORDER);
    }

    /**
     * @param request
     */
    public void cancelOrder(IdleRecycleOrderStateSynRequest request) {
        log.info("闲鱼订单-取消:{}", JSONObject.toJSONString(request));
        XyRecycleOrder order = recycleOrderService.getByBizOrderId(request.getBiz_order_id());

        XyRecycleOrderStateEnum.TransitionEnum transitionEnum = null;
        switch (order.getQuoteOrderState()) {
            case CREATE:
                transitionEnum = XyRecycleOrderStateEnum.TransitionEnum.CREATE_CANCEL;
                break;
            case QUOTED:
                transitionEnum = XyRecycleOrderStateEnum.TransitionEnum.QUOTED_CANCEL;
                break;
            case WAIT_PICK_UP:
                transitionEnum = XyRecycleOrderStateEnum.TransitionEnum.WAIT_PICK_UP_CANCEL;
                break;
            default:
                throw new OperationRejectedException(OperationExceptionCodeEnum.QUOTE_STATE_FAIL, order.getQuoteOrderState().getDesc());
        }
        //取消订单
        XyRecycleOrder up = new XyRecycleOrder();
        up.setId(order.getId());
        up.setCloseReason(request.getClose_reason());
        recycleOrderService.updateState(up, transitionEnum);
    }

    /**
     * 修改地址处理
     *
     * @param request
     */
    private void changeAddress(IdleRecycleOrderStateSynRequest request) {
        log.info("闲鱼订单-用户修改地址:{}", JSONObject.toJSONString(request));
        XyRecycleOrder order = recycleOrderService.getByBizOrderId(request.getBiz_order_id());
        //是否同意修改地址，取件之前默认同意修改地址
        boolean agreeUseAddressChange = XyRecycleOrderStateEnum.CREATE.equals(order.getQuoteOrderState())
                || XyRecycleOrderStateEnum.QUOTED.equals(order.getQuoteOrderState())
                || XyRecycleOrderStateEnum.WAIT_PICK_UP.equals(order.getQuoteOrderState());

        try {
            //同意修改地址
            if (agreeUseAddressChange) {
                XyRecycleOrder up = new XyRecycleOrder();
                up.setId(order.getId());
                up.setSellerPhone(request.getSeller_phone());
                up.setShipTime(request.getShip_time());
                up.setProvince(request.getProvince());
                up.setCity(request.getCity());
                up.setArea(request.getArea());
                up.setSellerAddress(request.getSeller_address());
                recycleOrderService.updateById(up);
            }
        } catch (Exception e) {
            log.error("闲鱼订单-用户修改地址处理异常:request:{},e:{}", JSONObject.toJSONString(request), e.getMessage(), e);
            agreeUseAddressChange = false;
        }

        //修改地址响应
        flywheelXianYuClient.orderFulfillmentChangeAddress(request.getBiz_order_id(), agreeUseAddressChange);
    }

    /**
     * @param request
     */
    private void applyRefundGoods(IdleRecycleOrderStateSynRequest request) {
        log.info("闲鱼订单-用户申请退回:{}", JSONObject.toJSONString(request));
        XyRecycleOrder order = recycleOrderService.getByBizOrderId(request.getBiz_order_id());

        //申请退回
        XyRecycleOrder up = new XyRecycleOrder();
        up.setId(order.getId());
        recycleOrderService.updateState(up, XyRecycleOrderStateEnum.TransitionEnum.QT_APPLY_REFUND);
    }

    /**
     * 用户撤销退回申请
     *
     * @param request
     */
    private void secondCheck31(IdleRecycleOrderStateSynRequest request) {
        log.info("闲鱼订单-用户撤销退回申请:{}", JSONObject.toJSONString(request));
        XyRecycleOrder order = recycleOrderService.getByBizOrderId(request.getBiz_order_id());

        //撤销
        XyRecycleOrder up = new XyRecycleOrder();
        up.setId(order.getId());
        recycleOrderService.updateState(up, XyRecycleOrderStateEnum.TransitionEnum.APPLY_REFUND_RETURN);

        //撤销重新推送质检
        flywheelXianYuClient.orderFulfillmentQuality(order.getBizOrderId(), order.getFinalApprizeAmount(), order.getQtCode());
    }

    /**
     * 用户确认交易
     *
     * @param request
     */
    private void sellerOrderConfirmed(IdleRecycleOrderStateSynRequest request) {
        log.info("闲鱼订单-用户确认交易:{}", JSONObject.toJSONString(request));
        XyRecycleOrder order = recycleOrderService.getByBizOrderId(request.getBiz_order_id());

        //用户确认交易
        XyRecycleOrder up = new XyRecycleOrder();
        up.setId(order.getId());
        recycleOrderService.updateState(up, XyRecycleOrderStateEnum.TransitionEnum.USER_AGREE);
    }


    /**
     * 用户评论
     *
     * @param request
     */
    private void sellerOrderRated(IdleRecycleOrderStateSynRequest request) {
        log.info("闲鱼订单-用户评论:{}", JSONObject.toJSONString(request));
        XyRecycleOrder order = recycleOrderService.getByBizOrderId(request.getBiz_order_id());

        //评论
        XyRecycleOrder up = new XyRecycleOrder();
        up.setId(order.getId());
        up.setUserEvaluate(request.getRate_content());
        recycleOrderService.updateById(up);
    }

}
