package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.PricingFinishRequest;
import com.seeease.flywheel.pricing.result.PricingFinishResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Mr. Du
 * @Description 定价提交审核
 * @Date create in 2023/1/19 09:49
 */
@Service
@Extension(bizId = BizCode.PRICING, useCase = UseCase.REPAIR_RECEIVING)
public class PricingFinishExt implements SubmitExtPtI<PricingFinishRequest, PricingFinishResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPricingFacade pricingFacade;

    @Override
    public PricingFinishResult submit(SubmitCmd<PricingFinishRequest> cmd) {
        return pricingFacade.finish(cmd.getRequest());
    }

    @Override
    public Map<String, Object> workflowVar(PricingFinishRequest request, PricingFinishResult result) {
        return null;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PricingFinishRequest request, PricingFinishResult result) {
        return Arrays.asList(StockLifeCycleResult.builder()
                .stockId(result.getStockId())
                .originSerialNo(result.getSerialNo())
                .operationDesc(OperationDescConst.PRICING)
                .build());
    }

    @Override
    public Class<PricingFinishRequest> getRequestClass() {
        return PricingFinishRequest.class;
    }

    @Override
    public void validate(SubmitCmd<PricingFinishRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getId()) ||
                StringUtils.isNotBlank(cmd.getRequest().getSerialNo()), "id不能为空");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getTobPrice()) ||
                Objects.nonNull(cmd.getRequest().getTocPrice()), "价格不能为空");
        /**
         * 销售优先等级
         */
        if (ObjectUtils.isEmpty((cmd.getRequest().getSalesPriority()))) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.SALES_PRIORITY_NON_NULL);
        }
        /**
         * 商品等级
         */
        if (StringUtils.isBlank(cmd.getRequest().getGoodsLevel())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.GOODS_LEVEL_NON_NULL);
        }
    }
}
