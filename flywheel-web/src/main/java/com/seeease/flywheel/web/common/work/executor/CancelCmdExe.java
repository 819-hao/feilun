package com.seeease.flywheel.web.common.work.executor;

import com.alibaba.cola.extension.BizScenario;
import com.alibaba.cola.extension.ExtensionExecutor;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.goods.IStockLifeCycleFacade;
import com.seeease.flywheel.goods.request.StockLifeCycleCreateRequest;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.flow.WorkflowStateEnum;
import com.seeease.flywheel.web.common.work.pti.CancelExtPtI;
import com.seeease.flywheel.web.common.work.result.CancelResult;
import com.seeease.flywheel.web.entity.WorkflowStart;
import com.seeease.flywheel.web.infrastructure.external.firework.WorkflowService;
import com.seeease.flywheel.web.infrastructure.service.WorkflowStartService;
import com.seeease.springframework.context.LoginStore;
import com.seeease.springframework.context.LoginUser;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/1/19
 */
@Slf4j
@Component
public class CancelCmdExe {
    @Resource
    private ExtensionExecutor extensionExecutor;
    @Resource
    private WorkflowStartService workflowStartService;
    @Resource
    private WorkflowService workflowService;

    @DubboReference(check = false, version = "1.0.0")
    private IStockLifeCycleFacade stockLifeCycleFacade;

    /**
     * @param cmd
     * @return
     */
    public Object cancel(CancelCmd cmd) {
        BizScenario bizScenario = BizScenario.valueOf(cmd.getBizCode(), cmd.getUseCase());
        // 执行参数转换
        extensionExecutor.executeVoid(CancelExtPtI.class, bizScenario, extension -> extension.convert(cmd));
        // 执行参数校验
        extensionExecutor.executeVoid(CancelExtPtI.class, bizScenario, extension -> extension.validate(cmd));
        // 执行业务处理
        CancelResult result = extensionExecutor.execute(CancelExtPtI.class, bizScenario, extension -> extension.handle(cmd));
        // 删除流程
        this.deleteProcess(result.getBusinessKey());
        //执行生命周期
        this.createLifeCycle(result);

        return result.getBizResult();
    }

    /**
     * @param businessKey
     * @return
     */
    private void deleteProcess(String businessKey) {
        WorkflowStart workflowStart = workflowStartService.getOne(Wrappers.<WorkflowStart>lambdaQuery()
                .eq(WorkflowStart::getBusinessKey, businessKey));
        WorkflowStart up = new WorkflowStart();
        up.setId(workflowStart.getId());
        up.setBizState(WorkflowStateEnum.CANCEL);
        try {
            workflowService.cancelProcess(workflowStart.getProcessInstanceId(), "流程取消");
            up.setWorkState(WorkflowStateEnum.CANCEL);
        } catch (Exception e) {
            log.error("流程取消异常，businessKey={},{}", businessKey, e.getMessage(), e);
        } finally {
            if (!workflowStartService.updateById(up)) {
                log.error("流程取消更新失败，businessKey={}", businessKey);
            }
        }
    }


    /**
     * 新增生命周期
     */
    private void createLifeCycle(CancelResult cancelResult) {
        try {
            List<StockLifeCycleResult> stockLifeCycleResultList = cancelResult.getStockLifeCycleResultList();

            List<StockLifeCycleCreateRequest> collect = stockLifeCycleResultList.stream().map(lifeCycle -> {

                StockLifeCycleCreateRequest stockLifeCycleCreateRequest = new StockLifeCycleCreateRequest();
                stockLifeCycleCreateRequest.setWno(lifeCycle.getStockWno());
                stockLifeCycleCreateRequest.setStockId(lifeCycle.getStockId());
                stockLifeCycleCreateRequest.setOriginSerialNo(lifeCycle.getOriginSerialNo());
                stockLifeCycleCreateRequest.setOperationDesc(lifeCycle.getOperationDesc());
                //todo 可能拿不到用户 UserContext.getUser().getStore().getId()
                stockLifeCycleCreateRequest.setStoreId(ObjectUtils.isNotEmpty(lifeCycle.getStoreId())
                        ? lifeCycle.getStoreId() : Optional.ofNullable(UserContext.getUser()).map(LoginUser::getStore).map(LoginStore::getId).orElse(null));
                stockLifeCycleCreateRequest.setCreatedBy(lifeCycle.getCreatedBy());
                stockLifeCycleCreateRequest.setUpdatedBy(lifeCycle.getCreatedBy());
                stockLifeCycleCreateRequest.setCreatedId(lifeCycle.getCreatedId());
                stockLifeCycleCreateRequest.setUpdatedId(lifeCycle.getCreatedId());

                return stockLifeCycleCreateRequest;
            }).collect(Collectors.toList());

            stockLifeCycleFacade.createBatch(collect);
        } catch (Exception e) {
            log.error("生命周期插入异常，createResult={},{}", JSONObject.toJSONString(cancelResult), e.getMessage(), e);
        }
    }
}