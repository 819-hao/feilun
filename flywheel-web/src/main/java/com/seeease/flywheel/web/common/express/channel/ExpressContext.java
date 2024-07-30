package com.seeease.flywheel.web.common.express.channel;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.entity.ExpressOrder;
import com.seeease.flywheel.web.entity.enums.ExpressOrderStateEnum;
import com.seeease.flywheel.web.infrastructure.service.ExpressOrderService;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/9/20
 */
@Slf4j
@Component
public class ExpressContext {

    private Map<ExpressChannelTypeEnum, ExpressChannel> channelMap;

    public ExpressContext(List<ExpressChannel> channelList) {
        channelMap = channelList.stream().collect(Collectors.toMap(ExpressChannel::getChanelType, Function.identity()));
    }

    @Resource
    private ExpressOrderService expressOrderService;


    /**
     * 物流下单
     *
     * @param placeOrderList
     * @return
     */
    public List<ExpressPlaceOrderResult> create(List<ExpressPlaceOrder> placeOrderList) {
        //已经存在的订单记录
        Map<String, ExpressOrder> expressOrderMap = expressOrderService.list(Wrappers.<ExpressOrder>lambdaQuery()
                .in(ExpressOrder::getSonSerialNo, placeOrderList.stream().map(ExpressPlaceOrder::getBusinessNo).collect(Collectors.toList())))
                .stream()
                .collect(Collectors.toMap(ExpressOrder::getSonSerialNo, Function.identity()));

        List<ExpressOrder> initExpressOrderList = placeOrderList.stream()
                .filter(t -> Boolean.FALSE.equals(expressOrderMap.containsKey(t.getBusinessNo())))
                .map(t -> {
                    ExpressOrder expressOrderUp = new ExpressOrder();
                    expressOrderUp.setExpressState(ExpressOrderStateEnum.INIT);
                    expressOrderUp.setSonSerialNo(t.getBusinessNo());
                    expressOrderUp.setSerialNo(t.getOrderInfo().getOrderNo());
                    expressOrderUp.setExpressChannel(t.getChannelType());
                    expressOrderUp.setExpressSource(1);
                    expressOrderUp.setRequestId(t.getRequestID());
                    expressOrderUp.setStoreId(t.getOrderInfo().getSaleShopId());
                    if (t.getChannelType() == ExpressChannelTypeEnum.KS_SF) {
                        expressOrderUp.setDouYinShopId(t.getOrderInfo().getKuaiShouShopId());
                    } else {
                        expressOrderUp.setDouYinShopId(t.getOrderInfo().getDouYinShopId());
                    }

                    return expressOrderUp;
                })
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(initExpressOrderList)) {
            //预插入下单记录
            expressOrderService.insertBatchSomeColumn(initExpressOrderList);
            //缓存预下单
            expressOrderMap.putAll(initExpressOrderList.stream().collect(Collectors.toMap(ExpressOrder::getSonSerialNo, Function.identity())));
        }

        List<ExpressPlaceOrderResult> resultList = new ArrayList<>();

        for (ExpressPlaceOrder placeOrder : placeOrderList) {
            ExpressOrder expressOrder = expressOrderMap.get(placeOrder.getBusinessNo());
            switch (expressOrder.getExpressState()) {
                case SUCCESS:
                    resultList.add(ExpressPlaceOrderResult.builder()
                            .success(true)
                            .businessNo(expressOrder.getSonSerialNo())
                            .orderNo(expressOrder.getSerialNo())
                            .expressNumber(expressOrder.getExpressNo())
                            .build());
                    break;
                case CANCEL:
                    resultList.add(ExpressPlaceOrderResult.builder()
                            .success(false)
                            .businessNo(expressOrder.getSonSerialNo())
                            .orderNo(expressOrder.getSerialNo())
                            .errMsg("订单已回收无法重新下单")
                            .build());
                    break;
                case ING:
                    resultList.add(ExpressPlaceOrderResult.builder()
                            .success(false)
                            .businessNo(expressOrder.getSonSerialNo())
                            .orderNo(expressOrder.getSerialNo())
                            .errMsg("订单进行中无法重新下单")
                            .build());
                    break;

                //能下单的状态
                case INIT:
                case FAIL:
                    //锁定下单
                    try {
                        ExpressOrder up = new ExpressOrder();
                        up.setId(expressOrder.getId());
                        up.setLockTime(System.currentTimeMillis());
                        expressOrderService.upAndStateTransition(up, expressOrder.getExpressState().equals(ExpressOrderStateEnum.INIT)
                                ? ExpressOrderStateEnum.TransitionEnum.INIT_ING : ExpressOrderStateEnum.TransitionEnum.FAIL_ING);
                    } catch (Exception e) {
                        log.error("锁定下单失败{}", e.getMessage(), e);
                        resultList.add(ExpressPlaceOrderResult.builder()
                                .success(false)
                                .errMsg("锁定下单失败")
                                .build());
                        break;
                    }
                    //去下单
                    ExpressPlaceOrderResult result = null;
                    try {
                        if (placeOrder.checkFailed()) {
                            log.warn("下单参数[{}]", JSONObject.toJSONString(placeOrder));
                            throw new OperationRejectedException(OperationExceptionCodeEnum.ACCOUNT_NON_NULL);
                        }
                        result = channelMap.get(placeOrder.getChannelType()).placeOrder(placeOrder);
                        result.setBusinessNo(expressOrder.getSonSerialNo());
                        result.setOrderNo(expressOrder.getSerialNo());
                    } catch (Exception e) {
                        log.error("快递单下单异常:{}", e.getMessage(), e);
                        result = ExpressPlaceOrderResult.builder()
                                .success(false)
                                .businessNo(expressOrder.getSonSerialNo())
                                .orderNo(expressOrder.getSerialNo())
                                .errMsg(e.getMessage())
                                .build();
                    } finally {
                        resultList.add(result);

                        ExpressOrder expressOrderUp = new ExpressOrder();
                        expressOrderUp.setId(expressOrder.getId());
                        expressOrderUp.setErrorMsg(result.getErrMsg());
                        expressOrderUp.setExpressNo(result.getExpressNumber());
                        expressOrderUp.setLockTime(NumberUtils.LONG_ZERO);
                        expressOrderService.upAndStateTransition(expressOrderUp, result.isSuccess()
                                ? ExpressOrderStateEnum.TransitionEnum.ING_SUCCESS : ExpressOrderStateEnum.TransitionEnum.ING_FAIL);
                    }
            }
        }

        return resultList;
    }


    /**
     * @param expressNo
     * @return
     */
    public ExpressRecoveryOrderResult recoveryOrder(String expressNo) {
        ExpressOrder expressOrder = expressOrderService.getOne(Wrappers.<ExpressOrder>lambdaQuery()
                .eq(ExpressOrder::getExpressNo, expressNo));

        if (Objects.isNull(expressOrder)) {
            return ExpressRecoveryOrderResult.builder()
                    .success(false)
                    .errMsg("物流单不存在下单记录")
                    .build();
        }
        if (expressOrder.getExpressState().equals(ExpressOrderStateEnum.CANCEL)) {
            return ExpressRecoveryOrderResult.builder()
                    .success(true)
                    .errMsg("无须重复操作")
                    .build();
        }

        //去下单
        try {
            ExpressRecoveryOrderResult result = channelMap.get(expressOrder.getExpressChannel()).recoveryOrder(expressOrder);
            ExpressOrder expressOrderUp = new ExpressOrder();
            expressOrderUp.setId(expressOrder.getId());
            expressOrderService.upAndStateTransition(expressOrderUp, ExpressOrderStateEnum.TransitionEnum.SUCCESS_CANCEL);
            return result;
        } catch (Exception e) {
            log.error("快递单下单异常:{}", e.getMessage(), e);
            return ExpressRecoveryOrderResult.builder()
                    .success(false)
                    .errMsg(e.getMessage())
                    .build();
        }
    }

}
