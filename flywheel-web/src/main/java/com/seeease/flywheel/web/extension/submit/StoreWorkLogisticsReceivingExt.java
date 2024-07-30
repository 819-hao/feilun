package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.pricing.request.PricingCancelRequest;
import com.seeease.flywheel.storework.IStoreWorkFacade;
import com.seeease.flywheel.storework.request.StoreWorkReceivedRequest;
import com.seeease.flywheel.storework.result.StoreWorkReceivedListResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.event.PricingCancelEvent;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 物流收货
 *
 * @Auther Gilbert
 * @Date 2023/1/19 15:23
 */
@Slf4j
@Service
@Extension(bizId = BizCode.STORAGE, useCase = UseCase.LOGISTICS_RECEIVING)
public class StoreWorkLogisticsReceivingExt implements SubmitExtPtI<StoreWorkReceivedRequest, StoreWorkReceivedListResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkFacade storeWorkFacade;

    @Autowired
    private ApplicationContext applicationContext;


    @Override
    public StoreWorkReceivedListResult submit(SubmitCmd<StoreWorkReceivedRequest> cmd) {
        return storeWorkFacade.logisticsReceiving(cmd.getRequest());
    }

    @Override
    public Map<String, Object> workflowVar(StoreWorkReceivedRequest request, StoreWorkReceivedListResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        workflowVar.put(VariateDefinitionKeyEnum.LOGISTICS_REJECT_STATE.getKey(), request.getLogisticsRejectState());

        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(StoreWorkReceivedRequest request, StoreWorkReceivedListResult result) {
        //拒收
        if (request.getLogisticsRejectState().intValue() == OperationDescConst.LOGISTICS_RECEIVING_YES_VALUE) {
            publishEvent(result);
        }
        return result.getStoreWorkCreateResultList().stream().map(storeWorkCreateResult -> StockLifeCycleResult.builder()
                .stockId(storeWorkCreateResult.getStockId())
                .originSerialNo(storeWorkCreateResult.getSerialNo())
                .operationDesc(request.getLogisticsRejectState().intValue() == OperationDescConst.LOGISTICS_RECEIVING_NO_VALUE ?
                        String.format(OperationDescConst.LOGISTICS_RECEIVING, OperationDescConst.LOGISTICS_RECEIVING_NO) : String.format(OperationDescConst.LOGISTICS_RECEIVING, OperationDescConst.LOGISTICS_RECEIVING_YES))
                .build()).collect(Collectors.toList());
    }

    @Override
    public Class<StoreWorkReceivedRequest> getRequestClass() {
        return StoreWorkReceivedRequest.class;
    }

    @Override
    public void validate(SubmitCmd<StoreWorkReceivedRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getWorkIds()), "作业id集合不能为空");
        Assert.isTrue(cmd.getRequest().getWorkIds().size() == cmd.getTaskList().size(), "业务数量和任务数量不一致");
    }

    /**
     * 通知取消定价
     *
     * @param data
     */
    private void publishEvent(StoreWorkReceivedListResult data) {
        try {
            List<PricingCancelRequest> collect = data.getStoreWorkCreateResultList().stream().map(storeWorkCreateResult -> {

                PricingCancelRequest pricingCancelRequest = new PricingCancelRequest();
                pricingCancelRequest.setStockId(storeWorkCreateResult.getStockId());

                pricingCancelRequest.setCreatedBy(UserContext.getUser().getUserName());
                pricingCancelRequest.setCreatedId(UserContext.getUser().getId());
                pricingCancelRequest.setStoreId(UserContext.getUser().getStore().getId());

                return pricingCancelRequest;
            }).collect(Collectors.toList());

            applicationContext.publishEvent(new PricingCancelEvent(this, collect));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
