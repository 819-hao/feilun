package com.seeease.flywheel.web.extension.cancel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.pricing.request.PricingCreateRequest;
import com.seeease.flywheel.purchase.IPurchaseReturnFacade;
import com.seeease.flywheel.purchase.request.PurchaseReturnCancelRequest;
import com.seeease.flywheel.purchase.result.PurchaseReturnCancelResult;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.pti.CancelExtPtI;
import com.seeease.flywheel.web.event.PricingStartEvent;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/2/2
 */
@Slf4j
@Service
@Extension(bizId = BizCode.PURCHASE_RETURN, useCase = UseCase.CANCEL)
public class PurchaseReturnCancelExt implements CancelExtPtI<PurchaseReturnCancelRequest, PurchaseReturnCancelResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseReturnFacade facade;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public PurchaseReturnCancelResult cancel(CancelCmd<PurchaseReturnCancelRequest> cmd) {
        return facade.cancel(cmd.getRequest());
    }

    @Override
    public String businessKey(PurchaseReturnCancelResult result) {
        return result.getSerialNo();
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PurchaseReturnCancelRequest request, PurchaseReturnCancelResult result) {
        //定价消息
        this.publishEvent(result);

        return result.getLine().stream().map(purchaseReturnLineVO -> StockLifeCycleResult.builder()
                        .stockId(purchaseReturnLineVO.getStockId())
                        .originSerialNo(result.getSerialNo())
                        .operationDesc(OperationDescConst.PURCHASE_RETURN_CANCEL).build())
                .collect(Collectors.toList());
    }

    @Override
    public Class<PurchaseReturnCancelRequest> getRequestClass() {
        return PurchaseReturnCancelRequest.class;
    }

    @Override
    public void validate(CancelCmd<PurchaseReturnCancelRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.notNull(cmd.getRequest().getPurchaseReturnId(), "采购退货单id不能为空");
    }

    /**
     * 开启定价
     *
     * @param result
     */
    private void publishEvent(PurchaseReturnCancelResult result) {
        try {
            List<PricingCreateRequest> collect = result.getLine().stream().map(stockLifeCycleResult -> {
                PricingCreateRequest pricingCreateRequest = new PricingCreateRequest();
                pricingCreateRequest.setStockId(stockLifeCycleResult.getStockId());
                pricingCreateRequest.setCreatedBy(UserContext.getUser().getUserName());
                pricingCreateRequest.setUpdatedBy(UserContext.getUser().getUserName());
                pricingCreateRequest.setCreatedId(UserContext.getUser().getId());
                pricingCreateRequest.setUpdatedId(UserContext.getUser().getId());
                pricingCreateRequest.setStoreId(UserContext.getUser().getStore().getId());
                pricingCreateRequest.setAgain(true);
                pricingCreateRequest.setCancel(true);
                return pricingCreateRequest;
            }).collect(Collectors.toList());

            applicationContext.publishEvent(new PricingStartEvent(this, collect));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
