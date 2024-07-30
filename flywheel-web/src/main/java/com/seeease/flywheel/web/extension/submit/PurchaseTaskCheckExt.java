package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.purchase.IPurchaseTaskFacade;
import com.seeease.flywheel.purchase.request.PurchaseTaskCheckRequest;
import com.seeease.flywheel.purchase.result.PurchaseTaskCheckResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采购任务审核
 *
 * @author Tiro
 * @date 2023/1/13
 */
@Slf4j
@Service
@Extension(bizId = BizCode.PURCHASE_TASK, useCase = UseCase.ACCEPT_REPAIR)
public class PurchaseTaskCheckExt implements SubmitExtPtI<PurchaseTaskCheckRequest, PurchaseTaskCheckResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseTaskFacade purchaseTaskFacade;

    @Override
    public Class<PurchaseTaskCheckRequest> getRequestClass() {
        return PurchaseTaskCheckRequest.class;
    }

    @Override
    public void validate(SubmitCmd<PurchaseTaskCheckRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.notNull(cmd.getRequest().getId(), "采购单id不能为空");
    }

    @Override
    public PurchaseTaskCheckResult submit(SubmitCmd<PurchaseTaskCheckRequest> cmd) {

        return purchaseTaskFacade.check(cmd.getRequest());
    }

    @Override
    public Map<String, Object> workflowVar(PurchaseTaskCheckRequest request, PurchaseTaskCheckResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        //0 否 1 是
        workflowVar.put(VariateDefinitionKeyEnum.VERIFY.getKey(), request.getCheck());

        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PurchaseTaskCheckRequest request, PurchaseTaskCheckResult result) {

        return Arrays.asList();
    }

}
