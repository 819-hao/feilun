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
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.seeeaseframework.mybatis.type.TransactionalUtil;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Set;

/**
 * @Author Mr. Du
 * @Description 质检异常
 * @Date create in 2023/3/4 10:48
 */
@Component
public class QtReturnStrategy extends QtDecisionStrategy {

    private static final Set<BusinessBillTypeEnum> SCOPE_BUSINESS = ImmutableSet.of(BusinessBillTypeEnum.GR_JS, BusinessBillTypeEnum.GR_HG_JHS, BusinessBillTypeEnum.GR_HG_ZH);
    private static final Set<BusinessBillTypeEnum> SCOPE_BUSINESS2 = ImmutableSet.of(BusinessBillTypeEnum.YC_CL);

    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;

    @Resource
    private BillHandlerEventPublisher billHandlerEventPublisher;

    @Resource
    private TransactionalUtil transactionalUtil;

    @Override
    public QualityTestingStateEnum getState() {
        return QualityTestingStateEnum.RETURN;
    }

    @Override
    public QualityTestingDecisionListResult save(QualityTestingDecisionRequest request) {

        return transactionalUtil.transactional(() -> {

            QualityTestingDetailsResult qualityTestingDetailsResult = request.getQualityTestingDetailsResult();

            billStoreWorkPreService.qtRejectWaitForDelivery(qualityTestingDetailsResult.getWorkId());

            BillQualityTesting billQualityTesting = new BillQualityTesting();
            billQualityTesting.setId(qualityTestingDetailsResult.getId());
            billQualityTesting.setQtConclusion(QualityTestingConclusionEnum.RETURN);
            billQualityTesting.setTransitionStateEnum(QualityTestingStateEnum.TransitionEnum.RECEIVE_RETURN_DELIVERY);
            billQualityTesting.setReturnImgs(request.getReturnImgs());
            billQualityTesting.setReturnReasonId(request.getReturnReasonId());
            billQualityTesting.setReturnReason(request.getReturnReason());

            LogQualityTestingOpt logQualityTestingOpt = LogQualityTestingOptConverter.INSTANCE.convert(qualityTestingDetailsResult);
            //日志
            logQualityTestingOpt.setQtState(QualityTestingStateEnum.TransitionEnum.RECEIVE_RETURN_DELIVERY.getToState());
            logQualityTestingOpt.setQtConclusion(QualityTestingConclusionEnum.RETURN);
            logQualityTestingOpt.setReturnImgs(request.getReturnImgs());
            logQualityTestingOpt.setReturnReasonId(request.getReturnReasonId());
            logQualityTestingOpt.setReturnReason(request.getReturnReason());

            super.optAndDecisionSave(billQualityTesting, logQualityTestingOpt);

            billHandlerEventPublisher.publishEvent(new QtDecisionEvent(qualityTestingDetailsResult.getStockId(), QualityTestingStateEnum.RETURN, qualityTestingDetailsResult.getOriginSerialNo()
                    , BigDecimal.ZERO, null, BusinessBillTypeEnum.fromValue(qualityTestingDetailsResult.getQtSource()), qualityTestingDetailsResult.getId(),null));

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
        Assert.notNull(request.getReturnReasonId(), "退货原因不能为空");
//        Assert.isTrue(!SCOPE_BUSINESS.contains(billQualityTestingMapper.selectOne(Wrappers.<BillQualityTesting>lambdaQuery().eq(BillQualityTesting::getId, request.getQualityTestingId())).getQtSource()), "质检不能判断退货");
        if (SCOPE_BUSINESS.contains(BusinessBillTypeEnum.fromValue(request.getQualityTestingDetailsResult().getQtSource())) && ObjectUtils.isNotEmpty(request.getQualityTestingDetailsResult().getFixId())) {
            throw new OperationRejectedException(OperationExceptionCode.BUY_BACK_PRICE_4);
        }

        if (SCOPE_BUSINESS2.contains(BusinessBillTypeEnum.fromValue(request.getQualityTestingDetailsResult().getQtSource()))) {
            throw new OperationRejectedException(OperationExceptionCode.BUY_BACK_PRICE_4);
        }
    }
}
