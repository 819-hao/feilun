package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.pricing.request.PricingCreateRequest;
import com.seeease.flywheel.purchase.IPurchaseFacade;
import com.seeease.flywheel.purchase.request.PurchaseExpressNumberUploadRequest;
import com.seeease.flywheel.purchase.result.PurchaseExpressNumberUploadListResult;
import com.seeease.flywheel.recycle.IRecycleOderFacade;
import com.seeease.flywheel.recycle.result.MarkektRecycleGetSaleProcessResult;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.BusinessMappingProcessEnum;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.ProcessDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.event.PricingStartEvent;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/1/13
 */
@Slf4j
@Service
@Extension(bizId = BizCode.PURCHASE, useCase = UseCase.UPLOAD_EXPRESS_NUMBER)
public class PurchaseExpressNumberUploadExt implements SubmitExtPtI<PurchaseExpressNumberUploadRequest, PurchaseExpressNumberUploadListResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseFacade purchaseFacade;

    @Autowired
    private ApplicationContext applicationContext;

    @Resource
    private CreateCmdExe workCreateCmdExe;

    @DubboReference(check = false, version = "1.0.0")
    private IRecycleOderFacade recycleOderFacade;

    @Override
    public Class<PurchaseExpressNumberUploadRequest> getRequestClass() {
        return PurchaseExpressNumberUploadRequest.class;
    }

    @Override
    public void validate(SubmitCmd<PurchaseExpressNumberUploadRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.notNull(cmd.getRequest().getPurchaseId(), "采购单id不能为空");
        Assert.isTrue(StringUtils.isNotBlank(cmd.getRequest().getExpressNumber()), "上传快递单号");
    }

    @Override
    public PurchaseExpressNumberUploadListResult submit(SubmitCmd<PurchaseExpressNumberUploadRequest> cmd) {
        cmd.getRequest().setStoreId(UserContext.getUser().getStore().getId().intValue());

        try {

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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return purchaseFacade.uploadExpressNumber(cmd.getRequest());
    }

    @Override
    public Map<String, Object> workflowVar(PurchaseExpressNumberUploadRequest request, PurchaseExpressNumberUploadListResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        ProcessDefinitionKeyEnum processDefinitionKeyEnum = BusinessMappingProcessEnum.fromValue(result.getPurchaseSource());

        //工作流参数不同
        switch (processDefinitionKeyEnum) {
            case PERSONAL_CONSIGN_SALE:
            case PERSONAL_BUY_BACK:
                workflowVar.put(VariateDefinitionKeyEnum.STORE_WORK_SERIAL_NO.getKey(), result.getStoreWorkList().get(0).getSerialNo());
                break;

            default:
                workflowVar.put(VariateDefinitionKeyEnum.STORE_WORK_SERIAL_NO_LIST.getKey(), result.getStoreWorkList()
                        .stream()
                        .map(StoreWorkCreateResult::getSerialNo)
                        .collect(Collectors.toList())
                );
        }
        workflowVar.put(VariateDefinitionKeyEnum.SHORT_CODES.getKey(), result.getShortcodes());

        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PurchaseExpressNumberUploadRequest request, PurchaseExpressNumberUploadListResult result) {

//        if (!BusinessMappingProcessEnum.fromValue(result.getPurchaseSource()).equals(ProcessDefinitionKeyEnum.PERSONAL_CONSIGN_SALE)) {
//            //定价流程
//            publishEvent(result);
//        }
        return result.getStoreWorkList().stream()
                .map(storeWorkCreateResult ->
                        Lists.newArrayList(StockLifeCycleResult.builder()
                                        .stockId(storeWorkCreateResult.getStockId())
                                        .originSerialNo(result.getSerialNo())
                                        .createdTime(result.getPurchaseCreatedTime())
                                        .operationDesc(OperationDescConst.PURCHASE_CREATE)
                                        .build()
                                , StockLifeCycleResult.builder()
                                        .stockId(storeWorkCreateResult.getStockId())
                                        .originSerialNo(result.getSerialNo())
                                        .operationDesc(OperationDescConst.CUSTOMER_DELIVERY)
                                        .build()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private void publishEvent(PurchaseExpressNumberUploadListResult data) {
        try {
            List<PricingCreateRequest> collect = data.getStoreWorkList().stream().map(storeWorkCreateResult -> {

                PricingCreateRequest pricingCreateRequest = new PricingCreateRequest();
                pricingCreateRequest.setStockId(storeWorkCreateResult.getStockId());
                pricingCreateRequest.setOriginSerialNo(data.getSerialNo());
                pricingCreateRequest.setStoreWorkSerialNo(storeWorkCreateResult.getSerialNo());
                pricingCreateRequest.setPricingSource(storeWorkCreateResult.getWorkSource());
                pricingCreateRequest.setCreatedBy(UserContext.getUser().getUserName());
                pricingCreateRequest.setUpdatedBy(UserContext.getUser().getUserName());
                pricingCreateRequest.setCreatedId(UserContext.getUser().getId());
                pricingCreateRequest.setUpdatedId(UserContext.getUser().getId());
                pricingCreateRequest.setStoreId(UserContext.getUser().getStore().getId());
                pricingCreateRequest.setAgain(false);
                pricingCreateRequest.setCancel(false);

                return pricingCreateRequest;
            }).collect(Collectors.toList());

            applicationContext.publishEvent(new PricingStartEvent(this, collect));
        } catch (Exception e) {
            log.error("定价开启异常,{}", e.getMessage(), e);
        }
    }
}
