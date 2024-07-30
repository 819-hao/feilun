package com.seeease.flywheel.serve.qt.strategy;

import com.google.common.collect.ImmutableSet;
import com.seeease.flywheel.qt.request.QualityTestingDecisionRequest;
import com.seeease.flywheel.qt.result.QualityTestingDecisionListResult;
import com.seeease.flywheel.qt.result.QualityTestingDetailsResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.event.BillHandlerEventPublisher;
import com.seeease.flywheel.serve.qt.convert.LogQualityTestingOptConverter;
import com.seeease.flywheel.serve.qt.convert.QualityTestingConverter;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.flywheel.serve.qt.entity.LogQualityTestingOpt;
import com.seeease.flywheel.serve.qt.enums.QualityTestingConclusionEnum;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.flywheel.serve.qt.event.QtDecisionEvent;
import com.seeease.flywheel.serve.qt.service.BillQualityTestingService;
import com.seeease.seeeaseframework.mybatis.type.TransactionalUtil;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 确认维修
 * @Date create in 2023/3/4 10:48
 */
@Component
public class QtConfirmFixStrategy extends QtDecisionStrategy {

    private static final Set<BusinessBillTypeEnum> SCOPE_BUSINESS = ImmutableSet.of(BusinessBillTypeEnum.GR_JS, BusinessBillTypeEnum.GR_HG_JHS, BusinessBillTypeEnum.GR_HG_ZH);

    private static final Set<BusinessBillTypeEnum> SCOPE_BUSINESS2 = ImmutableSet.of(BusinessBillTypeEnum.YC_CL);
    @Resource
    private TransactionalUtil transactionalUtil;

    @Resource
    private BillHandlerEventPublisher billHandlerEventPublisher;

    @Resource
    private BillQualityTestingService billQualityTestingService;

    @Override
    public QualityTestingStateEnum getState() {
        return QualityTestingStateEnum.CONFIRM_FIX;
    }

    @Override
    public QualityTestingDecisionListResult save(QualityTestingDecisionRequest request) {

        return transactionalUtil.transactional(() -> {

            QualityTestingDetailsResult qualityTestingDetailsResult = request.getQualityTestingDetailsResult();

            //更新状态
            BillQualityTesting billQualityTesting = new BillQualityTesting();
            billQualityTesting.setId(qualityTestingDetailsResult.getId());
            billQualityTesting.setQtConclusion(QualityTestingConclusionEnum.CONFIRM_FIX);
            billQualityTesting.setTransitionStateEnum(QualityTestingStateEnum.TransitionEnum.RECEIVE_CONFIRM_FIX);
            //待转交 初始者 todo
            billQualityTesting.setDeliver(0);
            billQualityTesting.setDeliverTo(2);
            billQualityTesting.setFixAdvise(request.getFixAdvise());
            billQualityTesting.setFixMoney(request.getFixMoney());
            billQualityTesting.setFixDay(request.getFixDay());
            billQualityTesting.setContent(request.getContent().stream().map(fixProjectMapper -> QualityTestingConverter.INSTANCE.convertFixProjectMapper(fixProjectMapper)).collect(Collectors.toList()));

            //日志
            LogQualityTestingOpt logQualityTestingOpt = LogQualityTestingOptConverter.INSTANCE.convert(qualityTestingDetailsResult);
            logQualityTestingOpt.setQtState(QualityTestingStateEnum.TransitionEnum.RECEIVE_FIX_DELIVERY.getToState());
            logQualityTestingOpt.setQtConclusion(QualityTestingConclusionEnum.FIX);
            logQualityTestingOpt.setDeliver(0);
            logQualityTestingOpt.setDeliverTo(2);
            logQualityTestingOpt.setFixAdvise(request.getFixAdvise());
            logQualityTestingOpt.setFixMoney(request.getFixMoney());
            logQualityTestingOpt.setFixDay(request.getFixDay());
            logQualityTestingOpt.setContent(request.getContent().stream().map(fixProjectMapper -> QualityTestingConverter.INSTANCE.convertFixProjectMapper(fixProjectMapper)).collect(Collectors.toList()));

            super.optAndDecisionSave(billQualityTesting, logQualityTestingOpt);

            billHandlerEventPublisher.publishEvent(new QtDecisionEvent(qualityTestingDetailsResult.getStockId(), QualityTestingStateEnum.CONFIRM_FIX, qualityTestingDetailsResult.getOriginSerialNo()
                    , request.getFixMoney(), null, BusinessBillTypeEnum.fromValue(qualityTestingDetailsResult.getQtSource()), qualityTestingDetailsResult.getId(), null));

            QualityTestingDecisionListResult build = QualityTestingDecisionListResult.builder()
                    .fixOnQt(0)
                    .stockId(qualityTestingDetailsResult.getStockId())
                    .serialNo(qualityTestingDetailsResult.getSerialNo())
                    .workSource(qualityTestingDetailsResult.getQtSource())
                    .originSerialNo(qualityTestingDetailsResult.getOriginSerialNo())
                    .storeWorkSerialNo(qualityTestingDetailsResult.getStoreWorkSerialNo())
                    .autoPrice(billQualityTestingService.autoPrice(qualityTestingDetailsResult.getStockId()))
                    .build();

            return build;
        });
    }

    @Override
    void preRequestProcessing(QualityTestingDecisionRequest request) {

    }

    @Override
    void checkRequest(QualityTestingDecisionRequest request) throws BusinessException {
        if (!SCOPE_BUSINESS.contains(BusinessBillTypeEnum.fromValue(request.getQualityTestingDetailsResult().getQtSource()))) {
            throw new OperationRejectedException(OperationExceptionCode.BUY_BACK_PRICE);
        }

        if (SCOPE_BUSINESS2.contains(BusinessBillTypeEnum.fromValue(request.getQualityTestingDetailsResult().getQtSource()))) {
            throw new OperationRejectedException(OperationExceptionCode.BUY_BACK_PRICE_4);
        }
    }
}
