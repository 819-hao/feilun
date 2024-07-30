package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.sale.ISaleReturnOrderFacade;
import com.seeease.flywheel.sale.request.SaleReturnOrderExpressNumberUploadRequest;
import com.seeease.flywheel.sale.result.SaleReturnOrderExpressNumberUploadResult;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@Extension(bizId = BizCode.TO_B_SALE_RETURN, useCase = UseCase.UPLOAD_EXPRESS_NUMBER)
public class SaleReturnOrderExpressNumberUploadExtToB implements SubmitExtPtI<SaleReturnOrderExpressNumberUploadRequest, SaleReturnOrderExpressNumberUploadResult> {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleReturnOrderFacade facade;

    @Override
    public Class<SaleReturnOrderExpressNumberUploadRequest> getRequestClass() {
        return SaleReturnOrderExpressNumberUploadRequest.class;
    }

    @Override
    public void validate(SubmitCmd<SaleReturnOrderExpressNumberUploadRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.notNull(cmd.getRequest().getSaleReturnId(), "id不能为空");
        Assert.isTrue(StringUtils.isNotBlank(cmd.getRequest().getExpressNumber()), " 快递单号");
    }

    @Override
    public SaleReturnOrderExpressNumberUploadResult submit(SubmitCmd<SaleReturnOrderExpressNumberUploadRequest> cmd) {
        cmd.getRequest().setStoreId(UserContext.getUser().getStore().getId());

        return facade.uploadExpressNumber(cmd.getRequest());
    }

    @Override
    public Map<String, Object> workflowVar(SaleReturnOrderExpressNumberUploadRequest request, SaleReturnOrderExpressNumberUploadResult result) {
        Map<String, Object> workflowVar = new HashMap<>();

        workflowVar.put(VariateDefinitionKeyEnum.SALE_WORK_SERIAL_NO_LIST.getKey(), result.getStoreWorkList()
                .stream()
                .map(StoreWorkCreateResult::getSerialNo)
                .collect(Collectors.toList())
        );
        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(SaleReturnOrderExpressNumberUploadRequest request, SaleReturnOrderExpressNumberUploadResult result) {
        return result.getStoreWorkList().stream()
                .map(storeWorkCreateResult -> StockLifeCycleResult.builder()
                        .stockId(storeWorkCreateResult.getStockId())
                        .originSerialNo(result.getSerialNo())
                        .operationDesc(String.format(OperationDescConst.SALE_UPLOAD_EXPRESS, OperationDescConst.SALE_RETURN_CREATE_TH))
                        .build())
                .collect(Collectors.toList());
    }
}
