package com.seeease.flywheel.serve.qt.strategy;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.ImmutableSet;
import com.seeease.flywheel.qt.request.QualityTestingDecisionRequest;
import com.seeease.flywheel.qt.result.QualityTestingDecisionListResult;
import com.seeease.flywheel.qt.result.QualityTestingDetailsResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.event.BillHandlerEventPublisher;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
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
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 换货
 * @Date create in 2023/3/4 10:48
 */
@Component
public class QtReturnStockStrategy extends QtDecisionStrategy {

    private static final Set<BusinessBillTypeEnum> SCOPE_BUSINESS2 = ImmutableSet.of(BusinessBillTypeEnum.TH_CG_DJ, BusinessBillTypeEnum.TH_CG_BH, BusinessBillTypeEnum.TH_CG_QK,BusinessBillTypeEnum.TH_CG_DJTP);

    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;

    @Resource
    private BillHandlerEventPublisher billHandlerEventPublisher;

    @Resource
    private TransactionalUtil transactionalUtil;

    @Resource
    private BillPurchaseService billPurchaseService;

    @Resource
    private BillPurchaseLineService billPurchaseLineService;

    @Resource
    private StockService stockService;

    @Override
    public QualityTestingStateEnum getState() {
        return QualityTestingStateEnum.RETURN_NEW;
    }

    @Override
    public QualityTestingDecisionListResult save(QualityTestingDecisionRequest request) {

        return transactionalUtil.transactional(() -> {

            QualityTestingDetailsResult qualityTestingDetailsResult = request.getQualityTestingDetailsResult();

            //换货
            billStoreWorkPreService.qtRejectWaitForDelivery(qualityTestingDetailsResult.getWorkId(), StoreWorkReturnTypeEnum.INT_STORE);

            //变更采购单表身号
            billPurchaseService.editByStock(qualityTestingDetailsResult.getStockId(),
                    qualityTestingDetailsResult.getOriginSerialNo(),
                    request.getReturnNewStockSn(),
                    request.getReturnFixRemarks()
            );

            BillQualityTesting billQualityTesting = new BillQualityTesting();
            billQualityTesting.setId(qualityTestingDetailsResult.getId());
            billQualityTesting.setQtConclusion(QualityTestingConclusionEnum.RETURN_STOCK);
            billQualityTesting.setTransitionStateEnum(QualityTestingStateEnum.TransitionEnum.RECEIVE_RETURN_NEW);

            LogQualityTestingOpt logQualityTestingOpt = LogQualityTestingOptConverter.INSTANCE.convert(qualityTestingDetailsResult);
            //日志
            logQualityTestingOpt.setQtState(QualityTestingStateEnum.TransitionEnum.RECEIVE_RETURN_NEW.getToState());
            logQualityTestingOpt.setQtConclusion(QualityTestingConclusionEnum.RETURN_STOCK);

            super.optAndDecisionSave(billQualityTesting, logQualityTestingOpt);

            billHandlerEventPublisher.publishEvent(new QtDecisionEvent(qualityTestingDetailsResult.getStockId(), QualityTestingStateEnum.RETURN, qualityTestingDetailsResult.getOriginSerialNo()
                    , BigDecimal.ZERO, null, BusinessBillTypeEnum.fromValue(qualityTestingDetailsResult.getQtSource()), qualityTestingDetailsResult.getId(),false));

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
        Assert.notNull(request.getReturnNewStockSn(), "换货表身号不能为空");

        if (!SCOPE_BUSINESS2.contains(BusinessBillTypeEnum.fromValue(request.getQualityTestingDetailsResult().getQtSource()))) {
            throw new OperationRejectedException(OperationExceptionCode.BUY_BACK_PRICE_4);
        }

        //1.是否允许建单
        Map<String, BillPurchaseLine> collectByLine = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery().in(BillPurchaseLine::getStockSn, Arrays.asList(request.getReturnNewStockSn()))
                .notIn(BillPurchaseLine::getPurchaseLineState, Arrays.asList(
                        PurchaseLineStateEnum.RETURNED,
                        PurchaseLineStateEnum.ORDER_CANCEL_WHOLE,
                        PurchaseLineStateEnum.WAREHOUSED,
                        PurchaseLineStateEnum.IN_SETTLED
                ))).stream().collect(Collectors.toMap(BillPurchaseLine::getStockSn, Function.identity()));

        if (CollectionUtils.isNotEmpty(collectByLine.keySet())) {
            throw new OperationRejectedException(OperationExceptionCode.PURCHASE_NEW_STOCK);
        }
        //2。查询表身号
        Map<String, Stock> collectByStock = stockService.list(Wrappers.<Stock>lambdaQuery().in(Stock::getSn, Arrays.asList(request.getReturnNewStockSn()))
                .notIn(Stock::getStockStatus, Arrays.asList(
                        StockStatusEnum.PURCHASE_RETURNED_ING,
                        StockStatusEnum.SOLD_OUT,
                        StockStatusEnum.PURCHASE_RETURNED
                ))).stream().collect(Collectors.toMap(Stock::getSn, Function.identity()));

        if (CollectionUtils.isNotEmpty(collectByStock.keySet())) {
            throw new OperationRejectedException(OperationExceptionCode.PURCHASE_NEW_STOCK);
        }
    }
}
