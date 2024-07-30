package com.seeease.flywheel.serve.qt.strategy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.ImmutableSet;
import com.seeease.flywheel.fix.request.FixCreateRequest;
import com.seeease.flywheel.fix.request.QtFixRequest;
import com.seeease.flywheel.fix.result.FixCreateResult;
import com.seeease.flywheel.fix.result.QtFixResult;
import com.seeease.flywheel.qt.request.QualityTestingDecisionRequest;
import com.seeease.flywheel.qt.result.QualityTestingDecisionListResult;
import com.seeease.flywheel.qt.result.QualityTestingDetailsResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.event.BillHandlerEventPublisher;
import com.seeease.flywheel.serve.fix.convert.LogFixOptConverter;
import com.seeease.flywheel.serve.fix.entity.BillFix;
import com.seeease.flywheel.serve.fix.enums.OrderTypeEnum;
import com.seeease.flywheel.serve.fix.service.BillFixService;
import com.seeease.flywheel.serve.fix.service.LogFixOptService;
import com.seeease.flywheel.serve.goods.entity.Brand;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.goods.service.BrandService;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.qt.convert.LogQualityTestingOptConverter;
import com.seeease.flywheel.serve.qt.convert.QualityTestingConverter;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.flywheel.serve.qt.entity.LogQualityTestingOpt;
import com.seeease.flywheel.serve.qt.enums.QualityTestingConclusionEnum;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.flywheel.serve.qt.event.QtDecisionEvent;
import com.seeease.flywheel.serve.qt.service.BillQualityTestingService;
import com.seeease.seeeaseframework.mybatis.type.TransactionalUtil;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 质检-维修
 * @Date create in 2023/3/4 10:48
 */
@Component
public class QtFixStrategy extends QtDecisionStrategy {

    private static final Set<BusinessBillTypeEnum> SCOPE_BUSINESS = ImmutableSet.of(BusinessBillTypeEnum.GR_JS);

    @Resource
    private TransactionalUtil transactionalUtil;

    @Resource
    private BillFixService billFixService;

    @Resource
    private BillHandlerEventPublisher billHandlerEventPublisher;

    @Resource
    private BillQualityTestingService billQualityTestingService;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private GoodsWatchService goodsWatchService;

    @Resource
    private BrandService brandService;

    @Resource
    private LogFixOptService logFixOptService;

    @Override
    public QualityTestingStateEnum getState() {
        return QualityTestingStateEnum.FIX;
    }

    @Override
    public QualityTestingDecisionListResult save(QualityTestingDecisionRequest request) {

        return transactionalUtil.transactional(() -> {

            QualityTestingDecisionListResult.QualityTestingDecisionListResultBuilder builder = QualityTestingDecisionListResult.builder();

            Integer fixId = 0;

            Boolean fixOnQt = false;

            String fixSerialNo = null;

            QualityTestingDetailsResult qualityTestingDetailsResult = request.getQualityTestingDetailsResult();

            //有维修单 更改维修单
            if (ObjectUtils.isNotEmpty(qualityTestingDetailsResult) && ObjectUtils.isNotEmpty(qualityTestingDetailsResult.getFixId())) {

                BillFix billFix = Optional.ofNullable(billFixService.getById(qualityTestingDetailsResult.getFixId())).map(Function.identity()).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.FIX_NOT_EXIT));

                QtFixRequest qtFixRequest = new QtFixRequest();

                qtFixRequest.setFixId(qualityTestingDetailsResult.getFixId());
                qtFixRequest.setFixAdvise(request.getFixAdvise());
                qtFixRequest.setFixDay(request.getFixDay());
                //变更状态
                QtFixResult qtFixResult = billFixService.qt(qtFixRequest);

                fixId = qtFixResult.getId();
                fixSerialNo = qtFixResult.getSerialNo();

                builder.isRepair(1);

                if (Objects.nonNull(billFix.getMaintenanceMasterId())) {
                    builder.isAllot(0);
                } else {
                    builder.isAllot(1);
                }
            } else {

                //无维修单 创建维修单
                FixCreateRequest fixCreateRequest = new FixCreateRequest();
                //老数据
                fixCreateRequest.setFixAdvise(request.getFixAdvise());
                fixCreateRequest.setFixSource(qualityTestingDetailsResult.getQtSource());
                fixCreateRequest.setCustomerId(qualityTestingDetailsResult.getCustomerId());
                fixCreateRequest.setCustomerContactId(qualityTestingDetailsResult.getCustomerContactId());
                fixCreateRequest.setOriginSerialNo(qualityTestingDetailsResult.getOriginSerialNo());
                fixCreateRequest.setStoreWorkSerialNo(qualityTestingDetailsResult.getStoreWorkSerialNo());

                fixCreateRequest.setStockId(qualityTestingDetailsResult.getStockId());

                Stock stock = stockMapper.selectById(qualityTestingDetailsResult.getStockId());

                //新数据
                fixCreateRequest.setOrderType(OrderTypeEnum.UNDEFINED.getValue());
                fixCreateRequest.setStockSn(Objects.nonNull(stock) ? stock.getSn() : null);
                fixCreateRequest.setStoreId(UserContext.getUser().getStore().getId());
                fixCreateRequest.setBrandId(Optional.ofNullable(stock).map(s -> goodsWatchService.getById(stock.getGoodsId())).map(GoodsWatch::getBrandId).orElse(null));
                fixCreateRequest.setStrapMaterial(Objects.nonNull(stock) ? stock.getStrapMaterial() : null);
                fixCreateRequest.setWatchSection(Objects.nonNull(stock) ? stock.getWatchSection() : null);
                if (Objects.nonNull(fixCreateRequest.getBrandId())) {
                    Brand brand = brandService.getOne(new LambdaQueryWrapper<Brand>().eq(Brand::getId, fixCreateRequest.getBrandId()));
                    fixCreateRequest.setBrandName(Objects.nonNull(brand) ? brand.getName() : null);
                }
                fixCreateRequest.setWatchSection(Objects.nonNull(stock) ? stock.getWatchSection() : null);

                //建单
                FixCreateResult fixCreateResult = billFixService.create(fixCreateRequest);
                fixId = fixCreateResult.getId();
                fixSerialNo = fixCreateResult.getSerialNo();

                fixOnQt = true;

                builder.isRepair(1);
                builder.isAllot(1);
            }

            //更新一下质检单的维修id 状态&id
            BillQualityTesting billQualityTesting = new BillQualityTesting();
            billQualityTesting.setId(qualityTestingDetailsResult.getId());
            billQualityTesting.setFixId(fixId);
            billQualityTesting.setQtConclusion(QualityTestingConclusionEnum.FIX);
            billQualityTesting.setTransitionStateEnum(QualityTestingStateEnum.TransitionEnum.RECEIVE_FIX_DELIVERY);
            billQualityTesting.setFixAdvise(request.getFixAdvise());
            billQualityTesting.setFixMoney(request.getFixMoney());
            billQualityTesting.setFixDay(request.getFixDay());
            billQualityTesting.setContent(request.getContent().stream().map(fixProjectMapper -> QualityTestingConverter.INSTANCE.convertFixProjectMapper(fixProjectMapper)).collect(Collectors.toList()));

            //日志
            LogQualityTestingOpt logQualityTestingOpt = LogQualityTestingOptConverter.INSTANCE.convert(qualityTestingDetailsResult);
            logQualityTestingOpt.setQtState(QualityTestingStateEnum.TransitionEnum.RECEIVE_FIX_DELIVERY.getToState());
            logQualityTestingOpt.setQtConclusion(QualityTestingConclusionEnum.FIX);
            logQualityTestingOpt.setFixAdvise(request.getFixAdvise());
            logQualityTestingOpt.setFixMoney(request.getFixMoney());
            logQualityTestingOpt.setFixDay(request.getFixDay());
            logQualityTestingOpt.setContent(request.getContent().stream().map(fixProjectMapper -> QualityTestingConverter.INSTANCE.convertFixProjectMapper(fixProjectMapper)).collect(Collectors.toList()));

            super.optAndDecisionSave(billQualityTesting, logQualityTestingOpt);

            billHandlerEventPublisher.publishEvent(new QtDecisionEvent(qualityTestingDetailsResult.getStockId(), QualityTestingStateEnum.FIX, qualityTestingDetailsResult.getOriginSerialNo()
                    , request.getFixMoney(), fixId, BusinessBillTypeEnum.fromValue(qualityTestingDetailsResult.getQtSource()), qualityTestingDetailsResult.getId(), null));


            BillFix billFix11 = billFixService.getById(fixId);

            logFixOptService.save(LogFixOptConverter.INSTANCE.convert(billFix11));

            return builder
                    .fixOnQt(!fixOnQt ? 1 : 0)
                    .fixSerialNo(fixSerialNo)
                    .stockId(qualityTestingDetailsResult.getStockId())
                    .serialNo(qualityTestingDetailsResult.getSerialNo())
                    .workSource(qualityTestingDetailsResult.getQtSource())
                    .originSerialNo(qualityTestingDetailsResult.getOriginSerialNo())
                    .storeWorkSerialNo(qualityTestingDetailsResult.getStoreWorkSerialNo())
                    .autoPrice(billQualityTestingService.autoPrice(qualityTestingDetailsResult.getStockId()))
                    .build();
        });
    }

    @Override
    void preRequestProcessing(QualityTestingDecisionRequest request) {
        request.setFixMoney(Optional.ofNullable(request.getFixMoney()).orElse(BigDecimal.valueOf(0L)));
    }

    @Override
    void checkRequest(QualityTestingDecisionRequest request) throws BusinessException {

        if (SCOPE_BUSINESS.contains(BusinessBillTypeEnum.fromValue(request.getQualityTestingDetailsResult().getQtSource())) && ObjectUtils.isEmpty(request.getQualityTestingDetailsResult().getFixId())) {
            throw new OperationRejectedException(OperationExceptionCode.BUY_BACK_PRICE_2);
        }

        Assert.notNull(request.getFixMoney(), "预计维修价不能为空");

    }
}
