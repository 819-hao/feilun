package com.seeease.flywheel.serve.pricing.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.pricing.request.*;
import com.seeease.flywheel.pricing.result.*;
import com.seeease.flywheel.serve.base.BigDecimalUtil;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SeeeaseConstant;
import com.seeease.flywheel.serve.fix.mapper.BillFixMapper;
import com.seeease.flywheel.serve.goods.entity.GoodsStockCPrice;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.mapper.GoodsMetaInfoSyncMapper;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.entity.Tag;
import com.seeease.flywheel.serve.maindata.mapper.PurchaseSubjectMapper;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.TagService;
import com.seeease.flywheel.serve.pricing.convert.PricingConvert;
import com.seeease.flywheel.serve.pricing.entity.BillPricing;
import com.seeease.flywheel.serve.pricing.entity.LogPricingOpt;
import com.seeease.flywheel.serve.pricing.entity.SalesPriorityModifyDTO;
import com.seeease.flywheel.serve.pricing.enums.PricingNodeEnum;
import com.seeease.flywheel.serve.pricing.enums.PricingStateEnum;
import com.seeease.flywheel.serve.pricing.mapper.BillPricingMapper;
import com.seeease.flywheel.serve.pricing.mapper.LogPricingOptMapper;
import com.seeease.flywheel.serve.pricing.service.BillPricingService;
import com.seeease.flywheel.serve.purchase.enums.SalesPriorityEnum;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dmmasxnmf
 * @description 针对表【bill_pricing(订价记录)】的数据库操作Service实现
 * @createDate 2023-03-21 10:44:40
 */
@Service
@Slf4j
public class BillPricingServiceImpl extends ServiceImpl<BillPricingMapper, BillPricing> implements BillPricingService {

    @Resource
    private StockMapper stockMapper;

    @Resource
    private BillFixMapper billFixMapper;

    @Resource
    private PurchaseSubjectMapper purchaseSubjectMapper;

    @Resource
    private GoodsMetaInfoSyncMapper goodsMetaInfoSyncMapper;
    @Resource
    private TagService tagService;



    /**
     * 可以改价格和状态
     */
    private static final ImmutableSet<StockStatusEnum> CHANGE_PRICE_AND_STATE = ImmutableSet.<StockStatusEnum>builder()
            .add(StockStatusEnum.WAIT_PRICING)
            .build();

    /**
     * 可以改价格
     */
    private static final ImmutableSet<StockStatusEnum> CHANGE_PRICE = ImmutableSet.<StockStatusEnum>builder()
            .add(StockStatusEnum.PURCHASE_IN_TRANSIT)
            .add(StockStatusEnum.WAIT_RECEIVED)
            .add(StockStatusEnum.ALLOCATE_IN_TRANSIT)
            .add(StockStatusEnum.MARKETABLE)
            .add(StockStatusEnum.EXCEPTION)
            .add(StockStatusEnum.EXCEPTION_IN)
            .add(StockStatusEnum.CONSIGNMENT)
            .add(StockStatusEnum.ON_LOAN)
            .build();

    /**
     * 不可以改价格
     */
    private static final ImmutableSet<StockStatusEnum> NO_CHANGE_PRICE = ImmutableSet.<StockStatusEnum>builder()
            .add(StockStatusEnum.SOLD_OUT)
            .add(StockStatusEnum.PURCHASE_RETURNED_ING)
            .add(StockStatusEnum.PURCHASE_RETURNED)
            .build();

    @Resource
    private LogPricingOptMapper logPricingOptMapper;

    @Resource
    private StockService stockService;

    @Resource
    private GoodsWatchService goodsWatchService;

    @Override
    public PricingCreateResult create(PricingCreateRequest request) {

        BillPricing billPricing = PricingConvert.INSTANCE.convert(request);
        billPricing.setAllPrice(billPricing.getPurchasePrice());
        billPricing.setPricingState(PricingStateEnum.CREATE);
        billPricing.setAutoPrice(1);
        baseMapper.insert(billPricing);

        LogPricingOpt logPricingOpt = PricingConvert.INSTANCE.convertLogPricingOptIgnore(billPricing);
        logPricingOpt.setPricingNode(PricingNodeEnum.INSERT);
        logPricingOpt.setAutoPrice(1);
        logPricingOptMapper.insert(logPricingOpt);

        return PricingConvert.INSTANCE.convertPricingCreateResult(billPricing);
    }

    @Override
    public PricingCreateResult createAuto(PricingCreateRequest request) {

        BillPricing billPricing = PricingConvert.INSTANCE.convert(request);
        billPricing.setAllPrice(billPricing.getPurchasePrice().add(request.getFixPrice()));
        billPricing.setPricingState(PricingStateEnum.CHECK);
        //前闭后开
        //预制数据
        preProcessing(billPricing);
        billPricing.setAutoPrice(2);
        baseMapper.insert(billPricing);

        LogPricingOpt logPricingOpt = PricingConvert.INSTANCE.convertLogPricingOptIgnore(billPricing);
        logPricingOpt.setAutoPrice(2);
        logPricingOpt.setPricingNode(PricingNodeEnum.SUBMIT);

        logPricingOptMapper.insert(logPricingOpt);

        return PricingConvert.INSTANCE.convertPricingCreateResult(billPricing);
    }

    @Override
    public PricingCreateResult again(PricingCreateRequest request) {

        BillPricing billPricing = PricingConvert.INSTANCE.convert(request);

        if (request.getCancel()) {
            billPricing.setTransitionStateEnum(PricingStateEnum.TransitionEnum.CANCEL_WHOLE_CREATE);
        } else {
            billPricing.setTransitionStateEnum(PricingStateEnum.TransitionEnum.COMPLETE_CREATE);
            billPricing.setAllPrice(billPricing.getPurchasePrice().add(billPricing.getFixPrice()));
        }
        billPricing.setAutoPrice(1);
        UpdateByIdCheckState.update(baseMapper, billPricing);

        LogPricingOpt logPricingOpt = PricingConvert.INSTANCE.convertLogPricingOpt(billPricing);
        logPricingOpt.setPricingNode(PricingNodeEnum.AGAIN);
        logPricingOpt.setAutoPrice(1);
        logPricingOptMapper.insert(logPricingOpt);

        BillPricing pricing = baseMapper.selectById(request.getId());

        return PricingConvert.INSTANCE.convertPricingCreateResult(pricing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PricingFinishResult finish(PricingFinishRequest request) {



        Optional.ofNullable(request.getTobPrice())
                .filter(com.seeease.springframework.utils.BigDecimalUtil::gtZero)
                .orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.CONSIGNMENT_PRICE_NOT_0));
        Optional.ofNullable(request.getTocPrice())
                .filter(com.seeease.springframework.utils.BigDecimalUtil::gtZero)
                .orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.CONSIGNMENT_PRICE_NOT_0));
        Optional.ofNullable(request.getConsignmentPrice())
                .filter(com.seeease.springframework.utils.BigDecimalUtil::gtZero)
                .orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.CONSIGNMENT_PRICE_NOT_0));

        //四舍五入
        request.setTobPrice(BigDecimalUtil.roundHalfUp(request.getTobPrice()));
        request.setTocPrice(BigDecimalUtil.roundHalfUp(request.getTocPrice()));
        request.setConsignmentPrice(BigDecimalUtil.roundHalfUp(request.getConsignmentPrice()));

        BillPricing billPricing = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> baseMapper.selectOne(Wrappers.<BillPricing>lambdaQuery()
                        .eq(BillPricing::getPricingState, PricingStateEnum.CREATE)
                        .eq(BillPricing::getId, t.getId()).or().eq(BillPricing::getSerialNo, t.getSerialNo())
                )).orElseThrow(() -> new BusinessException(ExceptionCode.PRICING_BILL_NOT_EXIST));




        //比较价格 重定价去掉成本限制 PROJT-1275
        if (!billPricing.getAgain()
                && (request.getTobPrice().compareTo(request.getTocPrice()) > 0
                || billPricing.getPurchasePrice().compareTo(request.getTobPrice()) >= 0
                || billPricing.getPurchasePrice().compareTo(request.getTocPrice()) >= 0)) {
            throw new OperationRejectedException(OperationExceptionCode.PRICING_ERROR);
        }

        BillPricing upPricing = PricingConvert.INSTANCE.convert(request);
        upPricing.setId(billPricing.getId());
        upPricing.setAllPrice(billPricing.getAllPrice());
        upPricing.setDemandId(request.getDemandId());
        upPricing.setWuyuBuyBackPrice(request.getWuyuBuyBackPrice());
        //预制数据
        preProcessing(upPricing);
        upPricing.setTransitionStateEnum(PricingStateEnum.TransitionEnum.CREATE_CHECK);

        UpdateByIdCheckState.update(baseMapper, upPricing);

        //日志记录
        BillPricing pricing = baseMapper.selectById(billPricing.getId());
        LogPricingOpt logPricingOpt = PricingConvert.INSTANCE.convertLogPricingOpt(pricing);
        logPricingOpt.setPricingNode(PricingNodeEnum.SUBMIT);
        logPricingOptMapper.insert(logPricingOpt);

        return PricingConvert.INSTANCE.convertPricingFinishResult(pricing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PricingFinishBatchResult finishBatch(PricingFinishBatchRequest request) {
        List<PricingFinishResult> resultList = request.getRequestList()
                .stream()
                .map(this::finish)
                .collect(Collectors.toList());
        return PricingFinishBatchResult.builder()
                .resultList(resultList)
                .build();
    }

    private void preProcessing(BillPricing pricing) {

        //特殊处理
        pricing.setBMargin(pricing.getBPrice().subtract(pricing.getAllPrice()).divide(pricing.getBPrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)));
        pricing.setCMargin(pricing.getCPrice().subtract(pricing.getAllPrice()).divide(pricing.getCPrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)));

        //前闭后开
        pricing.setTPrice(pricing.getCPrice().add(SeeeaseConstant.TAG_PRICE_ROLE_MAP.get(pricing.getCPrice())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PricingCompletedResult completed(PricingCompletedRequest request) {
        BillPricing billPricing = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> baseMapper.selectOne(Wrappers.<BillPricing>lambdaQuery()
                        .eq(BillPricing::getPricingState, PricingStateEnum.CHECK)
                        .eq(BillPricing::getId, t.getId()).or().eq(BillPricing::getSerialNo, t.getSerialNo())
                )).orElseThrow(() -> new BusinessException(ExceptionCode.PRICING_BILL_NOT_EXIST));

        BigDecimal toBPrice = Optional.ofNullable(request.getTobPrice()).orElse(billPricing.getBPrice());
        BigDecimal toCPrice = Optional.ofNullable(request.getTocPrice()).orElse(billPricing.getCPrice());
        BigDecimal consignmentPrice = Optional.ofNullable(request.getConsignmentPrice()).orElse(billPricing.getConsignmentPrice());

        if (com.seeease.springframework.utils.BigDecimalUtil.leZero(toBPrice)
                || com.seeease.springframework.utils.BigDecimalUtil.leZero(toCPrice)
                || com.seeease.springframework.utils.BigDecimalUtil.leZero(consignmentPrice)) {
            throw new OperationRejectedException(OperationExceptionCode.CONSIGNMENT_PRICE_NOT_0);
        }

        log.info("定价toC价格:{},定价toB价格:{},寄售价格:{}", toBPrice, toCPrice, consignmentPrice);
//        //1.注意维修价是否注入
//        List<BillFix> fixList = billFixMapper.selectList(Wrappers.<BillFix>lambdaQuery()
//                .eq(BillFix::getFixState, FixStateEnum.NORMAL)
//                .eq(BillFix::getStockId, billPricing.getStockId()));
//        BigDecimal fixPrice = BigDecimal.ZERO;
//        if (CollectionUtils.isNotEmpty(fixList)) {
//            fixPrice = fixList.stream().map(BillFix::getFixMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
//        }
//        log.info("维修价格:{}", fixPrice);
        if (!billPricing.getAgain()) {
            Boolean b = ImmutableRangeMap.<Comparable<BigDecimal>, Boolean>builder()
                    .put(Range.open(toBPrice, toCPrice), true)
                    .put(Range.atLeast(toCPrice), false)
                    .build().get(consignmentPrice);
            log.info("最终toB价格:{},最终toC价格:{}", toBPrice, toCPrice);
            if (ObjectUtils.isNotEmpty(b) && b) {
                log.info("寄售价格:{},最终toB价格:{}", consignmentPrice, billPricing.getBPrice());
                throw new OperationRejectedException(OperationExceptionCode.CONSIGNMENT_PRICE_NOT_EXIST_B);
            } else if (ObjectUtils.isNotEmpty(b) && !b) {
                //通知 //记录 警告
                throw new OperationRejectedException(OperationExceptionCode.CONSIGNMENT_PRICE_NOT_EXIST);
            }
        }

        //重置入参数
        PricingStateEnum.TransitionEnum transitionEnum;
        PricingNodeEnum pricingNodeEnum;
        if (WhetherEnum.NO.getValue().equals(request.getCheckState())) {
            transitionEnum = PricingStateEnum.TransitionEnum.CHECK_CREATE;
            pricingNodeEnum = PricingNodeEnum.COMPLETE;
        } else {
            transitionEnum = PricingStateEnum.TransitionEnum.CHECK_COMPLETE;
            pricingNodeEnum = PricingNodeEnum.CHECK;
            Stock stockQuery = stockService.getById(billPricing.getStockId());
            if (ObjectUtils.isNotEmpty(stockQuery)) {

                if (CHANGE_PRICE_AND_STATE.contains(stockQuery.getStockStatus())) {
                    //待定价 可以修改 状态+价格//改表价格 乐观锁问题
                    stockService.updateStockStatus(Arrays.asList(billPricing.getStockId()), StockStatusEnum.TransitionEnum.WAIT_PRICING_MARKETABLE);
                    Stock stock = new Stock();
                    stock.setDemandId(request.getDemandId());
                    stock.setWuyuBuyBackPrice(request.getWuyuBuyBackPrice());
                    stock.setTobPrice(toBPrice);
                    stock.setTocPrice(toCPrice);
                    stock.setConsignmentPrice(consignmentPrice);
                    if (null == stockQuery.getNewSettlePrice() || BigDecimal.ZERO.equals(stockQuery.getNewSettlePrice())){
                        stock.setNewSettlePrice(consignmentPrice);
                    }

                    stock.setId(billPricing.getStockId());
                    stock.setTagPrice(toCPrice.add(SeeeaseConstant.TAG_PRICE_ROLE_MAP.get(toCPrice)));
                    stock.setLevel(ObjectUtils.isNotEmpty(request.getGoodsLevel()) ? request.getGoodsLevel() : billPricing.getGoodsLevel());
                    stock.setSalesPriority(ObjectUtils.isNotEmpty(request.getSalesPriority()) ? request.getSalesPriority() : billPricing.getSalesPriority().getValue());
                    //改价格
                    stockService.updateById(stock);
                } else if (CHANGE_PRICE.contains(stockQuery.getStockStatus())) {
                    //采购在途
                    //只改价格
                    Stock stock = new Stock();
                    stock.setTobPrice(toBPrice);
                    stock.setTocPrice(toCPrice);
                    stock.setDemandId(request.getDemandId());
                    stock.setWuyuBuyBackPrice(request.getWuyuBuyBackPrice());
                    stock.setConsignmentPrice(consignmentPrice);
                    if (null == stockQuery.getNewSettlePrice() || BigDecimal.ZERO.equals(stockQuery.getNewSettlePrice())){
                        stock.setNewSettlePrice(consignmentPrice);
                    }
                    stock.setId(billPricing.getStockId());
                    stock.setTagPrice(toCPrice.add(SeeeaseConstant.TAG_PRICE_ROLE_MAP.get(toCPrice)));
                    stock.setLevel(ObjectUtils.isNotEmpty(request.getGoodsLevel()) ? request.getGoodsLevel() : billPricing.getGoodsLevel());
                    stock.setSalesPriority(ObjectUtils.isNotEmpty(request.getSalesPriority()) ? request.getSalesPriority() : billPricing.getSalesPriority().getValue());
                    //改价格
                    stockService.updateById(stock);
                } else if (NO_CHANGE_PRICE.contains(stockQuery.getStockStatus())) {
                    Stock stock = new Stock();
                    stock.setId(billPricing.getStockId());
                    stock.setDemandId(request.getDemandId());
                    stock.setWuyuBuyBackPrice(request.getWuyuBuyBackPrice());
                    stockService.updateById(stock);
                    //不能改价格
                    log.error("不能改价格，id={}", billPricing.getStockId());
                }
            }
        }

        BillPricing upPricing = PricingConvert.INSTANCE.convert(request);
        upPricing.setId(billPricing.getId());
        upPricing.setBPrice(toBPrice);
        upPricing.setDemandId(request.getDemandId());
        upPricing.setWuyuBuyBackPrice(request.getWuyuBuyBackPrice());
        upPricing.setCPrice(toCPrice);
        upPricing.setConsignmentPrice(consignmentPrice);
        upPricing.setTransitionStateEnum(transitionEnum);
        upPricing.setSalesPriority(ObjectUtils.isNotEmpty(request.getSalesPriority()) ? SalesPriorityEnum.fromCode(request.getSalesPriority()) : billPricing.getSalesPriority());
        upPricing.setGoodsLevel(ObjectUtils.isNotEmpty(request.getGoodsLevel()) ? request.getGoodsLevel() : billPricing.getGoodsLevel());
        upPricing.setAllPrice(billPricing.getPurchasePrice().add(billPricing.getFixPrice()));
        this.preProcessing(upPricing);
        UpdateByIdCheckState.update(baseMapper, upPricing);

        //日志
        BillPricing pricing = baseMapper.selectById(billPricing.getId());
        LogPricingOpt logPricingOpt = PricingConvert.INSTANCE.convertLogPricingOpt(pricing);
        logPricingOpt.setPricingNode(pricingNodeEnum);
        logPricingOptMapper.insert(logPricingOpt);

        PricingCompletedResult result = PricingConvert.INSTANCE.convertPricingCompletedResult(pricing);
        result.setPriceMessage(selectNewStock(billPricing.getStockId()));

        return result;
    }

    /**
     * 同一型号的新表校验
     *
     * @param stockId
     */
    private PricingCompletedResult.PriceMessage selectNewStock(Integer stockId) {
        //是否是新表
        GoodsStockCPrice cPrice = Optional.ofNullable(goodsMetaInfoSyncMapper.checkNewStock(stockId)).orElse(null);
        if (ObjectUtils.isEmpty(cPrice)) {
            return null;
        }

        //新表 同一型号的新表集合
        List<GoodsStockCPrice> stockCPriceList = goodsMetaInfoSyncMapper.checkCPrice(cPrice.getGoodsId(), true);

        if (CollectionUtils.isEmpty(stockCPriceList) || stockCPriceList.stream().map(GoodsStockCPrice::getTocPrice).distinct().count() == 1L) {
            return null;
        }

        GoodsStockCPrice goodsStockCPrice = stockCPriceList.get(0);
//        List<String> otherColl = stockCPriceList.stream().filter(stockCPrice -> !stockId.equals(stockCPrice.getStockId()))
//                .map(stockCPrice -> stockCPrice.getStockSn() + "(2c价:" + stockCPrice.getTocPrice() + "元,吊牌价:" + stockCPrice.getTagPrice() + "元)").collect(Collectors.toList());
//
//        List<String> localColl = stockCPriceList.stream().filter(stockCPrice -> stockId.equals(stockCPrice.getStockId()))
//                .map(stockCPrice -> stockCPrice.getStockSn() + "(2c价:" + stockCPrice.getTocPrice() + "元,吊牌价:" + stockCPrice.getTagPrice() + "元)").collect(Collectors.toList());
//        localColl.addAll(otherColl);

        return PricingCompletedResult.PriceMessage.builder()
                .brandName(goodsStockCPrice.getBrandName())
                .seriesName(goodsStockCPrice.getSeriesName())
                .model(goodsStockCPrice.getModel())
                .lineMsg(StringUtils.join(stockCPriceList.stream().collect(Collectors.groupingBy(GoodsStockCPrice::getTocPrice)).entrySet().stream().map(k -> {
                    GoodsStockCPrice stockCPrice = k.getValue().get(0);
                    return
//                            stockCPrice.getStockSn() +
                            "(2c价:" + stockCPrice.getTocPrice() + "元,吊牌价:" + stockCPrice.getTagPrice() + "元)";
                }).collect(Collectors.toList()), "\n"))
                .build();
    }

    @Override
    public PricingDetailsResult details(PricingDetailsRequest request) {

        BillPricing billPricing = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> baseMapper.selectOne(Wrappers.<BillPricing>lambdaQuery()
                        .eq(BillPricing::getId, request).or().eq(BillPricing::getSerialNo, request.getSerialNo())
                )).orElseThrow(() -> new BusinessException(ExceptionCode.PRICING_BILL_NOT_EXIST));

        PricingDetailsResult result = PricingConvert.INSTANCE.convertPricingDetailsResult(billPricing);

//        //1.注意维修价是否注入
//        List<BillFix> fixList = billFixMapper.selectList(Wrappers.<BillFix>lambdaQuery()
//                .eq(BillFix::getFixState, FixStateEnum.NORMAL)
//                .eq(BillFix::getStockId, billPricing.getStockId()));
//        BigDecimal fixPrice = BigDecimal.ZERO;
//        if (CollectionUtils.isNotEmpty(fixList)) {
//            fixPrice = fixList.stream().map(BillFix::getFixMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
//        }
//        log.info("维修价格:{}", fixPrice);

        WatchDataFusion watchDataFusion = goodsWatchService.getWatchDataFusionListByStockIds(Arrays.asList(result.getStockId())).get(FlywheelConstant.INDEX);

        result.setNewSettlePrice(watchDataFusion.getNewSettlePrice());
        result.setBrandName(watchDataFusion.getBrandName());
        result.setSeriesName(watchDataFusion.getSeriesName());
        result.setModel(watchDataFusion.getModel());
        result.setPricePub(watchDataFusion.getPricePub());
        result.setFiness(watchDataFusion.getFiness());
        result.setStockSn(watchDataFusion.getStockSn());
        result.setImage(watchDataFusion.getImage());
        result.setWuyuPrice(watchDataFusion.getWuyuPrice());
        result.setAttachment(watchDataFusion.getAttachment());
        if (result.getDemandId() == null ){
            result.setDemandId(watchDataFusion.getDemandId());
        }

        if (result.getWuyuBuyBackPrice() == null || BigDecimal.ZERO.equals(result.getWuyuBuyBackPrice())){
            result.setWuyuBuyBackPrice(watchDataFusion.getWuyuBuyBackPrice());
        }

        Tag tag = tagService.selectByStoreManagementId(result.getDemandId() == null ? watchDataFusion.getDemandId() : result.getDemandId());
        if (null != tag){
            result.setDemandName(tag.getTagName());
        }



//        BigDecimal finalFixPrice = fixPrice;
//        result.setConsignmentPrice(BigDecimalUtil.roundHalfUp(Optional.ofNullable(result.getConsignmentPrice())
//                .filter(com.seeease.springframework.utils.BigDecimalUtil::neZero)
//                .orElseGet(() -> stockMapper.getConsignmentPrice2(result.getStockId(), finalFixPrice).getConsignmentPrice())));

        result.setLogList(logPricingOptMapper.selectList(Wrappers.<LogPricingOpt>lambdaQuery()
                        .eq(LogPricingOpt::getStockId, billPricing.getStockId())
                        .eq(LogPricingOpt::getOriginSerialNo, billPricing.getOriginSerialNo())
                        .orderByAsc(LogPricingOpt::getId))
                .stream().map(logPricingOpt -> PricingConvert.INSTANCE.convertPricingLog(logPricingOpt)).
                collect(Collectors.toList()));
        result.setIsSale(Optional.ofNullable(stockMapper.selectById(billPricing.getStockId())).map(stock -> {
            if (Arrays.asList(StockStatusEnum.SOLD_OUT).contains(stock.getStockStatus())) {
                return true;
            }
            return false;
        }).orElse(true));

        return result;
    }

    @Override
    public PageResult<PricingListResult> list(PricingListRequest request) {

        if (CollectionUtils.isNotEmpty(request.getBrandIdList()) || StringUtils.isNotEmpty(request.getModel())) {
            List<Integer> collect = goodsWatchService.list(Wrappers.<GoodsWatch>lambdaQuery()
                            .in(CollectionUtils.isNotEmpty(request.getBrandIdList()), GoodsWatch::getBrandId, request.getBrandIdList())
                            .eq(StringUtils.isNotEmpty(request.getModel()), GoodsWatch::getModel, request.getModel()))
                    .stream().map(GoodsWatch::getId).collect(Collectors.toList());
            request.setGoodsIdList(CollectionUtils.isNotEmpty(collect) ? collect : null);
        }

        request.setPricingState(Optional.ofNullable(request.getPricingState())
                .filter(v -> v != -1)
                .orElse(null));

        request.setPricingSource(Optional.ofNullable(request.getPricingSource())
                .filter(v -> v != -1)
                .orElse(null));

        Page<PricingListResult> billPricingPage = baseMapper.list(new Page<>(request.getPage(), request.getLimit()), request);

        List<PricingListResult> records = billPricingPage.getRecords();

        List<WatchDataFusion> fusionList = goodsWatchService.getWatchDataFusionListByGoodsIds(records.stream().map(PricingListResult::getGoodsId).distinct().collect(Collectors.toList()));

        records.forEach(pricingListResult -> {
            WatchDataFusion watchDataFusion = fusionList.stream().filter(r -> r.getGoodsId().equals(pricingListResult.getGoodsId())).findAny().orElse(null);
            if (Objects.nonNull(watchDataFusion)) {
                pricingListResult.setBrandName(watchDataFusion.getBrandName());
                pricingListResult.setSeriesName(watchDataFusion.getSeriesName());
                pricingListResult.setModel(watchDataFusion.getModel());
                pricingListResult.setPricePub(watchDataFusion.getPricePub());
                pricingListResult.setImage(watchDataFusion.getImage());
            }
            //返回给前端寄售价格和寄售加点
//            convertConsingment(pricingListResult);
        });

        return PageResult.<PricingListResult>builder()
                .result(records)
                .totalCount(billPricingPage.getTotal())
                .totalPage(billPricingPage.getPages())
                .build();
    }

    @Override
    public PageResult<PricingLogListResult> logList(PricingLogListRequest request) {

        request.setPricingNode(Optional.ofNullable(request.getPricingNode())
                .filter(v -> v != -1)
                .orElse(null));

        request.setPricingSource(Optional.ofNullable(request.getPricingSource())
                .filter(v -> v != -1)
                .orElse(null));

        Page<PricingLogListResult> pricingOptPage = logPricingOptMapper.list(new Page<>(request.getPage(), request.getLimit()), request);

        List<PricingLogListResult> collect = pricingOptPage.getRecords();

        List<WatchDataFusion> fusionList = goodsWatchService.getWatchDataFusionListByGoodsIds(collect.stream().map(PricingLogListResult::getGoodsId).distinct().collect(Collectors.toList()));

        collect.forEach(pricingListResult -> {
            WatchDataFusion watchDataFusion = fusionList.stream().filter(r -> r.getGoodsId().equals(pricingListResult.getGoodsId())).findAny().get();
            pricingListResult.setBrandName(watchDataFusion.getBrandName());
            pricingListResult.setSeriesName(watchDataFusion.getSeriesName());
            pricingListResult.setModel(watchDataFusion.getModel());
            pricingListResult.setPricePub(watchDataFusion.getPricePub());
            pricingListResult.setImage(watchDataFusion.getImage());
        });

        return PageResult.<PricingLogListResult>builder()
                .result(collect)
                .totalCount(pricingOptPage.getTotal())
                .totalPage(pricingOptPage.getPages())
                .build();
    }

    /**
     * 定价单什么时候可以取消 todo
     */
    @Override
    public PricingCancelResult cancel(PricingCancelRequest request) {

        PricingCancelResult pricingCancelResult = new PricingCancelResult();

        try {
            BillPricing billPricing = Optional.ofNullable(request)
                    .filter(t -> Objects.nonNull(t.getStockId())
//                        || StringUtils.isNotBlank(t.getOriginSerialNo())
                    )
                    .map(t -> baseMapper.selectOne(Wrappers.<BillPricing>lambdaQuery()
                                    .eq(BillPricing::getStockId, t.getStockId())
//                                .or()
//                        .eq(BillPricing::getOriginSerialNo, t.getOriginSerialNo())
                    )).orElseThrow(() -> new BusinessException(ExceptionCode.PRICING_BILL_NOT_EXIST));


            PricingStateEnum.TransitionEnum transitionEnum = null;

            if (billPricing.getPricingState() == PricingStateEnum.CREATE) {
                //
                transitionEnum = PricingStateEnum.TransitionEnum.CREATE_CANCEL_WHOLE;
            } else if (billPricing.getPricingState() == PricingStateEnum.CHECK) {
                transitionEnum = PricingStateEnum.TransitionEnum.CHECK_CANCEL_WHOLE;
            } else if (billPricing.getPricingState() == PricingStateEnum.COMPLETE) {
                transitionEnum = PricingStateEnum.TransitionEnum.COMPLETE_CANCEL_WHOLE;
            } else {
                log.error("无需取消,stockId = {}", request.getStockId());
                pricingCancelResult.setSerialNo(billPricing.getSerialNo());

                pricingCancelResult.setSerialNo(billPricing.getSerialNo());
                pricingCancelResult.setStockId(request.getStockId());
                return pricingCancelResult;
            }

            BillPricing pricing = new BillPricing();
            pricing.setId(billPricing.getId());
            pricing.setTransitionStateEnum(transitionEnum);
            pricing.setUpdatedBy(request.getCreatedBy());
            pricing.setUpdatedId(request.getCreatedId());

            UpdateByIdCheckState.update(baseMapper, pricing);

            LogPricingOpt logPricingOpt = PricingConvert.INSTANCE.convertLogPricingOpt(billPricing);
            logPricingOpt.setPricingNode(PricingNodeEnum.CANCEL);
            logPricingOpt.setCreatedId(request.getCreatedId());
            logPricingOpt.setUpdatedId(request.getCreatedId());
            logPricingOpt.setCreatedBy(request.getCreatedBy());
            logPricingOpt.setUpdatedBy(request.getCreatedBy());
            logPricingOptMapper.insert(logPricingOpt);

            pricingCancelResult.setSerialNo(billPricing.getSerialNo());
            pricingCancelResult.setStockId(request.getStockId());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return pricingCancelResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PricingStockQueryImportResult> importList(PricingStockQueryImportRequest request,Integer state) {

        Map<String, PricingStockQueryImportRequest.ImportDto> importDtoMap = request.getDataList()
                .stream()
                .collect(Collectors.toMap(PricingStockQueryImportRequest.ImportDto::getStockSn, Function.identity()));

        PricingListRequest pricingListRequest = new PricingListRequest();
        pricingListRequest.setPricingState(state);
        pricingListRequest.setStockSnList(importDtoMap.keySet().stream().collect(Collectors.toList()));
        List<PricingListResult> records = baseMapper.list(new Page<>(1, importDtoMap.size()), pricingListRequest).getRecords();

        return records.stream()
                .map(t -> {
                    PricingStockQueryImportRequest.ImportDto importDto = importDtoMap.get(t.getStockSn());
                    return PricingStockQueryImportResult.builder()
                            .id(t.getId())
                            .serialNo(t.getSerialNo())
                            .stockSn(t.getStockSn())
                            .tobPrice(importDto.getBPrice())
                            .tocPrice(importDto.getCPrice())
                            .goodsLevel(importDto.getGoodsLevel())
                            .consignmentPrice(importDto.getConsignmentPrice())
                            .salesPriority(Integer.valueOf(importDto.getSalesPriority()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void convertConsingment(PricingListResult pricingListResult) {
        Stock consignmentPrice = stockMapper.getConsignmentPrice2(pricingListResult.getStockId(), pricingListResult.getFixPrice());
        PurchaseSubject purchaseSubject = purchaseSubjectMapper.selectById(consignmentPrice.getSourceSubjectId());
        if (Objects.nonNull(pricingListResult)) {
            pricingListResult.setConsignmentPrice(consignmentPrice.getConsignmentPrice());
            if (Objects.nonNull(purchaseSubject)) {
                pricingListResult.setConsignmentPoint(purchaseSubject.getConsignmentPoint());
            }
        }
    }

    @Override
    public int batchUpdateSalesPriority(SalesPriorityModifyDTO dto) {
        return baseMapper.batchUpdateSalesPriority(dto);
    }

    @Override
    public void updateByStockId(Integer stockId, BigDecimal finalPurchase) {
        baseMapper.updateByStockId(stockId, finalPurchase);
    }
}




