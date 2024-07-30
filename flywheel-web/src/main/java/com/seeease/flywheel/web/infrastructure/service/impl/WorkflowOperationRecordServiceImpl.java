package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.web.common.work.flow.WorkflowStateEnum;
import com.seeease.flywheel.web.entity.WorkflowOperationRecord;
import com.seeease.flywheel.web.infrastructure.mapper.WorkflowOperationRecordMapper;
import com.seeease.flywheel.web.infrastructure.service.WorkflowOperationRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【workflow_operation_record(流程操作记录)】的数据库操作Service实现
 * @createDate 2023-01-19 17:26:20
 */
@Service
public class WorkflowOperationRecordServiceImpl extends ServiceImpl<WorkflowOperationRecordMapper, WorkflowOperationRecord>
        implements WorkflowOperationRecordService {


    @Override
    public int insertBatchSomeColumn(List<WorkflowOperationRecord> recordList) {
        return baseMapper.insertBatchSomeColumn(recordList);
    }

    @Override
    public int updateByTaskId(String taskId, WorkflowStateEnum state) {
        return baseMapper.updateStateByTaskId(taskId, state.getValue());
    }
}




