package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.pricing.request.PricingCreateRequest;
import com.seeease.flywheel.qt.IQualityTestingFacade;
import com.seeease.flywheel.qt.request.QualityTestingBatchPassRequest;
import com.seeease.flywheel.qt.result.QualityTestingDecisionListResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.BusinessMappingProcessEnum;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.event.PricingStartEvent;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 质检判定
 * @Date create in 2023/1/19 09:49
 */
@Service
@Extension(bizId = BizCode.QT, useCase = UseCase.BATCH_PASS)
@Slf4j
public class QualityTestingBatchPassExt implements SubmitExtPtI<QualityTestingBatchPassRequest, List<QualityTestingDecisionListResult>> {

    @DubboReference(check = false, version = "1.0.0")
    private IQualityTestingFacade qualityTestingFacade;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public List<QualityTestingDecisionListResult> submit(SubmitCmd<QualityTestingBatchPassRequest> cmd) {

        return qualityTestingFacade.batchPass(cmd.getRequest().getQualityTestingIdList());
    }

    @Override
    public Map<String, Object> workflowVar(QualityTestingBatchPassRequest request, List<QualityTestingDecisionListResult> result) {
        Map<String, Object> workflowVar = new HashMap<>();
        workflowVar.put(VariateDefinitionKeyEnum.QT_STATE.getKey(), OperationDescConst.LOGISTICS_RECEIVING_PASS_VALUE);

        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(QualityTestingBatchPassRequest request, List<QualityTestingDecisionListResult> result) {
        try {
//            result.forEach(this::startProcess);
            startProcess(result.stream().filter(qualityTestingDecisionListResult -> (Arrays.asList(
                            BusinessMappingProcessEnum.TH_CG_BH.getBusinessKey(),
                            BusinessMappingProcessEnum.TH_CG_DJ.getBusinessKey(),
                            BusinessMappingProcessEnum.TH_CG_PL.getBusinessKey(),
                            BusinessMappingProcessEnum.TH_JS.getBusinessKey(),
                            BusinessMappingProcessEnum.TH_CG_QK.getBusinessKey(),
                            BusinessMappingProcessEnum.TH_CG_DJTP.getBusinessKey(),
                            BusinessMappingProcessEnum.GR_JS.getBusinessKey(),
                            BusinessMappingProcessEnum.GR_HG_ZH.getBusinessKey(),
                            BusinessMappingProcessEnum.GR_HG_JHS.getBusinessKey()

                    ).contains(qualityTestingDecisionListResult.getWorkSource()))

                            || ((Arrays.asList(BusinessMappingProcessEnum.GR_HS_JHS.getBusinessKey(),
                            BusinessMappingProcessEnum.GR_HS_ZH.getBusinessKey()
                            //个人回购批量通过时，肯定有定价单了
                    ).contains(
                            qualityTestingDecisionListResult.getWorkSource())
                    ) && StringUtils.isBlank(qualityTestingDecisionListResult.getFixSerialNo()))


            ).collect(Collectors.toList()));
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }

        return result.stream().map(qualityTestingDecisionListResult -> StockLifeCycleResult.builder()
                .originSerialNo(qualityTestingDecisionListResult.getSerialNo())
                .stockId(qualityTestingDecisionListResult.getStockId())
                .operationDesc(String.format(OperationDescConst.QUALITY_TESTING, OperationDescConst.LOGISTICS_RECEIVING_PASS))
                .build()).collect(Collectors.toList());
    }

    @Override
    public Class<QualityTestingBatchPassRequest> getRequestClass() {
        return QualityTestingBatchPassRequest.class;
    }

    @Override
    public void validate(SubmitCmd<QualityTestingBatchPassRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getQualityTestingIdList()), "作业id集合不能为空");
        Assert.isTrue(cmd.getRequest().getQualityTestingIdList().size() == cmd.getTaskList().size(), "业务数量和任务数量不一致");
    }

    /**
     * 开启自动定价流程
     *
     * @param list
     */
    private void startProcess(List<QualityTestingDecisionListResult> list) {

        try {
            applicationContext.publishEvent(new PricingStartEvent(this, list.stream().filter(data -> Arrays.asList(
                    BusinessMappingProcessEnum.TH_CG_DJ.getBusinessKey(),
                    BusinessMappingProcessEnum.TH_CG_BH.getBusinessKey(),
                    BusinessMappingProcessEnum.TH_CG_PL.getBusinessKey(),
                    BusinessMappingProcessEnum.TH_JS.getBusinessKey(),
                    BusinessMappingProcessEnum.GR_HS_JHS.getBusinessKey(),
                    BusinessMappingProcessEnum.GR_HS_ZH.getBusinessKey(),
                    BusinessMappingProcessEnum.GR_HG_JHS.getBusinessKey(),
                    BusinessMappingProcessEnum.GR_HG_ZH.getBusinessKey(),
                    BusinessMappingProcessEnum.GR_JS.getBusinessKey(),
                    BusinessMappingProcessEnum.TH_CG_QK.getBusinessKey(),
                    BusinessMappingProcessEnum.TH_CG_DJTP.getBusinessKey()
            ).contains(data.getWorkSource())).map(data -> {
                PricingCreateRequest pricingCreateRequest = new PricingCreateRequest();
                pricingCreateRequest.setStockId(data.getStockId());
                pricingCreateRequest.setOriginSerialNo(data.getOriginSerialNo());
                pricingCreateRequest.setStoreWorkSerialNo(data.getStoreWorkSerialNo());
                pricingCreateRequest.setPricingSource(data.getWorkSource());
                pricingCreateRequest.setCreatedBy(UserContext.getUser().getUserName());
                pricingCreateRequest.setUpdatedBy(UserContext.getUser().getUserName());
                pricingCreateRequest.setCreatedId(UserContext.getUser().getId());
                pricingCreateRequest.setUpdatedId(UserContext.getUser().getId());
                pricingCreateRequest.setStoreId(UserContext.getUser().getStore().getId());
                pricingCreateRequest.setAgain(false);
                pricingCreateRequest.setCancel(false);
                pricingCreateRequest.setAuto(data.getAutoPrice());
                return pricingCreateRequest;
            }).collect(Collectors.toList())));
        } catch (Exception e) {
            log.error("定价开启通知异常,{}", e.getMessage(), e);
        }
    }
}
