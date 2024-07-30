package com.seeease.flywheel.web.extension.create;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.pricing.request.PricingCancelRequest;
import com.seeease.flywheel.purchase.IPurchaseReturnFacade;
import com.seeease.flywheel.purchase.request.PurchaseReturnCreateRequest;
import com.seeease.flywheel.purchase.result.PurchaseReturnCreateResult;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.ProcessDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.pti.CreateExtPtI;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Service
@Slf4j
@Extension(bizId = BizCode.PURCHASE_RETURN, useCase = UseCase.PROCESS_CREATE)
public class PurchaseReturnCreateExt implements CreateExtPtI<PurchaseReturnCreateRequest, List<PurchaseReturnCreateResult>> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseReturnFacade purchaseReturnFacade;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Class<PurchaseReturnCreateRequest> getRequestClass() {
        return PurchaseReturnCreateRequest.class;
    }

    @Override
    public void validate(CreateCmd<PurchaseReturnCreateRequest> cmd) {
        //采购必要参数校验
        Assert.notNull(cmd, "请求不能为空");
        Assert.notNull(cmd.getRequest(), "请求入参不能为空");
        PurchaseReturnCreateRequest request = cmd.getRequest();

        Assert.isTrue(CollectionUtils.isNotEmpty(request.getDetails()) &&
                request.getDetails().stream().allMatch(Objects::nonNull), "采购信息不能为空或者非法");

        Assert.notNull(request.getCustomerId(), "客户不能为空");
        Assert.notNull(request.getCustomerContactId(), "客户联系人不能为空");

        Assert.isTrue(request.getDetails().stream().allMatch(billPurchaseReturnLineDto -> Objects.nonNull(billPurchaseReturnLineDto.getStockId()))
                , "采购表不能为空或者非法");

        List<PurchaseReturnCreateRequest.BillPurchaseReturnLineDto> collect = request.getDetails().stream().filter(billPurchaseReturnLineDto ->
                request.getDetails().stream().filter(billPurchaseReturnLineDto1 -> billPurchaseReturnLineDto.getStockId().equals(billPurchaseReturnLineDto1.getStockId())).count() > 1).collect(Collectors.toList());
        Assert.isTrue(CollectionUtils.isEmpty(collect), "存在重复数据");

    }

    @Override
    public List<PurchaseReturnCreateResult> create(CreateCmd<PurchaseReturnCreateRequest> cmd) {

        cmd.getRequest().setStoreId(UserContext.getUser().getStore().getId().intValue());

        return purchaseReturnFacade.create(cmd.getRequest());
    }

    @Override
    public List<ProcessInstanceStartDto> start(PurchaseReturnCreateRequest request, List<PurchaseReturnCreateResult> result) {
        return result.stream()
                .map(purchaseReturnCreateResult -> {

                    Map<String, Object> workflowVar = new HashMap<>();
                    workflowVar.put(VariateDefinitionKeyEnum.STORE_WORK_SERIAL_NO_LIST.getKey(), purchaseReturnCreateResult.getSerialList());

                    if (!purchaseReturnCreateResult.getStoreId().equals(FlywheelConstant._ZB_ID)) {
                        workflowVar.put(VariateDefinitionKeyEnum.SHORT_CODES.getKey(), purchaseReturnCreateResult.getShortcodes());
                    }

                    return ProcessInstanceStartDto.builder()
                            .serialNo(purchaseReturnCreateResult.getSerialNo())
                            .process(purchaseReturnCreateResult.getStoreId().equals(FlywheelConstant._ZB_ID) ? ProcessDefinitionKeyEnum.PURCHASE_RETURN : ProcessDefinitionKeyEnum.STORE_PURCHASE_RETURN)
                            .variables(workflowVar)
                            .build();
                }).collect(Collectors.toList());
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PurchaseReturnCreateRequest request, List<PurchaseReturnCreateResult> result) {
        List<StockLifeCycleResult> resultList = new ArrayList<>();
        try {
            for (PurchaseReturnCreateResult createResult : result) {
                List<StoreWorkCreateResult> storeWorkList = createResult.getStoreWorkList();
                for (StoreWorkCreateResult storeWorkCreateResult : storeWorkList) {
                    resultList.add(StockLifeCycleResult.builder()
                            .stockId(storeWorkCreateResult.getStockId())
                            .originSerialNo(createResult.getSerialNo())
                            .operationDesc(OperationDescConst.PURCHASE_RETURN_CREATE)
                            .build());
                }
            }
            this.publishEvent(result);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return resultList;
    }

    /**
     * 定价取消
     *
     * @param list
     */
    private void publishEvent(List<PurchaseReturnCreateResult> list) {
        try {
            List<PricingCancelRequest> collect = new ArrayList<>();

            //生命周期
            for (PurchaseReturnCreateResult result : list) {
                List<StoreWorkCreateResult> storeWorkList = result.getStoreWorkList();
                for (StoreWorkCreateResult storeWorkCreateResult : storeWorkList) {

                    PricingCancelRequest pricingCancelRequest = new PricingCancelRequest();
                    pricingCancelRequest.setStockId(storeWorkCreateResult.getStockId());
                    pricingCancelRequest.setCreatedBy(UserContext.getUser().getUserName());
                    pricingCancelRequest.setCreatedId(UserContext.getUser().getId());
                    pricingCancelRequest.setStoreId(UserContext.getUser().getStore().getId());

                    collect.add(pricingCancelRequest);
                }
            }

            applicationContext.publishEvent(new PricingCancelEvent(this, collect));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
