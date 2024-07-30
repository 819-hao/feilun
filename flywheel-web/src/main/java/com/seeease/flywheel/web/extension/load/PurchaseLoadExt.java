package com.seeease.flywheel.web.extension.load;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.purchase.request.PurchaseLoadRequest;
import com.seeease.flywheel.purchase.result.PurchaseLoadResult;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.consts.BusinessMappingProcessEnum;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.pti.CreateExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 采购挂载工作流
 * @Date create in 2023/9/7 15:36
 */
@Service
@Slf4j
@Extension(bizId = BizCode.PURCHASE, useCase = UseCase.PROCESS_LOAD)
public class PurchaseLoadExt implements CreateExtPtI<PurchaseLoadRequest, PurchaseLoadResult> {
    @Override
    public PurchaseLoadResult create(CreateCmd<PurchaseLoadRequest> cmd) {

        return PurchaseLoadResult.builder().build();
    }

    @Override
    public List<ProcessInstanceStartDto> start(PurchaseLoadRequest request, PurchaseLoadResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        //总部 && 门店
        switch (request.getStoreId()) {
            case FlywheelConstant._ZB_ID:
                break;
            default:
                workflowVar.put(VariateDefinitionKeyEnum.SHORT_CODES.getKey(), request.getShortcodes());
                break;
        }
        return Arrays.asList(ProcessInstanceStartDto.builder()
                .serialNo(request.getSerialNo())
                .variables(workflowVar)
                .process(BusinessMappingProcessEnum.fromValue(request.getBusinessKey()))
                .build());
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PurchaseLoadRequest request, PurchaseLoadResult result) {
        return request.getLine().stream().map(s -> StockLifeCycleResult.builder()
                .originSerialNo(request.getSerialNo())
                .stockWno(s).operationDesc(OperationDescConst.PURCHASE_CREATE).build()).collect(Collectors.toList());

    }

    @Override
    public Class<PurchaseLoadRequest> getRequestClass() {
        return PurchaseLoadRequest.class;
    }

    @Override
    public void validate(CreateCmd<PurchaseLoadRequest> cmd) {
        Assert.notNull(cmd, "请求不能为空");
        Assert.notNull(cmd.getRequest(), "请求入参不能为空");
        Assert.notNull(cmd.getRequest().getSerialNo(), "请求入参不能为空");
        Assert.notNull(cmd.getRequest().getBusinessKey(), "请求入参不能为空");
        Assert.notNull(cmd.getRequest().getStoreId(), "请求入参不能为空");
        Assert.notNull(cmd.getRequest().getShortcodes(), "请求入参不能为空");

        List<String> line = cmd.getRequest().getLine();

        Assert.isTrue(CollectionUtils.isNotEmpty(line) &&
                line.stream().allMatch(Objects::nonNull), "商品编码不能为空");
    }
}
