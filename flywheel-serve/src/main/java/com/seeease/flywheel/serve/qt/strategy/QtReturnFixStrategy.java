package com.seeease.flywheel.serve.qt.strategy;

import com.google.common.collect.ImmutableSet;
import com.seeease.flywheel.qt.request.QualityTestingDecisionRequest;
import com.seeease.flywheel.qt.result.QualityTestingDecisionListResult;
import com.seeease.flywheel.qt.result.QualityTestingDetailsResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.event.BillHandlerEventPublisher;
import com.seeease.flywheel.serve.qt.convert.LogQualityTestingOptConverter;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.flywheel.serve.qt.entity.LogQualityTestingOpt;
import com.seeease.flywheel.serve.qt.enums.QualityTestingConclusionEnum;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.flywheel.serve.qt.event.QtDecisionEvent;
import com.seeease.flywheel.serve.storework.enums.StoreWorkReturnTypeEnum;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.seeeaseframework.mybatis.type.TransactionalUtil;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Set;

/**
 * @Author Mr. Du
 * @Description 返修
 * @Date create in 2023/3/4 10:48
 */
@Component
public class QtReturnFixStrategy extends QtDecisionStrategy {

    private static final Set<BusinessBillTypeEnum> SCOPE_BUSINESS2 = ImmutableSet.of(BusinessBillTypeEnum.TH_CG_DJ, BusinessBillTypeEnum.TH_CG_BH, BusinessBillTypeEnum.TH_CG_QK,BusinessBillTypeEnum.TH_CG_DJTP);

    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;

    @Resource
    private BillHandlerEventPublisher billHandlerEventPublisher;

    @Resource
    private TransactionalUtil transactionalUtil;

    @Override
    public QualityTestingStateEnum getState() {
        return QualityTestingStateEnum.RETURN_FIX;
    }

    @Override
    public QualityTestingDecisionListResult save(QualityTestingDecisionRequest request) {

        return transactionalUtil.transactional(() -> {

            QualityTestingDetailsResult qualityTestingDetailsResult = request.getQualityTestingDetailsResult();

            //返修
            billStoreWorkPreService.qtRejectWaitForDelivery(qualityTestingDetailsResult.getWorkId(), StoreWorkReturnTypeEnum.OUT_STORE);

            BillQualityTesting billQualityTesting = new BillQualityTesting();
            billQualityTesting.setId(qualityTestingDetailsResult.getId());
            billQualityTesting.setQtConclusion(QualityTestingConclusionEnum.RETURN_FIX);
            billQualityTesting.setTransitionStateEnum(QualityTestingStateEnum.TransitionEnum.RECEIVE_RETURN_FIX);

            LogQualityTestingOpt logQualityTestingOpt = LogQualityTestingOptConverter.INSTANCE.convert(qualityTestingDetailsResult);
            //日志
            logQualityTestingOpt.setQtState(QualityTestingStateEnum.TransitionEnum.RECEIVE_RETURN_FIX.getToState());
            logQualityTestingOpt.setQtConclusion(QualityTestingConclusionEnum.RETURN_FIX);

            super.optAndDecisionSave(billQualityTesting, logQualityTestingOpt);

            billHandlerEventPublisher.publishEvent(new QtDecisionEvent(qualityTestingDetailsResult.getStockId(), QualityTestingStateEnum.RETURN, qualityTestingDetailsResult.getOriginSerialNo()
                    , BigDecimal.ZERO, null, BusinessBillTypeEnum.fromValue(qualityTestingDetailsResult.getQtSource()), qualityTestingDetailsResult.getId(), true));

            QualityTestingDecisionListResult build = QualityTestingDecisionListResult.builder()
                    .fixOnQt(0)
                    .stockId(qualityTestingDetailsResult.getStockId())
                    .serialNo(qualityTestingDetailsResult.getSerialNo())
                    .workSource(qualityTestingDetailsResult.getQtSource())
                    .build();
            return build;
        });
    }

    @Override
    void preRequestProcessing(QualityTestingDecisionRequest request) {

    }

    @Override
    void checkRequest(QualityTestingDecisionRequest request) throws BusinessException {
        if (!SCOPE_BUSINESS2.contains(BusinessBillTypeEnum.fromValue(request.getQualityTestingDetailsResult().getQtSource()))) {
            throw new OperationRejectedException(OperationExceptionCode.BUY_BACK_PRICE_4);
        }
    }
}
