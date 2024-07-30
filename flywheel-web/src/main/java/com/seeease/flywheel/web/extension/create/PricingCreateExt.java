package com.seeease.flywheel.web.extension.create;

import com.alibaba.cola.extension.Extension;
import com.alibaba.fastjson.JSON;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.PricingCreateRequest;
import com.seeease.flywheel.pricing.result.PricingCreateResult;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.ProcessDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.pti.CreateExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/1/13
 */
@Service
@Slf4j
@Extension(bizId = BizCode.PRICING, useCase = UseCase.PROCESS_CREATE)
public class PricingCreateExt implements CreateExtPtI<PricingCreateRequest, PricingCreateResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPricingFacade pricingFacade;

    @Override
    public Class<PricingCreateRequest> getRequestClass() {
        return PricingCreateRequest.class;
    }

    @Override
    public void validate(CreateCmd<PricingCreateRequest> cmd) {

    }

    @Override
    public PricingCreateResult create(CreateCmd<PricingCreateRequest> cmd) {
        log.info("定价开启cmd:{}", JSON.toJSONString(cmd));
        return pricingFacade.create(cmd.getRequest());
    }

    @Override
    public List<ProcessInstanceStartDto> start(PricingCreateRequest request, PricingCreateResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        workflowVar.put(VariateDefinitionKeyEnum.PRICING_AUTO.getKey(), result.getAuto());
        log.info("定价开启workflowVar:{}", JSON.toJSONString(workflowVar));
        log.info("定价开启result:{}", JSON.toJSONString(result));
        return Arrays.asList(ProcessInstanceStartDto.builder()
                .serialNo(result.getSerialNo())
                .process(ProcessDefinitionKeyEnum.PRICING)
                .variables(workflowVar)
                .build());
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PricingCreateRequest request, PricingCreateResult result) {
        StockLifeCycleResult.StockLifeCycleResultBuilder builder = StockLifeCycleResult.builder();
        try {
            if (request.getAuto()) {
                builder = builder.stockId(result.getStockId())
                        .originSerialNo(result.getSerialNo())
                        .createdBy(request.getCreatedBy())
                        .createdId(request.getCreatedId())
                        .storeId(request.getStoreId())
                        .operationDesc(String.format(OperationDescConst.PRICING_CREATE, OperationDescConst.NEW_AUTO));
            } else {
                if (request.getAgain() && request.getCancel()) {
                    //取消后重启
                    builder = builder.stockId(result.getStockId()).originSerialNo(result.getSerialNo()).operationDesc(String.format(OperationDescConst.PRICING_CREATE, OperationDescConst.RESTART));
                } else if (request.getAgain() && !request.getCancel()) {
                    //重启
                    builder = builder.stockId(result.getStockId()).originSerialNo(result.getSerialNo()).operationDesc(String.format(OperationDescConst.PRICING_CREATE, OperationDescConst.RESTART));
                } else {
                    //首次定价
                    builder = builder.stockId(result.getStockId())
                            .originSerialNo(result.getSerialNo())
                            .createdBy(request.getCreatedBy())
                            .createdId(request.getCreatedId())
                            .storeId(request.getStoreId())
                            .operationDesc(String.format(OperationDescConst.PRICING_CREATE, OperationDescConst.NEW));
                }
            }
        } catch (Exception e) {
            log.error("定价开启生命周期,{}", e.getMessage(), e);
        }
        return Arrays.asList(builder.build());
    }
}
