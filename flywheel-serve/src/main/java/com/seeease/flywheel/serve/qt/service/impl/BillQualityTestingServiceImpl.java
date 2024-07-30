package com.seeease.flywheel.serve.qt.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.qt.request.FixQualityTestingRequest;
import com.seeease.flywheel.qt.request.QualityTestingCreateRequest;
import com.seeease.flywheel.qt.request.QualityTestingListRequest;
import com.seeease.flywheel.qt.request.QualityTestingWaitDeliverListRequest;
import com.seeease.flywheel.qt.result.FixQualityTestingResult;
import com.seeease.flywheel.qt.result.QualityTestingListResult;
import com.seeease.flywheel.qt.result.QualityTestingWaitDeliverListResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.qt.convert.QualityTestingConverter;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.flywheel.serve.qt.enums.QualityTestingConclusionEnum;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.flywheel.serve.qt.mapper.BillQualityTestingMapper;
import com.seeease.flywheel.serve.qt.service.BillQualityTestingService;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_quality_testing】的数据库操作Service实现
 * @createDate 2023-01-17 11:25:43
 */
@Service
public class BillQualityTestingServiceImpl extends ServiceImpl<BillQualityTestingMapper, BillQualityTesting>
        implements BillQualityTestingService {
    @Resource
    private StockMapper stockMapper;

    @Resource
    private GoodsWatchService goodsWatchService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(List<QualityTestingCreateRequest> request) {
        List<BillQualityTesting> convert = QualityTestingConverter.INSTANCE.convert(request);

        convert.forEach(billQualityTesting -> {
            billQualityTesting.setQtState(QualityTestingStateEnum.RECEIVE);
            billQualityTesting.setSerialNo(SerialNoGenerator.generateQTSerialNo());
            billQualityTesting.setTaskArriveTime(new Date());
        });

        this.baseMapper.insertBatchSomeColumn(convert);
    }

    @Override
    public Page<QualityTestingListResult> page(QualityTestingListRequest request) {

        return this.baseMapper.getPage(new Page(request.getPage(), request.getLimit()), request);
    }

    @Override
    public Page<QualityTestingWaitDeliverListResult> page(QualityTestingWaitDeliverListRequest request) {
        return this.baseMapper.getPageWaitDeliver(new Page(request.getPage(), request.getLimit()), request);
    }

    @Override
    public FixQualityTestingResult fix(FixQualityTestingRequest request) {

        BillQualityTesting qualityTesting = baseMapper.selectOne(Wrappers.lambdaQuery(BillQualityTesting.class).
                eq(BillQualityTesting::getFixId, request.getFixId()));

        if (ObjectUtils.isNotEmpty(qualityTesting)) {

            BillQualityTesting billQualityTesting = new BillQualityTesting();
            billQualityTesting.setId(qualityTesting.getId());

            billQualityTesting.setTransitionStateEnum(QualityTestingStateEnum.TransitionEnum.FIX_RECEIVE);
            billQualityTesting.setTaskArriveTime(new Date());
            UpdateByIdCheckState.update(baseMapper, billQualityTesting);

            return QualityTestingConverter.INSTANCE.convertFixBillQualityTestingResult(billQualityTesting);
        }

        return null;
    }

    @Override
    public void decision(BillQualityTesting request) {
        UpdateByIdCheckState.update(this.baseMapper, request);
    }

    @Override
    public void update(Integer stockId, Integer deliverTo, QualityTestingStateEnum qualityTestingStateEnum) {

        BillQualityTesting qualityTesting = baseMapper.selectOne(Wrappers.lambdaQuery(BillQualityTesting.class)
                .eq(BillQualityTesting::getQtState, qualityTestingStateEnum)
                .eq(BillQualityTesting::getStockId, stockId));

        if (ObjectUtils.isNotEmpty(qualityTesting)) {

            BillQualityTesting billQualityTesting = new BillQualityTesting();
            billQualityTesting.setId(qualityTesting.getId());

            switch (deliverTo) {
                //维修 个人回购，个人寄售
                case 0:

                    switch (qualityTestingStateEnum) {
                        case CONFIRM_FIX:
                            billQualityTesting.setTransitionStateEnum(QualityTestingStateEnum.TransitionEnum.CONFIRM_FIX_NOT);
                            billQualityTesting.setQtConclusion(QualityTestingConclusionEnum.FIX);
                            billQualityTesting.setDeliverTo(0);
                            break;
                    }

                    break;
                //维修 个人回购，个人寄售
                case 1:
                    switch (qualityTestingStateEnum) {
                        case CONFIRM_FIX:
                            billQualityTesting.setTransitionStateEnum(QualityTestingStateEnum.TransitionEnum.CONFIRM_FIX_OK);
                            billQualityTesting.setDeliverTo(1);
                            billQualityTesting.setQtConclusion(QualityTestingConclusionEnum.RETURN);
                            break;
                    }
                    break;
            }

            billQualityTesting.setTaskArriveTime(new Date());
            UpdateByIdCheckState.update(baseMapper, billQualityTesting);
        }
    }

    //自动定价排出品牌
    private static final List<Integer> autoPriceExtBrand = Arrays.asList(188,
            66,
            75);

    private static final List<Integer> autoPriceExtSrc = Arrays.asList(
            BusinessBillTypeEnum.GR_HS_ZH.getValue(),
            BusinessBillTypeEnum.GR_HS_JHS.getValue(),
            BusinessBillTypeEnum.GR_HG_JHS.getValue(),
            BusinessBillTypeEnum.GR_JS.getValue(),
            BusinessBillTypeEnum.GR_HG_ZH.getValue());

    @Override
    public Boolean autoPrice(Integer stockId) {
        WatchDataFusion watchDataFusion = goodsWatchService.getWatchDataFusionListByStockIds(Lists.newArrayList(stockId))
                .stream().findFirst()
                .orElse(null);

        if (autoPriceExtBrand.contains(watchDataFusion.getBrandId())
                || watchDataFusion.getFiness().equals(FlywheelConstant.FINESS_S_99_NEW)
                || autoPriceExtSrc.contains(watchDataFusion.getStockSrc())
        ) {
            return false;
        }

        return true;
    }
}




