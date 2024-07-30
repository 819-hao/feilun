package com.seeease.flywheel.web.infrastructure.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.web.common.work.flow.WorkflowStateEnum;
import com.seeease.flywheel.web.entity.WorkflowOperationRecord;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【workflow_operation_record(流程操作记录)】的数据库操作Service
 * @createDate 2023-01-19 17:26:20
 */
public interface WorkflowOperationRecordService extends IService<WorkflowOperationRecord> {
    /**
     * @param recordList
     * @return
     */
    int insertBatchSomeColumn(List<WorkflowOperationRecord> recordList);

    /**
     * @param taskId
     * @param state
     * @return
     */
    int updateByTaskId(String taskId, WorkflowStateEnum state);
}
