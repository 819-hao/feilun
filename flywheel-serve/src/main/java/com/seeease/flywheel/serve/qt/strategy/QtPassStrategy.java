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
import com.seeease.flywheel.serve.qt.service.BillQualityTestingService;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.type.TransactionalUtil;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Set;

/**
 * @Author Mr. Du
 * @Description 质检通过
 * @Date create in 2023/3/4 10:48
 */
@Component
public class QtPassStrategy extends QtDecisionStrategy {

    private static final Set<BusinessBillTypeEnum> SCOPE_BUSINESS = ImmutableSet.of(BusinessBillTypeEnum.GR_HG_JHS, BusinessBillTypeEnum.GR_HG_ZH);

    private static final Set<BusinessBillTypeEnum> SCOPE_BUSINESS2 = ImmutableSet.of(BusinessBillTypeEnum.YC_CL);
    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;

    @Resource
    private BillHandlerEventPublisher billHandlerEventPublisher;

    @Resource
    private TransactionalUtil transactionalUtil;

    @Resource
    private BillQualityTestingService billQualityTestingService;

    @Override
    public QualityTestingStateEnum getState() {
        return QualityTestingStateEnum.NORMAL;
    }

    @Override
    public QualityTestingDecisionListResult save(QualityTestingDecisionRequest request) {

        return transactionalUtil.transactional(() -> {

            QualityTestingDetailsResult qualityTestingDetailsResult = request.getQualityTestingDetailsResult();

            billStoreWorkPreService.qtPassed(qualityTestingDetailsResult.getWorkId(), WhetherEnum.NO);

            BillQualityTesting billQualityTesting = new BillQualityTesting();
            billQualityTesting.setId(qualityTestingDetailsResult.getId());
            billQualityTesting.setQtConclusion(QualityTestingConclusionEnum.CREATE);
            billQualityTesting.setTransitionStateEnum(QualityTestingStateEnum.TransitionEnum.RECEIVE_NORMAL_DELIVERY);

            LogQualityTestingOpt logQualityTestingOpt = LogQualityTestingOptConverter.INSTANCE.convert(qualityTestingDetailsResult);
            //日志
            logQualityTestingOpt.setQtState(QualityTestingStateEnum.TransitionEnum.RECEIVE_NORMAL_DELIVERY.getToState());
            logQualityTestingOpt.setQtConclusion(QualityTestingConclusionEnum.CREATE);

            super.optAndDecisionSave(billQualityTesting, logQualityTestingOpt);

            billHandlerEventPublisher.publishEvent(new QtDecisionEvent(qualityTestingDetailsResult.getStockId(), QualityTestingStateEnum.NORMAL, qualityTestingDetailsResult.getOriginSerialNo()
                    , BigDecimal.ZERO, null, BusinessBillTypeEnum.fromValue(qualityTestingDetailsResult.getQtSource()), qualityTestingDetailsResult.getId(), null));

            QualityTestingDecisionListResult build = QualityTestingDecisionListResult.builder()
                    .fixOnQt(0)
                    .stockId(qualityTestingDetailsResult.getStockId())
                    .serialNo(qualityTestingDetailsResult.getSerialNo())
                    .originSerialNo(qualityTestingDetailsResult.getOriginSerialNo())
                    .storeWorkSerialNo(qualityTestingDetailsResult.getStoreWorkSerialNo())
                    .workSource(qualityTestingDetailsResult.getQtSource())
                    .autoPrice(billQualityTestingService.autoPrice(qualityTestingDetailsResult.getStockId()))
                    //是否有无维修单
                    .fixSerialNo(ObjectUtils.isEmpty(qualityTestingDetailsResult.getFixId()) ? null : String.valueOf(qualityTestingDetailsResult.getFixId()))
                    .build();

            return build;
        });
    }

    @Override
    void preRequestProcessing(QualityTestingDecisionRequest request) {

    }

    @Override
    void checkRequest(QualityTestingDecisionRequest request) throws BusinessException {
        //个人回购商品 不能直接进入库 维修 异常入库
//        if (SCOPE_BUSINESS.contains(BusinessBillTypeEnum.fromValue(request.getQualityTestingDetailsResult().getQtSource())) && ObjectUtils.isEmpty(request.getQualityTestingDetailsResult().getFixId())) {
//            throw new OperationRejectedException(OperationExceptionCode.BUY_BACK_PRICE_3);
//        }
//
//        if (SCOPE_BUSINESS2.contains(BusinessBillTypeEnum.fromValue(request.getQualityTestingDetailsResult().getQtSource())) && ObjectUtils.isEmpty(request.getQualityTestingDetailsResult().getFixId())) {
//            throw new OperationRejectedException(OperationExceptionCode.BUY_BACK_PRICE_3);
//        }
    }
}
