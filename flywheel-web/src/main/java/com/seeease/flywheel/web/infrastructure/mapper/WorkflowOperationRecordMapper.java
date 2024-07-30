package com.seeease.flywheel.web.infrastructure.mapper;

import com.seeease.flywheel.web.entity.WorkflowOperationRecord;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Tiro
 * @description 针对表【workflow_operation_record(流程操作记录)】的数据库操作Mapper
 * @createDate 2023-01-19 17:26:20
 * @Entity com.seeease.flywheel.web.entity.WorkflowOperationRecord
 */
public interface WorkflowOperationRecordMapper extends SeeeaseMapper<WorkflowOperationRecord> {

    /**
     * @param taskId
     * @param state
     * @return
     */
    int updateStateByTaskId(@Param("taskId") String taskId, @Param("workState") Integer state);
}




