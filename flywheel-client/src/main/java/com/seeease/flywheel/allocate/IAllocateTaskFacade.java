package com.seeease.flywheel.allocate;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.allocate.request.AllocateTaskListRequest;
import com.seeease.flywheel.allocate.result.AllocateTaskListResult;

/**
 * @author Tiro
 * @date 2023/8/29
 */
public interface IAllocateTaskFacade {

    /**
     * 调拨任务列表
     *
     * @param request
     * @return
     */
    PageResult<AllocateTaskListResult> list(AllocateTaskListRequest request);
}
