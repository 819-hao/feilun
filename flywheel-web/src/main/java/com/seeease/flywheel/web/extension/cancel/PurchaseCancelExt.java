package com.seeease.flywheel.web.extension.cancel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.purchase.IPurchaseFacade;
import com.seeease.flywheel.purchase.request.PurchaseCancelRequest;
import com.seeease.flywheel.purchase.result.PurchaseCancelResult;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.pti.CancelExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 采购取消
 *
 * @author Tiro
 * @date 2023/1/19
 */
@Service
@Extension(bizId = BizCode.PURCHASE, useCase = UseCase.CANCEL)
public class PurchaseCancelExt implements CancelExtPtI<PurchaseCancelRequest, PurchaseCancelResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseFacade purchaseFacade;

    @Override
    public PurchaseCancelResult cancel(CancelCmd<PurchaseCancelRequest> cmd) {
        return purchaseFacade.cancel(cmd.getRequest());
    }

    @Override
    public String businessKey(PurchaseCancelResult result) {
        return result.getSerialNo();
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PurchaseCancelRequest request, PurchaseCancelResult result) {
        return result.getLine().stream().map(purchaseLineVO -> StockLifeCycleResult.builder()
                        .stockWno(purchaseLineVO.getWno())
                        .originSerialNo(result.getSerialNo())
                        .operationDesc(OperationDescConst.PURCHASE_CANCEL).build())
                .collect(Collectors.toList());
    }

    @Override
    public Class<PurchaseCancelRequest> getRequestClass() {
        return PurchaseCancelRequest.class;
    }

    @Override
    public void validate(CancelCmd<PurchaseCancelRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.notNull(cmd.getRequest().getPurchaseId(), "采购单id不能为空");
    }
}
