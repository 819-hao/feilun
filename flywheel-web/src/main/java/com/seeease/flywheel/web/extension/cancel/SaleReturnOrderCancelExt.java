package com.seeease.flywheel.web.extension.cancel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.sale.ISaleReturnOrderFacade;
import com.seeease.flywheel.sale.request.SaleReturnOrderCancelRequest;
import com.seeease.flywheel.sale.result.SaleReturnOrderCancelResult;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.pti.CancelExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/2/2
 */
@Service
@Extension(bizId = BizCode.TO_C_SALE_RETURN, useCase = UseCase.CANCEL)
public class SaleReturnOrderCancelExt implements CancelExtPtI<SaleReturnOrderCancelRequest, SaleReturnOrderCancelResult> {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleReturnOrderFacade facade;

    @Override
    public SaleReturnOrderCancelResult cancel(CancelCmd<SaleReturnOrderCancelRequest> cmd) {
        return facade.cancel(cmd.getRequest());
    }

    @Override
    public String businessKey(SaleReturnOrderCancelResult result) {
        return result.getSerialNo();
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(SaleReturnOrderCancelRequest request, SaleReturnOrderCancelResult result) {
        return result.getLine().stream().map(saleReturnOrderLineVO -> StockLifeCycleResult.builder()
                .stockId(saleReturnOrderLineVO.getStockId())
                .originSerialNo(result.getSerialNo())
                .operationDesc(String.format(OperationDescConst.SALE_RETURN_CANCEL, OperationDescConst.SALE_RETURN_CREATE_GR))
                .build()).collect(Collectors.toList());
    }

    @Override
    public Class<SaleReturnOrderCancelRequest> getRequestClass() {
        return SaleReturnOrderCancelRequest.class;
    }

    @Override
    public void validate(CancelCmd<SaleReturnOrderCancelRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.notNull(cmd.getRequest().getId(), "id不能为空");
    }
}
