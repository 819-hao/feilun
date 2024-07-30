package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.PricingFinishBatchRequest;
import com.seeease.flywheel.pricing.result.PricingFinishBatchResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 定价提交审核
 * @Date create in 2023/1/19 09:49
 */
@Service
@Extension(bizId = BizCode.PRICING, useCase = UseCase.BATCH_PRICING)
public class PricingFinishBatchExt implements SubmitExtPtI<PricingFinishBatchRequest, PricingFinishBatchResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPricingFacade pricingFacade;

    @Override
    public PricingFinishBatchResult submit(SubmitCmd<PricingFinishBatchRequest> cmd) {

        return pricingFacade.finishBatch(cmd.getRequest());
    }

    @Override
    public Map<String, Object> workflowVar(PricingFinishBatchRequest request, PricingFinishBatchResult result) {
        return null;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PricingFinishBatchRequest request, PricingFinishBatchResult result) {
        return result.getResultList()
                .stream()
                .map(t -> StockLifeCycleResult.builder()
                        .stockId(t.getStockId())
                        .originSerialNo(t.getSerialNo())
                        .operationDesc(OperationDescConst.PRICING)
                        .build())
                .collect(Collectors.toList());

    }

    @Override
    public Class<PricingFinishBatchRequest> getRequestClass() {
        return PricingFinishBatchRequest.class;
    }

    @Override
    public void validate(SubmitCmd<PricingFinishBatchRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getRequestList()), "参数不能为空");

        cmd.getRequest().getRequestList().forEach(t -> {
            Assert.isTrue(Objects.nonNull(t.getId()) ||
                    StringUtils.isNotBlank(t.getSerialNo()), "id不能为空");
            Assert.isTrue(Objects.nonNull(t.getTobPrice()) ||
                    Objects.nonNull(t.getTocPrice()), "价格不能为空");
            /**
             * 销售优先等级
             */
            if (ObjectUtils.isEmpty(t.getSalesPriority())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.SALES_PRIORITY_NON_NULL);
            }
            /**
             * 商品等级
             */
            if (StringUtils.isBlank(t.getGoodsLevel())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.GOODS_LEVEL_NON_NULL);
            }
        });

    }
}
