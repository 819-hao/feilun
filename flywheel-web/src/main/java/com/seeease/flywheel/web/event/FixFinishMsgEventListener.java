package com.seeease.flywheel.web.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.firework.facade.common.request.WaitTaskRequest;
import com.seeease.firework.facade.service.ITaskFacade;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import com.seeease.flywheel.web.entity.WorkflowStart;
import com.seeease.flywheel.web.infrastructure.mapper.WorkflowStartMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Mr. Du
 * @Description 维修完成通知
 * @Date create in 2023/3/21 15:21
 */
@Slf4j
@Component
public class FixFinishMsgEventListener implements ApplicationListener<FixFinishMsgEvent> {

    @Resource
    private WorkflowStartMapper workflowStartMapper;

    @DubboReference(check = false, version = "1.0.0")
    private ITaskFacade taskFacade;

    @Override
    public void onApplicationEvent(FixFinishMsgEvent fixFinishMsgEvent) {

        WorkflowStart workflowStart = workflowStartMapper.selectList(Wrappers.<WorkflowStart>lambdaQuery()
                .eq(WorkflowStart::getBusinessKey, fixFinishMsgEvent.getFixFinishMsgRequest().getSerialNo())).stream().findAny().orElse(null);

        if (ObjectUtils.isNotEmpty(workflowStart) && StringUtils.isNotBlank(workflowStart.getProcessInstanceId())) {
            //执行某某
            taskFacade.waitTask(WaitTaskRequest.builder()
                    .processInstanceId(workflowStart.getProcessInstanceId())
                    .activityId(TaskDefinitionKeyEnum.FIX_WAIT.getKey())
                    .build());
        }
    }
}
