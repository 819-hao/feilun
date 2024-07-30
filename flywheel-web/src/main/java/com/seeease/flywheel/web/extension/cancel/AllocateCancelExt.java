package com.seeease.flywheel.web.extension.cancel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.allocate.IAllocateFacade;
import com.seeease.flywheel.allocate.request.AllocateCancelRequest;
import com.seeease.flywheel.allocate.result.AllocateCancelResult;
import com.seeease.flywheel.common.StockLifeCycleResult;
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
 * 取消调拨
 *
 * @author Tiro
 * @date 2023/3/15
 */
@Service
@Extension(bizId = BizCode.ALLOCATE, useCase = UseCase.CANCEL)
public class AllocateCancelExt implements CancelExtPtI<AllocateCancelRequest, AllocateCancelResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IAllocateFacade allocateFacade;

    @Override
    public AllocateCancelResult cancel(CancelCmd<AllocateCancelRequest> cmd) {
        return allocateFacade.cancel(cmd.getRequest());
    }

    @Override
    public String businessKey(AllocateCancelResult result) {
        return result.getSerialNo();
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(AllocateCancelRequest request, AllocateCancelResult result) {
        return result.getStockIdList()
                .stream()
                .map(id -> StockLifeCycleResult.builder()
                        .originSerialNo(result.getSerialNo())
                        .stockId(id)
                        .operationDesc(OperationDescConst.ALLOCATE_CANCEL)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Class<AllocateCancelRequest> getRequestClass() {
        return AllocateCancelRequest.class;
    }

    @Override
    public void validate(CancelCmd<AllocateCancelRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.notNull(cmd.getRequest().getAllocateId(), "调拨单id不能为空");
    }
}