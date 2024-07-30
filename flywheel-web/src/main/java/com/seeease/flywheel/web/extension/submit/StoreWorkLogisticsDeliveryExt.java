package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.purchase.request.AutoPurchaseCreateRequest;
import com.seeease.flywheel.storework.IStoreWorkFacade;
import com.seeease.flywheel.storework.request.StoreWorkDeliveryRequest;
import com.seeease.flywheel.storework.result.StoreWorkDeliveryResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.event.AutoPurchaseCreateEvent;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 物流发货
 *
 * @Auther Gilbert
 * @Date 2023/1/19 15:23
 */
@Service
@Extension(bizId = BizCode.STORAGE, useCase = UseCase.LOGISTICS_DELIVERY)
@Slf4j
public class StoreWorkLogisticsDeliveryExt implements SubmitExtPtI<StoreWorkDeliveryRequest, StoreWorkDeliveryResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkFacade storeWorkFacade;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public StoreWorkDeliveryResult submit(SubmitCmd<StoreWorkDeliveryRequest> cmd) {
        return storeWorkFacade.logisticsDelivery(cmd.getRequest());
    }

    @Override
    public Map<String, Object> workflowVar(StoreWorkDeliveryRequest request, StoreWorkDeliveryResult result) {
        return null;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(StoreWorkDeliveryRequest request, StoreWorkDeliveryResult result) {

        try {
            startProcess(request.getWorkIds());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return result.getStoreWorkCreateResultList().stream().map(storeWorkCreateResult -> StockLifeCycleResult.builder()
                .stockId(storeWorkCreateResult.getStockId())
                .originSerialNo(storeWorkCreateResult.getSerialNo())
                .operationDesc(OperationDescConst.LOGISTICS_DELIVERY)
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
    }


    /**
     * 开启自动采购创建
     *
     * @param list
     */
    private void startProcess(List<Integer> list) {

        try {
            AutoPurchaseCreateRequest autoPurchaseCreateRequest = new AutoPurchaseCreateRequest();
            autoPurchaseCreateRequest.setWorkIdList(list);
            applicationContext.publishEvent(new AutoPurchaseCreateEvent(this, autoPurchaseCreateRequest));
            log.info("自动创建采购开启正常,{}", list);
        } catch (Exception e) {
            log.error("自动创建采购开启异常,{}", e.getMessage(), e);
        }
    }

}
