package com.seeease.flywheel.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.seeease.firework.facade.common.request.WaitTaskRequest;
import com.seeease.firework.facade.service.ITaskFacade;
import com.seeease.flywheel.web.common.PageData;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.work.cmd.*;
import com.seeease.flywheel.web.common.work.consts.ProcessDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.executor.CancelCmdExe;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.common.work.executor.QueryCmdExe;
import com.seeease.flywheel.web.common.work.executor.SubmitCmdExe;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.flow.WorkflowStateEnum;
import com.seeease.flywheel.web.entity.WorkflowOperationRecord;
import com.seeease.flywheel.web.entity.WorkflowStart;
import com.seeease.flywheel.web.entity.request.WorkCompleteSkipRequest;
import com.seeease.flywheel.web.entity.request.WorkflowToDoListRequest;
import com.seeease.flywheel.web.entity.result.WorkflowToDoListResult;
import com.seeease.flywheel.web.infrastructure.external.firework.WorkflowService;
import com.seeease.flywheel.web.infrastructure.service.WorkflowOperationRecordService;
import com.seeease.flywheel.web.infrastructure.service.WorkflowStartService;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/1/9
 */
@Slf4j
@RestController
public class WorkController {

    @NacosValue(value = "${work.batch.size:300}", autoRefreshed = true)
    private int batchSize;

    @Resource
    private WorkflowService workflowService;
    @Resource
    private SubmitCmdExe workflowCmdExe;
    @Resource
    private QueryCmdExe workDetailsCmdExe;
    @Resource
    private CreateCmdExe workCreateCmdExe;
    @Resource
    private CancelCmdExe cancelCmdExe;
    @Resource
    private WorkflowStartService workflowStartService;
    @Resource
    private WorkflowOperationRecordService recordService;

    @DubboReference(check = false, version = "1.0.0")
    private ITaskFacade taskFacade;

    @PostMapping("/work/toDoList")
    public SingleResponse toDoList(@RequestBody WorkflowToDoListRequest request) {
        return SingleResponse.of(workflowService.toDoList(request));
    }

    @PostMapping("/workCreate/{bizCode}/{useCase}")
    public SingleResponse create(@PathVariable String bizCode,
                                 @PathVariable String useCase,
                                 @RequestBody CreateCmd request) {
        this.setPathVariable(request, bizCode, useCase);
        return SingleResponse.of(workCreateCmdExe.create(request));
    }

    @PostMapping("/workQuery/{bizCode}/{useCase}")
    public SingleResponse query(@PathVariable String bizCode,
                                @PathVariable String useCase,
                                @RequestBody QueryCmd request) {
        this.setPathVariable(request, bizCode, useCase);
        return SingleResponse.of(workDetailsCmdExe.query(request));
    }

    @PostMapping("/workSubmit/{bizCode}/{useCase}")
    public SingleResponse submit(@PathVariable String bizCode,
                                 @PathVariable String useCase,
                                 @RequestBody SubmitCmd request) {
        if (CollectionUtils.isNotEmpty(request.getTaskList()) && request.getTaskList().size() > batchSize) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.TASK_BATCH_SIZE, batchSize);
        }
        this.setPathVariable(request, bizCode, useCase);
        return SingleResponse.of(workflowCmdExe.submit(request));
    }

    @PostMapping("/workCancel/{bizCode}/{useCase}")
    public SingleResponse cancel(@PathVariable String bizCode,
                                 @PathVariable String useCase,
                                 @RequestBody CancelCmd request) {
        this.setPathVariable(request, bizCode, useCase);
        return SingleResponse.of(cancelCmdExe.cancel(request));
    }

    @PostMapping("/workCreate")
    public SingleResponse create(@RequestBody CreateCmd request) {
        return SingleResponse.of(workCreateCmdExe.create(request));
    }

    @PostMapping("/workQuery")
    public SingleResponse query(@RequestBody QueryCmd request) {
        return SingleResponse.of(workDetailsCmdExe.query(request));
    }

    @PostMapping("/workSubmit")
    public SingleResponse submit(@RequestBody SubmitCmd request) {
        if (CollectionUtils.isNotEmpty(request.getTaskList()) && request.getTaskList().size() > batchSize) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.TASK_BATCH_SIZE, batchSize);
        }
        return SingleResponse.of(workflowCmdExe.submit(request));
    }

    @PostMapping("/workCancel")
    public SingleResponse cancel(@RequestBody CancelCmd request) {
        return SingleResponse.of(cancelCmdExe.cancel(request));
    }

    /**
     * @param cmd
     * @param bizCode
     * @param useCase
     */
    private void setPathVariable(BaseCmd cmd, String bizCode, String useCase) {
        cmd.setBizCode(bizCode);
        cmd.setUseCase(useCase);
    }

    @PostMapping("/work/startProcess/{id}")
    public SingleResponse startProcess(@PathVariable Integer id) {
        WorkflowStart start = workflowStartService.getById(id);
        if (Objects.nonNull(start)
                && WorkflowStateEnum.COMPLETE.equals(start.getBizState())
                && start.getWorkState().equals(WorkflowStateEnum.INIT)) {

            ProcessInstanceStartDto processInstanceStartDto = ProcessInstanceStartDto.builder()
                    .process(ProcessDefinitionKeyEnum.fromKey(start.getProcessDefinitionKey()))
                    .serialNo(start.getBusinessKey())
                    .variables(start.getProcessVariables())
                    .build();
            WorkflowStart up = new WorkflowStart();
            up.setId(start.getId());
            try {
                //启动工作流
                String processInstanceId = workflowService.startProcess(processInstanceStartDto);
                up.setWorkState(WorkflowStateEnum.COMPLETE);
                up.setProcessInstanceId(processInstanceId);
                workflowStartService.updateById(up);
            } catch (Exception e) {
                log.error("启动流程异常，start={},{}", JSONObject.toJSONString(start), e.getMessage(), e);
            }
        }
        return SingleResponse.buildSuccess();
    }

    @PostMapping("/work/cancelProcess/{id}")
    public SingleResponse cancelProcess(@PathVariable Integer id) {
        WorkflowStart workflowStart = workflowStartService.getById(id);
        if (Objects.nonNull(workflowStart)
                && WorkflowStateEnum.CANCEL.equals(workflowStart.getBizState())) {

            try {

                workflowService.cancelProcess(workflowStart.getProcessInstanceId(), "流程取消");

                WorkflowStart up = new WorkflowStart();
                up.setId(workflowStart.getId());
                up.setWorkState(WorkflowStateEnum.CANCEL);
                workflowStartService.updateById(up);
            } catch (Exception e) {
                log.error("流程取消异常，workflowStart={},{}", JSONObject.toJSONString(workflowStart), e.getMessage(), e);
            }
        }
        return SingleResponse.buildSuccess();
    }

    @PostMapping("/work/completeTask/{id}")
    public SingleResponse completeTask(@PathVariable Integer id) {
        WorkflowOperationRecord record = recordService.getById(id);
        if (Objects.nonNull(record)
                && WorkflowStateEnum.COMPLETE.equals(record.getBizState())
                && WorkflowStateEnum.INIT.equals(record.getWorkState())) {
            try {
                UserTaskDto task = UserTaskDto.builder()
                        .taskId(record.getTaskId())
                        .businessKey(record.getBusinessKey())
                        .parentBusinessKey(record.getParentBusinessKey())
                        .variables(record.getVariables())
                        .build();
                //完成任务
                workflowService.completeTask(Lists.newArrayList(task), null);
                recordService.updateByTaskId(task.getTaskId(), WorkflowStateEnum.COMPLETE);
            } catch (Exception e) {
                log.error("完成任务流程异常，record={},{}", JSONObject.toJSONString(record), e.getMessage(), e);
            }
        }
        return SingleResponse.buildSuccess();
    }


    /**
     * 补偿任务
     *
     * @param request
     * @return
     */
    @PostMapping("/work/completeSkip")
    public SingleResponse completeSkip(@RequestBody WorkCompleteSkipRequest request) {

        PageData<WorkflowToDoListResult> tasks = workflowService.toDoList(WorkflowToDoListRequest.builder()
                .serialNo(Objects.requireNonNull(request.getSerialNo()))
                .taskDefinitionKeys(Arrays.asList(Objects.requireNonNull(request.getTaskDefinitionKey())))
                .page(NumberUtils.INTEGER_ONE)
                .limit(200)
                .build());

        if (CollectionUtils.isEmpty(tasks.getResult())) {
            return SingleResponse.of(tasks.getResult().size());
        }

        List<UserTaskDto> taskList = tasks.getResult().stream()
                .map(t -> UserTaskDto.builder()
                        .taskId(t.getTaskId())
                        .taskName(t.getTaskName())
                        .businessKey(t.getBusinessKey())
                        .parentBusinessKey(t.getParentBusinessKey())
                        .variables(request.getGlobalVariables())
                        .build())
                .collect(Collectors.toList());
        //设置参数
        if (Objects.nonNull(request.getGlobalVariables())) {
            taskList.forEach(t -> {
                if (Objects.nonNull(t.getVariables())) {
                    t.getVariables().putAll(request.getGlobalVariables());
                } else {
                    t.setVariables(request.getGlobalVariables());
                }
            });
        }

        List<WorkflowOperationRecord> recordList = taskList.stream()
                .map(task -> {
                    WorkflowOperationRecord record = new WorkflowOperationRecord();
                    record.setTaskId(task.getTaskId());
                    record.setBizCode(request.getBizCode());
                    record.setUseCase(request.getUseCase());
                    record.setBusinessKey(task.getBusinessKey());
                    record.setParentBusinessKey(task.getParentBusinessKey());
                    record.setBizState(WorkflowStateEnum.COMPLETE);
                    record.setWorkState(WorkflowStateEnum.INIT);
                    record.setVariables(task.getVariables());
                    return record;
                })
                .collect(Collectors.toList());
        recordService.insertBatchSomeColumn(recordList);

        Map<String, Boolean> resultMap = Collections.EMPTY_MAP;
        try {

            //完成任务
            resultMap = workflowService.completeTask(taskList, UserContext.getUser());
        } catch (Exception e) {
            log.error("完成任务SKIP异常，taskList={},request={}", JSONObject.toJSONString(taskList), JSONObject.toJSONString(request), e.getMessage(), e);
        } finally {
            Map<String, Boolean> finalResultMap = resultMap;
            taskList.forEach(task -> {
                WorkflowStateEnum stateEnum = Optional.ofNullable(finalResultMap.get(task.getTaskId()))
                        .map(t -> t ? WorkflowStateEnum.COMPLETE : WorkflowStateEnum.ERROR)
                        .orElse(WorkflowStateEnum.ERROR);
                if (1 != recordService.updateByTaskId(task.getTaskId(), stateEnum)) {
                    log.error("完成任务SKIP存储失败，record={},request={}}", JSONObject.toJSONString(task), JSONObject.toJSONString(request));
                }

            });
        }
        return SingleResponse.of(tasks.getResult().size());
    }

    /**
     * 消息任务跳过
     * @return
     */
    @PostMapping("/work/messageSkip")
    public SingleResponse waitTask(@RequestBody WaitTaskRequest request){
        taskFacade.waitTask(request);

        return SingleResponse.buildSuccess();
    }

}
