package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.qt.IQualityTestingFacade;
import com.seeease.flywheel.qt.request.QualityTestingDecisionRequest;
import com.seeease.flywheel.qt.result.QualityTestingDecisionListResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr. Du
 * @Description 质检转交
 * @Date create in 2023/1/19 09:49
 */
@Service
@Extension(bizId = BizCode.QT, useCase = UseCase.QT_DELIVERY)
public class QualityTestingDeliverExt implements SubmitExtPtI<QualityTestingDecisionRequest, QualityTestingDecisionListResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IQualityTestingFacade qualityTestingFacade;

    @Override
    public QualityTestingDecisionListResult submit(SubmitCmd<QualityTestingDecisionRequest> cmd) {
        return qualityTestingFacade.receive(cmd.getRequest().getQualityTestingId());
    }

    @Override
    public Map<String, Object> workflowVar(QualityTestingDecisionRequest request, QualityTestingDecisionListResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        workflowVar.put(VariateDefinitionKeyEnum.FIX_ON_QT.getKey(), result.getFixOnQt());
        workflowVar.put(VariateDefinitionKeyEnum.FIX_SERIAL_NO.getKey(), result.getFixSerialNo());
        switch (result.getDeliverTo()) {
            //维修
            case 0:
                workflowVar.put(VariateDefinitionKeyEnum.DELIVER_TO.getKey(), result.getDeliverTo());
                workflowVar.put(VariateDefinitionKeyEnum.QT_STATE.getKey(), OperationDescConst.LOGISTICS_RECEIVING_FIX_VALUE);
                break;
            //退货
            case 1:
                workflowVar.put(VariateDefinitionKeyEnum.DELIVER_TO.getKey(), result.getDeliverTo());
                workflowVar.put(VariateDefinitionKeyEnum.QT_STATE.getKey(), OperationDescConst.LOGISTICS_RECEIVING_RETURN_VALUE);
                break;
            //入库
            case 3:
                workflowVar.put(VariateDefinitionKeyEnum.DELIVER_TO.getKey(), result.getDeliverTo());
                workflowVar.put(VariateDefinitionKeyEnum.QT_STATE.getKey(), OperationDescConst.LOGISTICS_RECEIVING_CONFIRM_FIX_VALUE);
                break;
        }
        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(QualityTestingDecisionRequest request, QualityTestingDecisionListResult result) {
        return Arrays.asList(StockLifeCycleResult.builder()
                .stockId(result.getStockId())
                .originSerialNo(result.getSerialNo())
                .operationDesc(OperationDescConst.QT_CHANGE).build());
    }

    @Override
    public Class<QualityTestingDecisionRequest> getRequestClass() {
        return QualityTestingDecisionRequest.class;
    }

    @Override
    public void validate(SubmitCmd<QualityTestingDecisionRequest> cmd) {

    }
}
