package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.pricing.request.PricingCancelRequest;
import com.seeease.flywheel.purchase.IPurchaseFacade;
import com.seeease.flywheel.purchase.request.PurchaseAcceptRepairRequest;
import com.seeease.flywheel.purchase.result.PurchaseAcceptRepairResult;
import com.seeease.flywheel.recycle.IRecycleOderFacade;
import com.seeease.flywheel.recycle.result.MarkektRecycleGetSaleProcessResult;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.event.PricingCancelEvent;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 采购修不修
 *
 * @author Tiro
 * @date 2023/1/13
 */
@Slf4j
@Service
@Extension(bizId = BizCode.PURCHASE, useCase = UseCase.ACCEPT_REPAIR)
public class PurchaseAcceptRepairExt implements SubmitExtPtI<PurchaseAcceptRepairRequest, PurchaseAcceptRepairResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseFacade purchaseFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IRecycleOderFacade recycleOderFacade;

    @Autowired
    private ApplicationContext applicationContext;

    @Resource
    private CreateCmdExe workCreateCmdExe;

    @Override
    public Class<PurchaseAcceptRepairRequest> getRequestClass() {
        return PurchaseAcceptRepairRequest.class;
    }

    @Override
    public void validate(SubmitCmd<PurchaseAcceptRepairRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.notNull(cmd.getRequest().getPurchaseId(), "采购单id不能为空");
    }

    @Override
    public PurchaseAcceptRepairResult submit(SubmitCmd<PurchaseAcceptRepairRequest> cmd) {
        cmd.getRequest().setStoreId(UserContext.getUser().getStore().getId().intValue());
        try {
            if (ObjectUtils.isNotEmpty(cmd.getRequest().getPurchaseId()) && cmd.getRequest().getAcceptState().equals(1)) {

                for (MarkektRecycleGetSaleProcessResult process : recycleOderFacade.intercept(cmd.getRequest().getPurchaseId()).stream().filter(recycleGetSaleProcessResult -> Objects.nonNull(recycleGetSaleProcessResult)).collect(Collectors.toList())) {
                    //开启销售
                    if (ObjectUtils.isNotEmpty(process) && ObjectUtils.isNotEmpty(process.getSaleLoadRequest())) {
                        CreateCmd createCmd = new CreateCmd();
                        createCmd.setBizCode(BizCode.SALE);
                        createCmd.setUseCase(UseCase.PROCESS_LOAD);
                        createCmd.setRequest(process.getSaleLoadRequest());
                        workCreateCmdExe.create(createCmd);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return purchaseFacade.acceptRepair(cmd.getRequest());
    }

    @Override
    public Map<String, Object> workflowVar(PurchaseAcceptRepairRequest request, PurchaseAcceptRepairResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        //0 否 1 是
        workflowVar.put(VariateDefinitionKeyEnum.DELIVER_TO.getKey(), request.getAcceptState().intValue() == 0 ? 1 : 0);
        workflowVar.put(VariateDefinitionKeyEnum.IS_REPAIR.getKey(), request.getAcceptState().intValue() == 1 ? 1 : 0);
        workflowVar.put(VariateDefinitionKeyEnum.IS_ALLOT.getKey(), request.getAcceptState().intValue() == 1 ? 1 : 0);

        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PurchaseAcceptRepairRequest request, PurchaseAcceptRepairResult result) {
        //不接受维修
        if (request.getAcceptState().intValue() == OperationDescConst.ACCEPT_REPAIR_RETURN_VALUE) {
            publishEvent(result);
        }

        return Arrays.asList(
                StockLifeCycleResult.builder()
                        .stockId(result.getStoreWorkList().get(FlywheelConstant.INDEX).getStockId())
                        .originSerialNo(result.getSerialNo())
                        .operationDesc(request.getAcceptState().intValue() == OperationDescConst.ACCEPT_REPAIR_FIX_VALUE ?
                                String.format(OperationDescConst.ACCEPT_REPAIR, OperationDescConst.ACCEPT_REPAIR_FIX) : String.format(OperationDescConst.ACCEPT_REPAIR, OperationDescConst.ACCEPT_REPAIR_RETURN))
                        .build());
    }

    /**
     * 通知取消定价
     *
     * @param data
     */
    private void publishEvent(PurchaseAcceptRepairResult data) {
        try {
            //生命周期
            List<PricingCancelRequest> collect = data.getStoreWorkList().stream().map(storeWorkCreateResult -> {

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
