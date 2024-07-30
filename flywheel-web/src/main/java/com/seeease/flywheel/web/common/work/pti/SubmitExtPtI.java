package com.seeease.flywheel.web.common.work.pti;

import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.result.SubmitResult;

import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/1/13
 */
public interface SubmitExtPtI<T, R> extends WorkExtPtI<T, SubmitCmd<T>> {

    default SubmitResult handle(SubmitCmd<T> cmd) {
        R r = submit(cmd);
        SubmitResult.SubmitResultBuilder builder = SubmitResult.builder()
                .bizResult(r);
        try {
            builder.workflowVar(workflowVar(cmd.getRequest(), r));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            builder.stockLifeCycleResultList(lifeCycle(cmd.getRequest(), r));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    R submit(SubmitCmd<T> cmd);

    Map<String, Object> workflowVar(T request, R result);

    List<StockLifeCycleResult> lifeCycle(T request, R result);
}
