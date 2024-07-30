package com.seeease.flywheel.web.common.work.executor;

import com.alibaba.cola.extension.BizScenario;
import com.alibaba.cola.extension.ExtensionExecutor;
import com.seeease.flywheel.web.common.context.NiceStopWatch;
import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.pti.QueryExtPtI;
import com.seeease.flywheel.web.common.work.result.QueryListResult;
import com.seeease.flywheel.web.common.work.result.QueryPageResult;
import com.seeease.flywheel.web.common.work.result.QueryResult;
import com.seeease.flywheel.web.common.work.result.QuerySingleResult;
import com.seeease.flywheel.web.infrastructure.external.firework.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 工作详情
 *
 * @author trio
 * @date 2023/1/15
 */
@Slf4j
@Component
public class QueryCmdExe {
    @Resource
    private ExtensionExecutor extensionExecutor;
    @Resource
    private WorkflowService workflowService;

    /**
     * @param cmd
     * @return
     */
    public QueryResult query(QueryCmd cmd) {
        NiceStopWatch stopWatch = new NiceStopWatch(String.format("%s-%s-工作详情", cmd.getBizCode(), cmd.getUseCase()));
        stopWatch.start("参数校验");
        BizScenario bizScenario = BizScenario.valueOf(cmd.getBizCode(), cmd.getUseCase());
        // 执行参数转换
        extensionExecutor.executeVoid(QueryExtPtI.class, bizScenario, extension -> extension.convert(cmd));
        // 执行参数校验
        extensionExecutor.executeVoid(QueryExtPtI.class, bizScenario, extension -> extension.validate(cmd));
        stopWatch.stop();
        stopWatch.start("业务查询");
        //获取需要流程
        List<TaskDefinitionKeyEnum> taskDefinitionKeyEnums = extensionExecutor.execute(QueryExtPtI.class, bizScenario, extension -> extension.needQueryTaskKeys(cmd));
        // 执行详情获取
        QueryResult result = extensionExecutor.execute(QueryExtPtI.class, bizScenario, extension -> extension.query(cmd));
        stopWatch.stop();
        //查用户任务
        stopWatch.start("任务查询");
        this.queryUserTask(result, cmd.isQueryTask(), taskDefinitionKeyEnums);
        stopWatch.stop();
        log.info("{}", stopWatch.prettyPrint());
        return result;
    }

    /**
     * 查任务
     *
     * @param result
     */
    private void queryUserTask(QueryResult result, boolean queryTask, List<TaskDefinitionKeyEnum> taskDefinitionKeyEnums) {
        if (CollectionUtils.isEmpty(taskDefinitionKeyEnums)) {
            return;
        }
        if (result instanceof QuerySingleResult) {
            this.setTask((QuerySingleResult) result, queryTask, taskDefinitionKeyEnums);
        } else if (result instanceof QueryListResult) {
            QueryListResult listResult = (QueryListResult) result;
            setTask(listResult.getResultList(), queryTask, taskDefinitionKeyEnums);
        } else if (result instanceof QueryPageResult) {
            QueryPageResult pageResult = (QueryPageResult) result;
            setTask(pageResult.getResultList(), queryTask, taskDefinitionKeyEnums);
        } else {
            throw new RuntimeException("未知的结果类型");
        }
    }

    /**
     * 查任务
     *
     * @param result
     */
    private void setTask(QuerySingleResult result, boolean queryTask, List<TaskDefinitionKeyEnum> taskDefinitionKeyEnums) {
        if (Objects.isNull(result.getTask())) {
            return;
        }
        UserTaskDto task = result.getTask();
        result.setTask(null);
        if (queryTask && CollectionUtils.isNotEmpty(taskDefinitionKeyEnums)) {
            try {
                result.setTask(workflowService.queryTask(task, taskDefinitionKeyEnums
                        .stream()
                        .map(TaskDefinitionKeyEnum::getKey)
                        .collect(Collectors.toList()))
                );
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

    }

    /**
     * @param resultList
     * @param queryTask
     * @param taskDefinitionKeyEnums
     */
    private void setTask(List<QuerySingleResult> resultList, boolean queryTask, List<TaskDefinitionKeyEnum> taskDefinitionKeyEnums) {
        try {
            if (CollectionUtils.isEmpty(resultList) || CollectionUtils.isEmpty(taskDefinitionKeyEnums)) {
                return;
            }
            //不查询任务
            if (!queryTask) {
                resultList.forEach(t -> t.setTask(null));
                return;
            }

            Assert.isTrue(resultList.stream()
                    .allMatch(t -> Optional.ofNullable(t.getTask())
                            .map(UserTaskDto::getBusinessKey)
                            .filter(StringUtils::isNotBlank)
                            .isPresent()), "任务参数不能为空");

            List<UserTaskDto> taskList = resultList.stream()
                    .map(QuerySingleResult::getTask)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(taskList)) {
                return;
            }
            Map<String, UserTaskDto> taskDtoMap = workflowService.queryTaskList(taskList, taskDefinitionKeyEnums
                    .stream()
                    .map(TaskDefinitionKeyEnum::getKey)
                    .collect(Collectors.toList()));
            //设置任务
            resultList.forEach(t -> t.setTask(taskDtoMap.get(t.getTask().getBusinessKey())));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
