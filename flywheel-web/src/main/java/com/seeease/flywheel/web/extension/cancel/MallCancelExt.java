package com.seeease.flywheel.web.extension.cancel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.recycle.IRecycleOderFacade;
import com.seeease.flywheel.recycle.request.RecycleOrderClientCancelRequest;
import com.seeease.flywheel.recycle.result.RecycleOrderClientCancelResult;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.pti.CancelExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

/**
 * 客户取消
 *
 * @author Tiro
 * @date 2023/1/19
 */
@Service
@Extension(bizId = BizCode.MALL, useCase = UseCase.CANCEL)
public class MallCancelExt implements CancelExtPtI<RecycleOrderClientCancelRequest, RecycleOrderClientCancelResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IRecycleOderFacade recycleOderFacade;

    @Override
    public RecycleOrderClientCancelResult cancel(CancelCmd<RecycleOrderClientCancelRequest> cmd) {
        return recycleOderFacade.clientCancel(cmd.getRequest());
    }

    @Override
    public String businessKey(RecycleOrderClientCancelResult result) {
        return result.getSerialNo();
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(RecycleOrderClientCancelRequest request, RecycleOrderClientCancelResult result) {
        return Arrays.asList();
    }

    @Override
    public Class<RecycleOrderClientCancelRequest> getRequestClass() {
        return RecycleOrderClientCancelRequest.class;
    }

    @Override
    public void validate(CancelCmd<RecycleOrderClientCancelRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.notNull(cmd.getRequest().getId(), "商城回收id不能为空");
    }
}
