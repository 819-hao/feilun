package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.storework.IStoreWorkFacade;
import com.seeease.flywheel.storework.request.StoreWorkDeliveryRequest;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.flywheel.storework.result.StoreWorkDeliveryResult;
import com.seeease.flywheel.web.common.context.NiceStopWatch;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.entity.DouYinOrder;
import com.seeease.flywheel.web.entity.KuaishouOrder;
import com.seeease.flywheel.web.entity.enums.WhetherNotifyEnum;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.flywheel.web.infrastructure.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 门店发货
 *
 * @author Tiro
 * @date 2023/3/13
 */
@Slf4j
@Service
@Extension(bizId = BizCode.SHOP, useCase = UseCase.LOGISTICS_DELIVERY)
public class ShopWorkDeliveryExt implements SubmitExtPtI<StoreWorkDeliveryRequest, StoreWorkDeliveryResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkFacade storeWorkFacade;
    @Resource
    private DouYinOrderService douYinOrderService;

    @Resource
    private KuaishouOrderService kuaishouOrderService;

    @Resource
    private KuaishouCallbackNotifyService kuaishouCallbackNotifyService;
    @Resource
    private DouYinOrderLineService douYinOrderLineService;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private DouYinCallbackNotifyService douYinCallbackNotifyService;

    @Override
    public StoreWorkDeliveryResult submit(SubmitCmd<StoreWorkDeliveryRequest> cmd) {
        NiceStopWatch stopWatch = new NiceStopWatch(String.format("%s-%s-门店发货", cmd.getBizCode(), cmd.getUseCase()));
        log.info("门店发货:{}", JSONObject.toJSONString(cmd));
        stopWatch.start("门店发货业务开始");
        StoreWorkDeliveryRequest request = cmd.getRequest();
        request.setShopDelivery(true);
        StoreWorkDeliveryResult result = storeWorkFacade.logisticsDelivery(cmd.getRequest());
        stopWatch.stop();
        try {
            this.douYinNotice(result, 5000 - stopWatch.getTotalTimeMillis());
            this.KuaiShouNotice(result, 5000 - stopWatch.getTotalTimeMillis());
        } catch (Exception e) {
            log.error("告知抖音异常 ,{},{}", e, e.getMessage());
        }
        return result;
    }

    /**
     * 异步告知抖音发货
     *
     * @param result
     * @param timeout
     */
    private void douYinNotice(StoreWorkDeliveryResult result, long timeout) {
        Map<String, List<StoreWorkCreateResult>> workMap = result.getStoreWorkCreateResultList()
                .stream()
                .filter(s -> s.getOriginSerialNo().startsWith("TOCXS"))
                .collect(Collectors.groupingBy(StoreWorkCreateResult::getOriginSerialNo));

        if (MapUtils.isEmpty(workMap)) {
            return;
        }
        Map<String, List<DouYinOrder>> douYinOrderMap = douYinOrderService.selectListBySerialNo(workMap.keySet().stream().collect(Collectors.toList()))
                .stream()
                .collect(Collectors.groupingBy(DouYinOrder::getSerialNo));

        if (MapUtils.isEmpty(douYinOrderMap)) {
            return;
        }
        //任务异步处理
        Future<String> future = threadPoolTaskExecutor.submit(() -> {
            workMap.forEach((k, v) -> {
                try {
                    List<DouYinOrder> douYinOrderList = douYinOrderMap.get(k);
                    if (CollectionUtils.isEmpty(douYinOrderList)) {
                        return;
                    }
                    Map<Integer, WhetherNotifyEnum> resMap = douYinCallbackNotifyService.deliveryNotify(douYinOrderList, v.get(0).getDeliveryExpressNumber());
                    //更新发货通知状态
                    douYinOrderService.updateBatchById(douYinOrderList.stream()
                            .map(dyo -> {
                                DouYinOrder douYinOrder = new DouYinOrder();
                                douYinOrder.setId(dyo.getId());
                                douYinOrder.setWhetherNotify(resMap.getOrDefault(dyo.getId(), WhetherNotifyEnum.FAIL));
                                return douYinOrder;
                            }).collect(Collectors.toList()));
                } catch (Exception e) {
                    log.error("告知抖音异常,{}", e.getMessage(), e);
                }
            });
            return "通知抖音了";
        });
        if (timeout > 0) {
            try {
                String r = future.get(timeout, TimeUnit.MILLISECONDS);
                log.info("告知抖音完成,{}", r);
            } catch (Exception e) {
                log.error("告知抖音异常,{}", e.getMessage(), e);
            }
        }
    }

    /**
     * 异步告知快手发货
     *
     * @param result
     * @param timeout
     */
    private void KuaiShouNotice(StoreWorkDeliveryResult result, long timeout) {
        Map<String, List<StoreWorkCreateResult>> workMap = result.getStoreWorkCreateResultList()
                .stream()
                .filter(s -> s.getOriginSerialNo().startsWith("TOCXS"))
                .collect(Collectors.groupingBy(StoreWorkCreateResult::getOriginSerialNo));

        if (MapUtils.isEmpty(workMap)) {
            return;
        }
        Map<String, List<KuaishouOrder>> kuaiShouOrderMap = kuaishouOrderService.list(Wrappers.<KuaishouOrder>lambdaQuery().in(KuaishouOrder::getSerialNo, workMap.keySet().stream().collect(Collectors.toList())))
                .stream()
                .collect(Collectors.groupingBy(KuaishouOrder::getSerialNo));

        if (MapUtils.isEmpty(kuaiShouOrderMap)) {
            return;
        }
        //任务异步处理
        Future<String> future = threadPoolTaskExecutor.submit(() -> {
            workMap.forEach((k, v) -> {
                try {
                    List<KuaishouOrder> kuaishouOrderList = kuaiShouOrderMap.get(k);
                    if (CollectionUtils.isEmpty(kuaishouOrderList)) {
                        return;
                    }
                    Map<Integer, WhetherNotifyEnum> resMap = kuaishouCallbackNotifyService.deliveryNotify(kuaishouOrderList, v.get(0).getDeliveryExpressNumber());
                    //更新发货通知状态
                    kuaishouOrderService.updateBatchById(kuaishouOrderList.stream()
                            .map(kso -> {
                                KuaishouOrder kuaishouOrder = new KuaishouOrder();
                                kuaishouOrder.setId(kso.getId());
                                kuaishouOrder.setWhetherNotify(resMap.getOrDefault(kso.getId(), WhetherNotifyEnum.FAIL).getValue());
                                return kuaishouOrder;
                            }).collect(Collectors.toList()));
                } catch (Exception e) {
                    log.error("告知快手异常,{}", e.getMessage(), e);
                }
            });
            return "通知快手了";
        });
        if (timeout > 0) {
            try {
                String r = future.get(timeout, TimeUnit.MILLISECONDS);
                log.info("告知快手完成,{}", r);
            } catch (Exception e) {
                log.error("告知快手异常,{}", e.getMessage(), e);
            }
        }
    }

    @Override
    public Map<String, Object> workflowVar(StoreWorkDeliveryRequest request, StoreWorkDeliveryResult result) {
        return null;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(StoreWorkDeliveryRequest request, StoreWorkDeliveryResult result) {
        return result.getStoreWorkCreateResultList().stream().map(storeWorkCreateResult -> StockLifeCycleResult.builder()
                .stockId(storeWorkCreateResult.getStockId())
                .originSerialNo(storeWorkCreateResult.getSerialNo())
                .operationDesc(OperationDescConst.SHOP_LOGISTICS_DELIVERY)
                .build()).collect(Collectors.toList());
    }

    @Override
    public Class<StoreWorkDeliveryRequest> getRequestClass() {
        return StoreWorkDeliveryRequest.class;
    }

    @Override
    public void validate(SubmitCmd<StoreWorkDeliveryRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getWorkIds()), "作业id集合不能为空");
        Assert.isTrue(cmd.getRequest().getWorkIds().size() == cmd.getTaskList().size(), "业务数量和任务数量不一致");
        //判断如果是商品定金销售商品是当前是否能发货
        Assert.isTrue(storeWorkFacade.validateCanDoIfMallOrder(cmd.getRequest().getWorkIds()), "商城订单未支付完成，暂时无法发货");

    }


}

