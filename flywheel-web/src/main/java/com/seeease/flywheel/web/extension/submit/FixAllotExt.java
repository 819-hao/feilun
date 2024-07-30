package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.fix.IFixFacade;
import com.seeease.flywheel.fix.request.FixAllotRequest;
import com.seeease.flywheel.fix.result.FixAllotResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @Author Mr. Du
 * @Description 维修分配
 * @Date create in 2023/1/19 09:49
 */
@Service
@Extension(bizId = BizCode.FIX, useCase = UseCase.ALLOT_RECEIVING)
public class FixAllotExt implements SubmitExtPtI<FixAllotRequest, FixAllotResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IFixFacade fixFacade;

    @Override
    public FixAllotResult submit(SubmitCmd<FixAllotRequest> cmd) {
        return fixFacade.allot(cmd.getRequest());
    }

    @Override
    public Map<String, Object> workflowVar(FixAllotRequest request, FixAllotResult result) {
        return null;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(FixAllotRequest request, FixAllotResult result) {
        return Optional.ofNullable(result.getStockId()).map(r -> Arrays.asList(StockLifeCycleResult.builder()
                .stockId(result.getStockId())
                .originSerialNo(result.getSerialNo())
                .operationDesc(OperationDescConst.FIX_ALLOT)
                .build())).orElse(Arrays.asList());
    }

    @Override
    public Class<FixAllotRequest> getRequestClass() {
        return FixAllotRequest.class;
    }

    @Override
    public void validate(SubmitCmd<FixAllotRequest> cmd) {
        Assert.notNull(cmd.getRequest().getMaintenanceMasterId(), "维修师不存在");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getFixId()) || Objects.nonNull(cmd.getRequest().getSerialNo()), "维修单条件不存在");
    }
}
