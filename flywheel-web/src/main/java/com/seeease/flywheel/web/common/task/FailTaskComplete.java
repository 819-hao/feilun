package com.seeease.flywheel.web.common.task;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.flow.WorkflowStateEnum;
import com.seeease.flywheel.web.entity.WorkflowOperationRecord;
import com.seeease.flywheel.web.infrastructure.external.firework.WorkflowService;
import com.seeease.flywheel.web.infrastructure.service.WorkflowOperationRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 工作流任务补偿
 *
 * @author Tiro
 * @date 2023/4/18
 */
@Slf4j
@Component
public class FailTaskComplete {
    @Resource
    private WorkflowService workflowService;
    @Resource
    private WorkflowOperationRecordService recordService;

    /**
     * 每1分钟执行一次
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public synchronized void completeTask() {
        List<WorkflowOperationRecord> recordList = recordService.page(Page.of(1, 100), Wrappers.<WorkflowOperationRecord>lambdaQuery()
                        .eq(WorkflowOperationRecord::getBizState, WorkflowStateEnum.COMPLETE)
                        .le(WorkflowOperationRecord::getCreatedTime, DateUtils.addMinutes(new Date(), -5))
                        .ne(WorkflowOperationRecord::getWorkState, WorkflowStateEnum.COMPLETE))
                .getRecords();

        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }

        Map<String, Boolean> resultMap = workflowService.completeTask(recordList.stream()
                .map(record -> UserTaskDto.builder()
                        .taskId(record.getTaskId())
                        .businessKey(record.getBusinessKey())
                        .parentBusinessKey(record.getParentBusinessKey())
                        .variables(record.getVariables())
                        .build())
                .collect(Collectors.toList()), null);

        log.info("任务补偿结果，resultMap={}}", JSONObject.toJSONString(resultMap));

        recordList.forEach(record -> {
            try {
                WorkflowStateEnum stateEnum = Optional.ofNullable(resultMap.get(record.getTaskId()))
                        .map(t -> t ? WorkflowStateEnum.COMPLETE : WorkflowStateEnum.ERROR)
                        .orElse(WorkflowStateEnum.ERROR);
                if (1 != recordService.updateByTaskId(record.getTaskId(), stateEnum)) {
                    log.error("任务补偿完成失败，record={},resultMap={}}", JSONObject.toJSONString(record), JSONObject.toJSONString(resultMap));
                }
            } catch (Exception e) {
                log.error("任务补偿完成任务流程异常，record={},{}", JSONObject.toJSONString(record), e.getMessage(), e);
            }
        });
    }
}
