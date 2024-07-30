package com.seeease.flywheel.web.common.work.pti;

import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.result.CreateResult;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/17
 */
public interface CreateExtPtI<T, R> extends WorkExtPtI<T, CreateCmd<T>> {

    /**
     * 创建
     *
     * @param cmd
     * @return
     */
    default CreateResult handle(CreateCmd<T> cmd) {
        R r = create(cmd);
        CreateResult.CreateResultBuilder builder = CreateResult.builder()
                .bizResult(r);
        try {
            builder.instanceStart(start(cmd.getRequest(), r));
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

    R create(CreateCmd<T> cmd);

    List<ProcessInstanceStartDto> start(T request, R result);

    List<StockLifeCycleResult> lifeCycle(T request, R result);


}