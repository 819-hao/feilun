package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.pricing.request.PricingCancelRequest;
import com.seeease.flywheel.pricing.request.PricingCreateRequest;
import com.seeease.flywheel.qt.IQualityTestingFacade;
import com.seeease.flywheel.qt.request.QualityTestingDecisionRequest;
import com.seeease.flywheel.qt.result.QualityTestingDecisionListResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.BusinessMappingProcessEnum;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.event.PricingCancelEvent;
import com.seeease.flywheel.web.event.PricingStartEvent;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr. Du
 * @Description 质检判定
 * @Date create in 2023/1/19 09:49
 */
@Service
@Slf4j
@Extension(bizId = BizCode.QT, useCase = UseCase.QT_DETERMINE)
public class QualityTestingDecisionExt implements SubmitExtPtI<QualityTestingDecisionRequest, QualityTestingDecisionListResult> {

    private final static List<BusinessMappingProcessEnum> BUSINESS_MAPPING_PROCESS_ENUM_LIST = Arrays.asList(
            BusinessMappingProcessEnum.TH_CG_BH,
            BusinessMappingProcessEnum.TH_CG_DJ,
            BusinessMappingProcessEnum.TH_CG_PL,
            BusinessMappingProcessEnum.TH_CG_QK,
            BusinessMappingProcessEnum.TH_CG_DJTP,
//            BusinessMappingProcessEnum.GR_HS_JHS,
//            BusinessMappingProcessEnum.GR_HS_ZH,
            BusinessMappingProcessEnum.GR_HG_JHS,
            BusinessMappingProcessEnum.GR_HG_ZH,
            BusinessMappingProcessEnum.TH_JS,
            BusinessMappingProcessEnum.GR_JS

    );


    private final static List<BusinessMappingProcessEnum> BUSINESS_MAPPING_PROCESS_ENUM_LIST3 = Arrays.asList(
            BusinessMappingProcessEnum.GR_HS_JHS,
            BusinessMappingProcessEnum.GR_HS_ZH
//            BusinessMappingProcessEnum.GR_HG_JHS,
//            BusinessMappingProcessEnum.GR_HG_ZH

    );

    private final static List<BusinessMappingProcessEnum> BUSINESS_MAPPING_PROCESS_ENUM_LIST2 = Arrays.asList(
//            BusinessMappingProcessEnum.GR_HS_JHS,
//            BusinessMappingProcessEnum.GR_HS_ZH,
            BusinessMappingProcessEnum.GR_HG_JHS,
            BusinessMappingProcessEnum.GR_HG_ZH,
            BusinessMappingProcessEnum.TH_JS,
            BusinessMappingProcessEnum.GR_JS,
            BusinessMappingProcessEnum.GR_HG_ZH

    );

    @DubboReference(check = false, version = "1.0.0")
    private IQualityTestingFacade qualityTestingFacade;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public QualityTestingDecisionListResult submit(SubmitCmd<QualityTestingDecisionRequest> cmd) {

        QualityTestingDecisionListResult result = qualityTestingFacade.decision(cmd.getRequest());

        return result;
    }

    @Override
    public Map<String, Object> workflowVar(QualityTestingDecisionRequest request, QualityTestingDecisionListResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        workflowVar.put(VariateDefinitionKeyEnum.QT_STATE.getKey(), request.getQtState());
        workflowVar.put(VariateDefinitionKeyEnum.FIX_ON_QT.getKey(), result.getFixOnQt());
        workflowVar.put(VariateDefinitionKeyEnum.FIX_SERIAL_NO.getKey(), result.getFixSerialNo());
        workflowVar.put(VariateDefinitionKeyEnum.IS_REPAIR.getKey(), result.getIsRepair());
        workflowVar.put(VariateDefinitionKeyEnum.IS_ALLOT.getKey(), result.getIsAllot());

        if (StringUtils.isNotBlank(result.getOriginApplyPurchaseSerialNo())) {
            workflowVar.put(VariateDefinitionKeyEnum.APPLY_PURCHASE_SERIAL_NO.getKey(), result.getOriginApplyPurchaseSerialNo());
            workflowVar.put(VariateDefinitionKeyEnum.APPLY_PURCHASE_OWNER.getKey(), result.getApplyPurchaseOwner());
        }

        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(QualityTestingDecisionRequest request, QualityTestingDecisionListResult result) {
        StockLifeCycleResult.StockLifeCycleResultBuilder builder = StockLifeCycleResult.builder().stockId(result.getStockId()).originSerialNo(result.getSerialNo());
        BusinessMappingProcessEnum businessMappingProcessEnum = null;
        try {
            businessMappingProcessEnum = BusinessMappingProcessEnum.fromValue2(result.getWorkSource());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        switch (request.getQtState()) {
            case OperationDescConst.LOGISTICS_RECEIVING_PASS_VALUE:
                builder.operationDesc(String.format(OperationDescConst.QUALITY_TESTING, OperationDescConst.LOGISTICS_RECEIVING_PASS));
                if (ObjectUtils.isNotEmpty(businessMappingProcessEnum) && (BUSINESS_MAPPING_PROCESS_ENUM_LIST.contains(businessMappingProcessEnum)
                        //个人回收未走维修生成定价单
                        || (BUSINESS_MAPPING_PROCESS_ENUM_LIST3.contains(businessMappingProcessEnum) && StringUtils.isBlank(result.getFixSerialNo()))
                )

                ) {
                    //定价流程
                    startProcess(result);
                }
                break;
            case OperationDescConst.LOGISTICS_RECEIVING_ERROR_VALUE:
                builder.operationDesc(String.format(OperationDescConst.QUALITY_TESTING, OperationDescConst.LOGISTICS_RECEIVING_ERROR));
                if (ObjectUtils.isNotEmpty(businessMappingProcessEnum) && BUSINESS_MAPPING_PROCESS_ENUM_LIST.contains(businessMappingProcessEnum)
                        || (BUSINESS_MAPPING_PROCESS_ENUM_LIST3.contains(businessMappingProcessEnum) && StringUtils.isBlank(result.getFixSerialNo()))
                ) {
                    //定价流程
                    startProcess(result);
                }
                break;
            //退货
            case OperationDescConst.LOGISTICS_RECEIVING_RETURN_VALUE:
                //todo 啥时候取消取消通知
                publishEvent(result);
                builder.operationDesc(String.format(OperationDescConst.QUALITY_TESTING, OperationDescConst.LOGISTICS_RECEIVING_RETURN));
                break;

            //维修生成定价单 个人回收 生成定价单
            //回购，回收进入维修，此时有维修单 不生成定价单
            case OperationDescConst.LOGISTICS_RECEIVING_FIX_VALUE:
                builder.operationDesc(String.format(OperationDescConst.QUALITY_TESTING, OperationDescConst.LOGISTICS_RECEIVING_FIX));
                //维修 首次质检维修 符合采购流程的
                if (ObjectUtils.isNotEmpty(businessMappingProcessEnum) &&
                        //回收生成定价单
                        BUSINESS_MAPPING_PROCESS_ENUM_LIST3.contains(businessMappingProcessEnum)
                        //同时不是质检后维修
                        && result.getFixOnQt().equals(0)
                ) {
                    //定价流程
                    startProcess(result);
                }
                break;
            //确认维修 个人回购确认流程 生成定价单
            case OperationDescConst.LOGISTICS_RECEIVING_CONFIRM_VALUE:
                builder.operationDesc(String.format(OperationDescConst.QUALITY_TESTING, OperationDescConst.LOGISTICS_RECEIVING_CONFIRM));
                if (ObjectUtils.isNotEmpty(businessMappingProcessEnum) &&
                        BUSINESS_MAPPING_PROCESS_ENUM_LIST2.contains(businessMappingProcessEnum)
                ) {
                    //定价流程
                    startProcess(result);
                }
                break;
            //返修和换货 不生成定价单
            case OperationDescConst.LOGISTICS_RECEIVING_CONFIRM_RETURN_VALUE:
            case OperationDescConst.LOGISTICS_RECEIVING_CONFIRM_FIX_VALUE:
                break;

        }
        return Arrays.asList(builder.build());
    }

    @Override
    public Class<QualityTestingDecisionRequest> getRequestClass() {
        return QualityTestingDecisionRequest.class;
    }

    @Override
    public void validate(SubmitCmd<QualityTestingDecisionRequest> cmd) {

    }

    /**
     * 通知取消定价
     *
     * @param data
     */
    private void publishEvent(QualityTestingDecisionListResult data) {

        //生命周期
        PricingCancelRequest pricingCancelRequest = new PricingCancelRequest();

        pricingCancelRequest.setStockId(data.getStockId());

        pricingCancelRequest.setCreatedBy(UserContext.getUser().getUserName());
        pricingCancelRequest.setCreatedId(UserContext.getUser().getId());
        pricingCancelRequest.setStoreId(UserContext.getUser().getStore().getId());

        applicationContext.publishEvent(new PricingCancelEvent(this, Arrays.asList(pricingCancelRequest)));
    }

    /**
     * 开启自动定价流程
     *
     * @param data
     */
    private void startProcess(QualityTestingDecisionListResult data) {

        try {

            if (!Arrays.asList(
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
            ).contains(data.getWorkSource())) {
                return;
            }

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
            //质检单返回
            pricingCreateRequest.setAuto(data.getAutoPrice());

            applicationContext.publishEvent(new PricingStartEvent(this, Arrays.asList(pricingCreateRequest)));
        } catch (Exception e) {
            log.error("定价开启异常,{}", e.getMessage(), e);
        }
    }


}
