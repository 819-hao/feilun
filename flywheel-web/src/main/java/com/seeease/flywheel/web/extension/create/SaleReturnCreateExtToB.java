package com.seeease.flywheel.web.extension.create;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.sale.ISaleReturnOrderFacade;
import com.seeease.flywheel.sale.request.SaleReturnOrderCreateRequest;
import com.seeease.flywheel.sale.result.SaleReturnOrderCreateResult;
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
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Service
@Extension(bizId = BizCode.TO_B_SALE_RETURN, useCase = UseCase.PROCESS_CREATE)
public class SaleReturnCreateExtToB implements CreateExtPtI<SaleReturnOrderCreateRequest, SaleReturnOrderCreateResult> {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleReturnOrderFacade facade;

    @Override
    public Class<SaleReturnOrderCreateRequest> getRequestClass() {
        return SaleReturnOrderCreateRequest.class;
    }

    @Override
    public void validate(CreateCmd<SaleReturnOrderCreateRequest> cmd) {
        Assert.notNull(cmd, "id不能为空");
    }


    @Override
    public SaleReturnOrderCreateResult create(CreateCmd<SaleReturnOrderCreateRequest> cmd) {
        return facade.create(cmd.getRequest());
    }

    @Override
    public List<ProcessInstanceStartDto> start(SaleReturnOrderCreateRequest request, SaleReturnOrderCreateResult result) {
        return result.getList()
                .stream()
                .map(t -> {
                    Map<String, Object> workflowVar = new HashMap<>();
                    workflowVar.put(VariateDefinitionKeyEnum.LOCATION_ID.getKey(), t.getDeliveryLocationId());
                    workflowVar.put(VariateDefinitionKeyEnum.SHORT_CODES.getKey(), t.getShortcodes());
                    workflowVar.put(VariateDefinitionKeyEnum.LINE.getKey(), 1);

                    return ProcessInstanceStartDto.builder()
                            .serialNo(t.getSerialNo())
                            .process(ProcessDefinitionKeyEnum.TO_B_SALE_RETURN)
                            .variables(workflowVar)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(SaleReturnOrderCreateRequest request, SaleReturnOrderCreateResult result) {
        return result.getList()
                .stream()
                .map(t -> t.getStockIdList()
                        .stream()
                        .map(id -> StockLifeCycleResult.builder()
                                .originSerialNo(t.getSerialNo())
                                .stockId(id)
                                .operationDesc(String.format(OperationDescConst.SALE_RETURN_CREATE, OperationDescConst.SALE_RETURN_CREATE_TH))
                                .build())
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
