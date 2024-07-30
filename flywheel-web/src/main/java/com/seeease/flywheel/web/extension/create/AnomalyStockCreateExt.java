package com.seeease.flywheel.web.extension.create;

import com.alibaba.cola.extension.Extension;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.seeease.flywheel.anomaly.IAnomalyFacade;
import com.seeease.flywheel.anomaly.request.AnomalyStockCreateRequest;
import com.seeease.flywheel.anomaly.result.AnomalyStockCreateResult;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.ProcessDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.pti.CreateExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 异常商品创建
 * @Date create in 2023/4/12 11:20
 */
@Service
@Extension(bizId = BizCode.STOCK, useCase = UseCase.PROCESS_CREATE)
public class AnomalyStockCreateExt implements CreateExtPtI<AnomalyStockCreateRequest, AnomalyStockCreateResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IAnomalyFacade anomalyFacade;

    @Override
    public AnomalyStockCreateResult create(CreateCmd<AnomalyStockCreateRequest> cmd) {
        return anomalyFacade.create(cmd.getRequest());
    }

    @Override
    public List<ProcessInstanceStartDto> start(AnomalyStockCreateRequest request, AnomalyStockCreateResult result) {

        if (request.getDirect().intValue() == 0) {
            return result.getList().stream().map(anomalyStockCreateResultDto -> {

                Map<String, Object> workflowVar = new HashMap<>();
                workflowVar.put(VariateDefinitionKeyEnum.STORE_WORK_SERIAL_NO_LIST.getKey(), Arrays.asList(anomalyStockCreateResultDto.getSerialNo()));

                return ProcessInstanceStartDto.builder()
                        .serialNo(anomalyStockCreateResultDto.getParentSerialNo())
                        .variables(workflowVar)
                        .process(ProcessDefinitionKeyEnum.EXCEPTION_STOCK_HANDLE)
                        .build();

            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(AnomalyStockCreateRequest request, AnomalyStockCreateResult result) {

        if (request.getDirect().intValue() == 0) {
            return result.getList().stream().map(anomalyStockCreateResultDto -> StockLifeCycleResult.builder()
                    .stockId(anomalyStockCreateResultDto.getStockId())
                    .originSerialNo(anomalyStockCreateResultDto.getParentSerialNo())
                    .operationDesc(String.format(OperationDescConst.EXCEPTION_STOCK_HANDLE, OperationDescConst.FIX))
                    .build()).collect(Collectors.toList());
        } else if (request.getDirect().intValue() == 1) {
            return result.getList().stream().map(anomalyStockCreateResultDto -> StockLifeCycleResult.builder()
                    .stockId(anomalyStockCreateResultDto.getStockId())
                    .operationDesc(String.format(OperationDescConst.EXCEPTION_STOCK_HANDLE, OperationDescConst.NORMAL))
                    .build()).collect(Collectors.toList());
        }

        return null;
    }


    @Override
    public void validate(CreateCmd<AnomalyStockCreateRequest> cmd) {
        Assert.notNull(cmd, "参数不能为空");
        Assert.notNull(cmd.getRequest().getStockIdList(), "商品不能为空");
        Assert.notNull(cmd.getRequest().getDirect(), "动作不能为空");
        Assert.isTrue(cmd.getRequest().getStockIdList().stream().allMatch(t -> Objects.nonNull(t)), "商品id不能为空");
    }

    @Override
    public Class<AnomalyStockCreateRequest> getRequestClass() {
        return AnomalyStockCreateRequest.class;
    }

}
