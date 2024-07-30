package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.purchase.IPurchaseFacade;
import com.seeease.flywheel.purchase.request.PurchaseExpressNumberUploadRequest;
import com.seeease.flywheel.purchase.result.PurchaseExpressNumberUploadListResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.UserContext;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 归还用户
 *
 * @author Tiro
 * @date 2023/1/13
 */
@Service
@Extension(bizId = BizCode.PURCHASE, useCase = UseCase.CONFIRM_RETURN)
public class PurchaseConfirmReturnExt implements SubmitExtPtI<PurchaseExpressNumberUploadRequest, PurchaseExpressNumberUploadListResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseFacade purchaseFacade;

    @Override
    public Class<PurchaseExpressNumberUploadRequest> getRequestClass() {
        return PurchaseExpressNumberUploadRequest.class;
    }

    @Override
    public void validate(SubmitCmd<PurchaseExpressNumberUploadRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.notNull(cmd.getRequest().getPurchaseId(), "采购单id不能为空");
    }

    @Override
    public PurchaseExpressNumberUploadListResult submit(SubmitCmd<PurchaseExpressNumberUploadRequest> cmd) {
        cmd.getRequest().setStoreId(UserContext.getUser().getStore().getId().intValue());

        return purchaseFacade.confirmReturn(cmd.getRequest());
    }

    @Override
    public Map<String, Object> workflowVar(PurchaseExpressNumberUploadRequest request, PurchaseExpressNumberUploadListResult result) {
        return null;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PurchaseExpressNumberUploadRequest request, PurchaseExpressNumberUploadListResult result) {
        return Arrays.asList(StockLifeCycleResult.builder()
                .originSerialNo(result.getSerialNo())
                .stockId(result.getStoreWorkList().get(FlywheelConstant.INDEX).getStockId())
                .operationDesc(OperationDescConst.CONFIRM_RETURN)
                .build());
    }
}
