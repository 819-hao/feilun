package com.seeease.flywheel.web.extension.cancel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.purchase.IPurchaseTaskFacade;
import com.seeease.flywheel.purchase.request.PurchaseTaskCancelRequest;
import com.seeease.flywheel.purchase.result.PurchaseTaskCancelResult;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.pti.CancelExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author wbh
 * @date 2023/2/2
 */
@Slf4j
@Service
@Extension(bizId = BizCode.PURCHASE_TASK, useCase = UseCase.CANCEL)
public class PurchaseTaskCancelExt implements CancelExtPtI<PurchaseTaskCancelRequest, PurchaseTaskCancelResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseTaskFacade facade;

    @Override
    public PurchaseTaskCancelResult cancel(CancelCmd<PurchaseTaskCancelRequest> cmd) {
        return facade.cancel(cmd.getRequest());
    }

    @Override
    public String businessKey(PurchaseTaskCancelResult result) {
        return result.getSerialNo();
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PurchaseTaskCancelRequest request, PurchaseTaskCancelResult result) {
        return Collections.emptyList();
    }

    @Override
    public Class<PurchaseTaskCancelRequest> getRequestClass() {
        return PurchaseTaskCancelRequest.class;
    }

    @Override
    public void validate(CancelCmd<PurchaseTaskCancelRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getId()) || StringUtils.isNotBlank(cmd.getRequest().getSerialNo()), "id或单号不能为空");
    }
}
