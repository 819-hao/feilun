package com.seeease.flywheel.web.common.work.executor;

import com.alibaba.cola.extension.BizScenario;
import com.alibaba.cola.extension.ExtensionExecutor;
import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.goods.IStockLifeCycleFacade;
import com.seeease.flywheel.goods.request.StockLifeCycleCreateRequest;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.flow.WorkflowStateEnum;
import com.seeease.flywheel.web.common.work.pti.CreateExtPtI;
import com.seeease.flywheel.web.common.work.result.CreateResult;
import com.seeease.flywheel.web.entity.WorkflowStart;
import com.seeease.flywheel.web.infrastructure.external.firework.WorkflowService;
import com.seeease.flywheel.web.infrastructure.service.WorkflowStartService;
import com.seeease.springframework.context.LoginStore;
import com.seeease.springframework.context.LoginUser;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/1/17
 */
@Slf4j
@Component
public class CreateCmdExe {
    @Resource
    private ExtensionExecutor extensionExecutor;
    @Resource
    private WorkflowService workflowService;
    @Resource
    private WorkflowStartService workflowStartService;

    @DubboReference(check = false, version = "1.0.0")
    private IStockLifeCycleFacade stockLifeCycleFacade;

    public Object create(CreateCmd cmd) {
        BizScenario bizScenario = BizScenario.valueOf(cmd.getBizCode(), cmd.getUseCase());
        // 执行参数转换
        extensionExecutor.executeVoid(CreateExtPtI.class, bizScenario, extension -> extension.convert(cmd));
        // 执行参数校验
        extensionExecutor.executeVoid(CreateExtPtI.class, bizScenario, extension -> extension.validate(cmd));
        // 执行业务处理
        CreateResult result = extensionExecutor.execute(CreateExtPtI.class, bizScenario, extension -> extension.handle(cmd));
        // 开启流程
        this.startProcess(cmd, result.getInstanceStart());
        // 执行生命周期
        this.createLifeCycle(result);

        return result.getBizResult();
    }

    /**
     * @param cmd
     * @param instanceStart
     * @return
     */
    private void startProcess(CreateCmd cmd, List<ProcessInstanceStartDto> instanceStart) {
        if (ObjectUtils.isEmpty(instanceStart)) {
            return;
        }
        try {
            for (ProcessInstanceStartDto processInstanceStartDto : instanceStart) {
                WorkflowStart start = new WorkflowStart();
                try {
                    //初始化参数
                    start.setBizCode(processInstanceStartDto.getProcess().getKey());
                    start.setUseCase(cmd.getUseCase());
                    start.setBizState(WorkflowStateEnum.COMPLETE);
                    start.setWorkState(WorkflowStateEnum.INIT);
                    start.setProcessDefinitionKey(processInstanceStartDto.getProcess().getKey());
                    start.setBusinessKey(processInstanceStartDto.getSerialNo());
                    start.setProcessVariables(processInstanceStartDto.getVariables());

                    //启动工作流
                    String processInstanceId = workflowService.startProcess(processInstanceStartDto);
                    //启动完成参数
                    start.setWorkState(WorkflowStateEnum.COMPLETE);
                    start.setProcessInstanceId(processInstanceId);
                } catch (Exception e) {
                    log.error("启动流程失败，cmd={},instanceStart={},{}", JSONObject.toJSONString(cmd), JSONObject.toJSONString(instanceStart), e.getMessage(), e);
                } finally {
                    if (!workflowStartService.save(start)) {
                        log.error("启动流程存储失败cmd={},instanceStart={},start={}", JSONObject.toJSONString(cmd), JSONObject.toJSONString(instanceStart), JSONObject.toJSONString(start));
                    }
                }
            }
        } catch (Exception e) {
            log.error("启动流程异常，cmd={},instanceStart={},{}", JSONObject.toJSONString(cmd), JSONObject.toJSONString(instanceStart), e.getMessage(), e);
        }

    }

    /**
     * 新增生命周期
     */
    private void createLifeCycle(CreateResult createResult) {
        try {
            List<StockLifeCycleResult> stockLifeCycleResultList = createResult.getStockLifeCycleResultList();
            if (CollectionUtils.isEmpty(stockLifeCycleResultList)) {
                return;
            }
            List<StockLifeCycleCreateRequest> request = stockLifeCycleResultList.stream()
                    .map(lifeCycle ->
                            StockLifeCycleCreateRequest.builder()
                                    .wno(lifeCycle.getStockWno())
                                    .stockId(lifeCycle.getStockId())
                                    .originSerialNo(lifeCycle.getOriginSerialNo())
                                    .operationDesc(lifeCycle.getOperationDesc())
                                    .storeId(ObjectUtils.isNotEmpty(lifeCycle.getStoreId()) ? lifeCycle.getStoreId() : Optional.ofNullable(UserContext.getUser()).map(LoginUser::getStore).map(LoginStore::getId).orElse(null))
                                    .createdBy(lifeCycle.getCreatedBy())
                                    .updatedBy(lifeCycle.getCreatedBy())
                                    .createdId(lifeCycle.getCreatedId())
                                    .updatedId(lifeCycle.getCreatedId())
                                    .build()
                    ).collect(Collectors.toList());

            stockLifeCycleFacade.createBatch(request);
        } catch (Exception e) {
            log.error("生命周期插入异常，createResult={},{}", JSONObject.toJSONString(createResult), e.getMessage(), e);
        }
    }
}
