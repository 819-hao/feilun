package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.TextRobotMessage;
import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.PricingCheckBatchPassRequest;
import com.seeease.flywheel.pricing.result.PricingCompletedResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.utils.StrFormatterUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author Mr. Du
 * @Description 定价批量通过
 * @Date create in 2023/1/19 09:49
 */
@Service
@Extension(bizId = BizCode.PRICING, useCase = UseCase.BATCH_PASS)
public class PricingCheckBatchPassExt implements SubmitExtPtI<PricingCheckBatchPassRequest, List<PricingCompletedResult>> {

    @DubboReference(check = false, version = "1.0.0")
    private IPricingFacade pricingFacade;

    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    @NacosValue(value = "${robot.newStockNotify:2d798d3a-62a3-458e-b361-b327a6d8df0f}", autoRefreshed = true)
    private String newStockRobot;

    @Override
    public List<PricingCompletedResult> submit(SubmitCmd<PricingCheckBatchPassRequest> cmd) {

        List<PricingCompletedResult> resultList = pricingFacade.batchPass(cmd.getRequest().getPricingIdList());
        if (CollectionUtils.isNotEmpty(resultList)){
            for (PricingCompletedResult result : resultList) {
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
            }
        }

        return resultList;
    }

    @Override
    public Map<String, Object> workflowVar(PricingCheckBatchPassRequest request, List<PricingCompletedResult> result) {
        Map<String, Object> workflowVar = new HashMap<>();
        workflowVar.put(VariateDefinitionKeyEnum.CHECK_STATE.getKey(), WhetherEnum.YES.getValue());

        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PricingCheckBatchPassRequest request, List<PricingCompletedResult> result) {
        return result.stream()
                .map(pricingCompletedResult -> StockLifeCycleResult.builder()
                        .stockId(pricingCompletedResult.getStockId())
                        .originSerialNo(pricingCompletedResult.getSerialNo())
                        .operationDesc(String.format(OperationDescConst.PRICING_CHECK, OperationDescConst.PRICING_CHECK_PASS))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Class<PricingCheckBatchPassRequest> getRequestClass() {
        return PricingCheckBatchPassRequest.class;
    }

    @Override
    public void validate(SubmitCmd<PricingCheckBatchPassRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getPricingIdList()), "作业id集合不能为空");
        Assert.isTrue(cmd.getRequest().getPricingIdList().size() == cmd.getTaskList().size(), "业务数量和任务数量不一致");
    }
}
