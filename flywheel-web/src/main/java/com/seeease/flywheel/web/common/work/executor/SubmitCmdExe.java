package com.seeease.flywheel.web.common.work.executor;

import com.alibaba.cola.extension.BizScenario;
import com.alibaba.cola.extension.ExtensionExecutor;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.goods.IStockLifeCycleFacade;
import com.seeease.flywheel.goods.request.StockLifeCycleCreateRequest;
import com.seeease.flywheel.web.common.context.NiceStopWatch;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.flow.WorkflowStateEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.common.work.result.SubmitResult;
import com.seeease.flywheel.web.entity.WorkflowOperationRecord;
import com.seeease.flywheel.web.infrastructure.external.firework.WorkflowService;
import com.seeease.flywheel.web.infrastructure.service.WorkflowOperationRecordService;
import com.seeease.springframework.context.LoginStore;
import com.seeease.springframework.context.LoginUser;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/1/13
 */
@Slf4j
@Component
public class SubmitCmdExe {
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private ExtensionExecutor extensionExecutor;
    @Resource
    private WorkflowService workflowService;
    @Resource
    private WorkflowOperationRecordService recordService;

    @DubboReference(check = false, version = "1.0.0")
    private IStockLifeCycleFacade stockLifeCycleFacade;

    /**
     * @param cmd
     * @return
     */
    public Object submit(SubmitCmd cmd) {
        NiceStopWatch stopWatch = new NiceStopWatch(String.format("%s-%s-任务提交", cmd.getBizCode(), cmd.getUseCase()));
        stopWatch.start("任务检查");
        //任务列表检查
        if (CollectionUtils.isEmpty(cmd.getTaskList()) || Boolean.FALSE.equals(cmd.taskListEffective())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.PREVIOUS_STEP_WAIT_COMPLETE);
        }

        List<UserTaskDto> taskList = cmd.getTaskList();

        Assert.isTrue(taskList.stream().map(UserTaskDto::getTaskId).distinct().count() == taskList.size(), "任务重复");

        long count = recordService.count(Wrappers.<WorkflowOperationRecord>lambdaQuery()
                .in(WorkflowOperationRecord::getTaskId, taskList.stream().map(UserTaskDto::getTaskId).collect(Collectors.toList())));
        if (count > 0) {
            log.warn("重复任务:{}", JSONObject.toJSONString(taskList));
            throw new OperationRejectedException(OperationExceptionCodeEnum.TASK_REPEAT_SUBMISSION);
        }

        try {
            BizScenario bizScenario = BizScenario.valueOf(cmd.getBizCode(), cmd.getUseCase());
            // 执行参数转换
            extensionExecutor.executeVoid(SubmitExtPtI.class, bizScenario, extension -> extension.convert(cmd));
            // 执行参数校验
            extensionExecutor.executeVoid(SubmitExtPtI.class, bizScenario, extension -> extension.validate(cmd));
            stopWatch.stop();
            stopWatch.start("业务处理");
            // 执行业务处理
            SubmitResult result = extensionExecutor.execute(SubmitExtPtI.class, bizScenario, extension -> extension.handle(cmd));
            //完成工作流任务
            stopWatch.stop();
            stopWatch.start("流程处理");
            this.completeTask(cmd, result.getWorkflowVar(), stopWatch.getTotalTimeMillis());
            //完成生命周期
            stopWatch.stop();
            stopWatch.start("生命周期处理");
            this.createLifeCycle(result);

            stopWatch.stop();
            log.info("{}", stopWatch.prettyPrint());
            // 记录处理结果
            return result.getBizResult();
        } finally {
            //释放锁

        }
    }


    /**
     * 完成任务
     *
     * @param cmd
     * @param variables
     */
    private void completeTask(SubmitCmd cmd, Map<String, Object> variables, long timeConsuming) {
        try {
            List<UserTaskDto> taskList = cmd.getTaskList();
            List<WorkflowOperationRecord> recordList = taskList.stream()
                    .map(task -> {
                        WorkflowOperationRecord record = new WorkflowOperationRecord();
                        record.setTaskId(task.getTaskId());
                        record.setBizCode(cmd.getBizCode());
                        record.setUseCase(cmd.getUseCase());
                        record.setBusinessKey(task.getBusinessKey());
                        record.setParentBusinessKey(task.getParentBusinessKey());
                        record.setBizState(WorkflowStateEnum.COMPLETE);
                        record.setWorkState(WorkflowStateEnum.INIT);
                        record.setVariables(variables);
                        return record;
                    })
                    .collect(Collectors.toList());
            //保存任务记录
            int len = recordService.insertBatchSomeColumn(recordList);
            //任务异步处理
            this.completeTaskAsync(cmd, taskList, variables, UserContext.getUser(), 3000 - timeConsuming);

            if (len != recordList.size()) {
                throw new RuntimeException("任务记录保存异常");
            }
        } catch (Exception e) {
            log.error("任务提交异常，cmd={},variables={},mgs={}", JSONObject.toJSONString(cmd), JSONObject.toJSON(variables), e.getMessage(), e);
        }
    }


    /**
     * @param cmd
     * @param taskList
     * @param variables
     * @param user
     * @param timeout
     */
    private void completeTaskAsync(SubmitCmd cmd, List<UserTaskDto> taskList, Map<String, Object> variables, LoginUser user, long timeout) {
        //任务异步处理
        Future<String> future = threadPoolTaskExecutor.submit(() -> {
            Map<String, Boolean> resultMap = Collections.EMPTY_MAP;
            try {
                //设置参数
                taskList.forEach(t -> t.setVariables(variables));
                //完成任务
                resultMap = workflowService.completeTask(taskList, user);
            } catch (Exception e) {
                log.error("完成任务流程异常，taskList={},cmd={}", JSONObject.toJSONString(taskList), JSONObject.toJSONString(cmd), e.getMessage(), e);
            } finally {
                Map<String, Boolean> finalResultMap = resultMap;
                taskList.forEach(task -> {
                    WorkflowStateEnum stateEnum = Optional.ofNullable(finalResultMap.get(task.getTaskId()))
                            .map(t -> t ? WorkflowStateEnum.COMPLETE : WorkflowStateEnum.ERROR)
                            .orElse(WorkflowStateEnum.ERROR);
                    if (1 != recordService.updateByTaskId(task.getTaskId(), stateEnum)) {
                        log.error("完成任务流程存储失败，record={},cmd={}}", JSONObject.toJSONString(task), JSONObject.toJSONString(cmd));
                    }

                });
            }
            return "完事了";
        });

        if (timeout > 0) {
            try {
                String result = future.get(timeout, TimeUnit.MILLISECONDS);
                log.info("任务已完成,{} [{}]", result, JSONObject.toJSON(cmd));
                return;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.info("任务异步处理中～ [{}]", JSONObject.toJSON(cmd));
    }


    /**
     * 新增生命周期
     *
     * @param submitResult
     */
    private void createLifeCycle(SubmitResult submitResult) {
        try {
            List<StockLifeCycleCreateRequest> stockLifeCycleCreateRequestList = submitResult.getStockLifeCycleResultList().stream()
                    .map(lifeCycle -> StockLifeCycleCreateRequest.builder()
                            .wno(lifeCycle.getStockWno())
                            .stockId(lifeCycle.getStockId())
                            .originSerialNo(lifeCycle.getOriginSerialNo())
                            .operationDesc(lifeCycle.getOperationDesc())
                            .storeId(ObjectUtils.isNotEmpty(lifeCycle.getStoreId()) ? lifeCycle.getStoreId() : Optional.ofNullable(UserContext.getUser()).map(LoginUser::getStore).map(LoginStore::getId).orElse(null))
                            .operationTime(Optional.ofNullable(lifeCycle.getCreatedTime()).map(Date::getTime).orElse(null))
                            .build()
                    ).collect(Collectors.toList());
            stockLifeCycleFacade.createBatch(stockLifeCycleCreateRequestList);
        } catch (Exception e) {
            log.error("生命周期插入异常，createResult={},{}", JSONObject.toJSONString(submitResult), e.getMessage(), e);
        }
    }
}
