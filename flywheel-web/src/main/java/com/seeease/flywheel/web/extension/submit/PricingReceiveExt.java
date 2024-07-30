package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.TextRobotMessage;
import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.PricingCompletedRequest;
import com.seeease.flywheel.pricing.result.PricingCompletedResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.utils.StrFormatterUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author Mr. Du
 * @Description 定价审核完成
 * @Date create in 2023/1/19 09:49
 */
@Service
@Extension(bizId = BizCode.PRICING, useCase = UseCase.REPAIR_COMPLETED)
public class PricingReceiveExt implements SubmitExtPtI<PricingCompletedRequest, PricingCompletedResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPricingFacade pricingFacade;

    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    @NacosValue(value = "${robot.newStockNotify:2d798d3a-62a3-458e-b361-b327a6d8df0f}", autoRefreshed = true)
    private String newStockRobot;

    @Override
    public PricingCompletedResult submit(SubmitCmd<PricingCompletedRequest> cmd) {

        PricingCompletedResult result = pricingFacade.completed(cmd.getRequest());

        if (ObjectUtils.isNotEmpty(result.getPriceMessage())) {
            wxCpMessageFacade.send(TextRobotMessage.builder()
                    .key(newStockRobot)
                    .text(TextRobotMessage.Text.builder()
                            .content(StrFormatterUtil.format("【价格异常告警】\n【告警原因：新表价格不一致】\n【品牌：{}】\n【系列：{}】\n【型号：{}】\n【成色：S级/99新】\n【{}】",
                                    result.getPriceMessage().getBrandName(), result.getPriceMessage().getSeriesName(), result.getPriceMessage().getModel(), result.getPriceMessage().getLineMsg()))
                            .mentioned_list(
                                    Stream.of(Lists.newArrayList("@all"))
                                            .flatMap(Collection::stream)
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toList()))
                            .build())
                    .build());
        }

        return result;
    }

    @Override
    public Map<String, Object> workflowVar(PricingCompletedRequest request, PricingCompletedResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        workflowVar.put(VariateDefinitionKeyEnum.CHECK_STATE.getKey(), request.getCheckState());

        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PricingCompletedRequest request, PricingCompletedResult result) {
        return Arrays.asList(StockLifeCycleResult.builder()
                .stockId(result.getStockId())
                .originSerialNo(result.getSerialNo())
                .operationDesc(request.getCheckState().intValue() == OperationDescConst.PRICING_CHECK_PASS_VALUE
                        ? String.format(OperationDescConst.PRICING_CHECK, OperationDescConst.PRICING_CHECK_PASS) : String.format(OperationDescConst.PRICING_CHECK, OperationDescConst.PRICING_CHECK_ERROR))
                .build());
    }

    @Override
    public Class<PricingCompletedRequest> getRequestClass() {
        return PricingCompletedRequest.class;
    }

    @Override
    public void validate(SubmitCmd<PricingCompletedRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getId()) ||
                StringUtils.isNotBlank(cmd.getRequest().getSerialNo()), "id不能为空");
    }
}
