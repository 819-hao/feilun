package com.seeease.flywheel.web.common.work.pti;

import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.result.CancelResult;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/19
 */
public interface CancelExtPtI<T, R> extends WorkExtPtI<T, CancelCmd<T>> {

    /**
     * 取消
     *
     * @param cmd
     * @return
     */
    default CancelResult handle(CancelCmd<T> cmd) {
        R r = cancel(cmd);
        CancelResult.CancelResultBuilder builder = CancelResult.builder()
                .bizResult(r);
        try {
            builder.businessKey(businessKey(r));
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

    R cancel(CancelCmd<T> cmd);

    String businessKey(R result);

    List<StockLifeCycleResult> lifeCycle(T request, R result);

}