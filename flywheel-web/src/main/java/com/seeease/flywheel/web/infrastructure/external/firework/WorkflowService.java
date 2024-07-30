package com.seeease.flywheel.web.infrastructure.external.firework;

import com.seeease.flywheel.web.common.PageData;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.entity.User;
import com.seeease.flywheel.web.entity.request.WorkflowToDoListRequest;
import com.seeease.flywheel.web.entity.result.WorkflowToDoListResult;
import com.seeease.springframework.context.LoginUser;

import java.util.List;
import java.util.Map;

/**
 * @author trio
 * @date 2023/1/15
 */
public interface WorkflowService {
    String startProcess(ProcessInstanceStartDto processInstanceStart);

    void cancelProcess(String processInstanceId, String reason);

    UserTaskDto queryTask(UserTaskDto dto, List<String> workProcessIdList);

    Map<String, UserTaskDto> queryTaskList(List<UserTaskDto> dto, List<String> taskDefinitionKeys);

    Map<String, Boolean> completeTask(List<UserTaskDto> dto, LoginUser user);

    PageData<WorkflowToDoListResult> toDoList(WorkflowToDoListRequest request);

    void saveUser(List<User> users);
}
