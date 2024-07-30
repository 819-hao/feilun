package com.seeease.flywheel.web.extension.cancel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.PricingCancelRequest;
import com.seeease.flywheel.pricing.result.PricingCancelResult;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.pti.CancelExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/2
 */
@Service
@Extension(bizId = BizCode.PRICING, useCase = UseCase.CANCEL)
public class PricingCancelExt implements CancelExtPtI<PricingCancelRequest, PricingCancelResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPricingFacade facade;

    @Override
    public PricingCancelResult cancel(CancelCmd<PricingCancelRequest> cmd) {
        return facade.cancel(cmd.getRequest());
    }

    @Override
    public String businessKey(PricingCancelResult result) {
        return result.getSerialNo();
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PricingCancelRequest request, PricingCancelResult result) {
        return Arrays.asList(StockLifeCycleResult.builder()
                //都是异步的处理
                .storeId(request.getStoreId())
                .stockId(result.getStockId())
                .originSerialNo(result.getSerialNo())
                .operationDesc(OperationDescConst.PRICING_CANCEL)
                //都是异步的处理
                .createdBy(request.getCreatedBy())
                .createdId(request.getCreatedId())
                .build());
    }

    @Override
    public Class<PricingCancelRequest> getRequestClass() {
        return PricingCancelRequest.class;
    }

    @Override
    public void validate(CancelCmd<PricingCancelRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
    }
}
