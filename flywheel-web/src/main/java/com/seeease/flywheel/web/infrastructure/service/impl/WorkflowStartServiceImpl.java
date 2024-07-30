package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.web.entity.WorkflowStart;
import com.seeease.flywheel.web.infrastructure.service.WorkflowStartService;
import com.seeease.flywheel.web.infrastructure.mapper.WorkflowStartMapper;
import org.springframework.stereotype.Service;

/**
* @author Tiro
* @description 针对表【workflow_start(流程启动记录)】的数据库操作Service实现
* @createDate 2023-01-19 17:26:26
*/
@Service
public class WorkflowStartServiceImpl extends ServiceImpl<WorkflowStartMapper, WorkflowStart>
    implements WorkflowStartService{

}




