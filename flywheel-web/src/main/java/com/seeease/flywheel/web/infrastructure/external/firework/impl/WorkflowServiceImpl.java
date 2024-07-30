package com.seeease.flywheel.web.infrastructure.external.firework.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.seeease.firework.facade.common.base.Page;
import com.seeease.firework.facade.common.dto.CompleteDTO;
import com.seeease.firework.facade.common.dto.GroupDTO;
import com.seeease.firework.facade.common.dto.TaskDTO;
import com.seeease.firework.facade.common.dto.UserDTO;
import com.seeease.firework.facade.common.request.*;
import com.seeease.firework.facade.service.IIdentityFacade;
import com.seeease.firework.facade.service.ITaskFacade;
import com.seeease.firework.facade.service.ProcessInstanceService;
import com.seeease.flywheel.web.common.PageData;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.entity.User;
import com.seeease.flywheel.web.entity.UserRole;
import com.seeease.flywheel.web.entity.request.WorkflowToDoListRequest;
import com.seeease.flywheel.web.entity.result.WorkflowToDoListResult;
import com.seeease.flywheel.web.infrastructure.external.firework.WorkflowService;
import com.seeease.springframework.context.LoginUser;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author trio
 * @date 2023/1/15
 */
@Service
@Slf4j
public class WorkflowServiceImpl implements WorkflowService {

    @NacosValue(value = "${saleOrder.replaceDeliveryShopRole:}", autoRefreshed = true)
    private List<String> IP_REAR_ROLE;

    @DubboReference(check = false, version = "1.0.0")
    private ITaskFacade taskFacade;

    @DubboReference(check = false, version = "1.0.0")
    private ProcessInstanceService processInstanceService;

    @DubboReference(check = false, version = "1.0.0")
    private IIdentityFacade identityFacade;


    @Override
    public String startProcess(ProcessInstanceStartDto dto) {
        StartProcessInstanceByKeyRequest request = new StartProcessInstanceByKeyRequest();
        request.setProcessDefinitionKey(dto.getProcess().getKey());
        request.setBusinessKey(dto.getSerialNo());
        request.setVariables(dto.getVariables());

        return processInstanceService.startProcessInstanceByKey(request);
    }

    @Override
    public void cancelProcess(String processInstanceId, String reason) {
        DeleteProcessInstanceByIdRequest request = new DeleteProcessInstanceByIdRequest();
        request.setProcessInstanceId(processInstanceId);
        request.setReason(reason);
        processInstanceService.deleteProcessInstanceById(request);
    }

    @Override
    public UserTaskDto queryTask(UserTaskDto dto, List<String> workProcessIdList) {
        LoginUser user = UserContext.getUser();
        TaskQueryRequest request = new TaskQueryRequest();
        request.setBusinessKey(dto.getBusinessKey());
        request.setOperator(user.getUserid());
        request.setTaskDefinitionKeys(workProcessIdList);
        TaskDTO task = taskFacade.singleTask(request);
        if (Objects.isNull(task) || !workProcessIdList.contains(task.getTaskDefinitionKey())) {
            return null;
        }
        return UserTaskDto.builder()
                .taskName(task.getTaskName())
                .businessKey(task.getBusinessKey())
                .parentBusinessKey(task.getParentBusinessKey())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .taskId(task.getTaskId())
                .build();
    }

    @Override
    public Map<String, UserTaskDto> queryTaskList(List<UserTaskDto> dto, List<String> taskDefinitionKeys) {
        LoginUser user = UserContext.getUser();
        TaskBatchQueryRequest request = new TaskBatchQueryRequest();
        request.setBusinessKeys(dto.stream().map(UserTaskDto::getBusinessKey).collect(Collectors.toList()));
        request.setOperator(user.getUserid());
        request.setTaskDefinitionKeys(taskDefinitionKeys);

        Map<String, TaskDTO> taskMap = taskFacade.listTaskByBusinessKeys(request);
        if (MapUtils.isEmpty(taskMap)) {
            return Collections.emptyMap();
        }

        Map<String, UserTaskDto> result = new HashMap<>(taskMap.size());
        taskMap.entrySet().forEach(t -> {
            TaskDTO task = t.getValue();
            result.put(t.getKey(), UserTaskDto.builder()
                    .taskName(task.getTaskName())
                    .businessKey(task.getBusinessKey())
                    .parentBusinessKey(task.getParentBusinessKey())
                    .taskDefinitionKey(task.getTaskDefinitionKey())
                    .taskId(task.getTaskId())
                    .build());
        });
        return result;
    }

    @Override
    public Map<String, Boolean> completeTask(List<UserTaskDto> taskDtoList, LoginUser user) {
        CompleteTaskRequest request = new CompleteTaskRequest();
        request.setTaskList(taskDtoList.stream()
                .filter(t -> StringUtils.isNotBlank(t.getTaskId()))
                .map(t -> CompleteTaskRequest.CompleteTaskDTO.builder()
                        .taskId(t.getTaskId())
                        .variables(t.getVariables())
                        .build())
                .collect(Collectors.toList()));
        request.setOperator(Optional.ofNullable(user).map(LoginUser::getUserid).orElse(null));

        return taskFacade.completeTask(request)
                .stream()
                .collect(Collectors.toMap(CompleteDTO::getTaskId, CompleteDTO::isSuccess));

    }

    @Override
    public PageData<WorkflowToDoListResult> toDoList(WorkflowToDoListRequest request) {
        LoginUser user = UserContext.getUser();
        TaskPageQueryRequest taskPageQueryRequest = new TaskPageQueryRequest();
        taskPageQueryRequest.setOperator(user.getUserid());
        taskPageQueryRequest.setPageNum(request.getPage());
        taskPageQueryRequest.setPageSize(request.getLimit());
        taskPageQueryRequest.setKeyword(request.getSerialNo());
        taskPageQueryRequest.setTaskDefinitionKeys(request.getTaskDefinitionKeys());
        Page<TaskDTO> res = taskFacade.pageTask(taskPageQueryRequest);


        return PageData.<WorkflowToDoListResult>builder()
                .totalCount(res.getSize())
                .result(res.getList().stream()
                        .map(this::convert)
                        .collect(Collectors.toList()))
                .totalPage(res.getSize() / request.getLimit() + res.getSize() % request.getLimit() == 0 ? 0L : 1L)
                .build();
    }

    @Override
    public void saveUser(List<User> users) {
        if (CollectionUtils.isEmpty(users)) {
            return;
        }
        UserSaveRequest request = new UserSaveRequest();
        request.setUsers(users.stream()
                .map(u -> {
                    UserDTO user = new UserDTO();
                    user.setUserId(u.getUserid());
                    user.setUserName(u.getUserName());
                    if (CollectionUtils.isNotEmpty(u.getRoles())) {
                        user.setGroups(u.getRoles().stream()
                                .map(r -> this.convertGroupDTO(u, r))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()));
                    }
                    //直播运维后方=所有抖音门店后方
                    if (user.getGroups().stream().anyMatch(t -> t.getGroupId().equals("zbywrear"))
                            && CollectionUtils.isNotEmpty(IP_REAR_ROLE)) {
                        user.getGroups().addAll(
                                IP_REAR_ROLE.stream()
                                        .map(t -> {
                                            GroupDTO g = new GroupDTO();
                                            g.setGroupId(t);
                                            return g;
                                        })
                                        .collect(Collectors.toList())
                        );
                    }
                    return user;
                })
                .collect(Collectors.toList())
        );
        identityFacade.saveUser(request);
    }

    /**
     * @param dto
     * @return
     */
    private WorkflowToDoListResult convert(TaskDTO dto) {
        return WorkflowToDoListResult.builder()
                .taskId(dto.getTaskId())
                .taskName(dto.getTaskName())
                .taskDefinitionKey(dto.getTaskDefinitionKey())
                .processDefinitionKey(dto.getProcessDefinitionKey())
                .processDefinitionName(dto.getProcessDefinitionName())
                .businessKey(dto.getBusinessKey())
                .parentBusinessKey(dto.getParentBusinessKey())
                .createTime(dto.getCreateTime())
                .build();
    }

    /**
     * @param u
     * @param r
     * @return
     */
    private GroupDTO convertGroupDTO(User u, UserRole r) {
        //角色key不符合规则
        if (StringUtils.isEmpty(r.getRoleKey())) {
            return null;
        }
        //标签简码不符合规则
        if (r.getShopSpec() != 0 && StringUtils.isEmpty(u.getTagShortcodes())) {
            return null;
        }
        String groupId = r.getRoleKey();
        String groupName = r.getRoleName();
        if (r.getShopSpec() != 0) {
            groupId = u.getTagShortcodes() + groupId;
            groupName = u.getTagName() + groupName;
        }
        GroupDTO g = new GroupDTO();
        g.setGroupId(groupId);
        g.setGroupName(groupName);
        return g;
    }
}
