package com.seeease.flywheel.serve.pricing.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.*;
import com.seeease.flywheel.pricing.result.*;
import com.seeease.flywheel.sale.request.SaleReturnStockQueryImportRequest;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.fix.entity.BillFix;
import com.seeease.flywheel.serve.fix.enums.FixStateEnum;
import com.seeease.flywheel.serve.fix.service.BillFixService;
import com.seeease.flywheel.serve.goods.entity.Brand;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.goods.service.BrandService;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.pricing.entity.BillPricing;
import com.seeease.flywheel.serve.pricing.entity.SalesPriorityModifyDTO;
import com.seeease.flywheel.serve.pricing.entity.WuyuPricing;
import com.seeease.flywheel.serve.pricing.enums.AutoPricingMappingEnum;
import com.seeease.flywheel.serve.pricing.enums.PricingStateEnum;
import com.seeease.flywheel.serve.pricing.service.BillPricingService;
import com.seeease.flywheel.serve.pricing.service.WuyuPricingService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.enums.SalesPriorityEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.LoginUser;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.utils.BigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 11:00
 */
@DubboService(version = "1.0.0")
@Slf4j
public class PricingFacade implements IPricingFacade {

    @Resource
    private BillPricingService billPricingService;

    @Resource
    private BillPurchaseService billPurchaseService;

    @Resource
    private BillPurchaseLineService billPurchaseLineService;

    @Resource
    private BillFixService billFixService;

    @Resource
    private StockService stockService;

    @Resource
    private GoodsWatchService goodsWatchService;

    @Resource
    private BrandService brandService;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private WuyuPricingService wuyuPricingService;


    @Override
    public PricingCreateResult create(PricingCreateRequest request) {

        //1.查询表是否有单
        BillPricing billPricing = billPricingService.list(Wrappers.<BillPricing>lambdaQuery()
                        .eq(BillPricing::getStockId, request.getStockId()))
                .stream().findFirst().orElse(null);

        if (request.getAuto()) {
            //有单不创建
            if (Objects.nonNull(billPricing)) {
                return new PricingCreateResult();
            }
            return autoStart(request);
        } else {
            if (request.getAgain() && request.getCancel()) {
                //取消后重启
                return cancelStart(request);
            } else if (request.getAgain() && !request.getCancel()) {
                //重启
                return completeStart(request);
            } else {
                //首次定价
                //有单不创建
                if (Objects.nonNull(billPricing)) {
                    return new PricingCreateResult();
                }
                return initStart(request);
            }
        }
    }

    @NotNull
    private PricingCreateResult initStart(PricingCreateRequest request) {

        if (StringUtils.isNotBlank(request.getOriginSerialNo())) {
            BillPurchase billPurchase = Optional.ofNullable(request)
                    .filter(t -> StringUtils.isNotBlank(t.getOriginSerialNo()))
                    .map(t -> billPurchaseService.getOne(Wrappers.<BillPurchase>lambdaQuery()
                            .eq(BillPurchase::getSerialNo, t.getOriginSerialNo())
                    )).orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_BILL_NOT_EXIST));

            BillPurchaseLine billPurchaseLine = Optional.ofNullable(request)
                    .filter(t -> Objects.nonNull(t.getStockId()))
                    .map(t -> billPurchaseLineService.getOne(Wrappers.<BillPurchaseLine>lambdaQuery()
                            .eq(BillPurchaseLine::getPurchaseId, billPurchase.getId())
                            .eq(BillPurchaseLine::getStockId, t.getStockId())
                    )).orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_BILL_NOT_EXIST));

            request.setPurchasePrice(billPurchaseLine.getPurchasePrice());
        } else {

            BillPurchaseLine billPurchaseLine = Optional.ofNullable(request)
                    .filter(t -> Objects.nonNull(t.getStockId()))
                    .map(t -> billPurchaseLineService.getOne(Wrappers.<BillPurchaseLine>lambdaQuery()
                            .eq(BillPurchaseLine::getStockId, t.getStockId())
                    )).orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_BILL_NOT_EXIST));

            BillPurchase billPurchase = Optional.ofNullable(billPurchaseLine)
                    .filter(t -> ObjectUtils.isNotEmpty(t.getPurchaseId()))
                    .map(t -> billPurchaseService.getOne(Wrappers.<BillPurchase>lambdaQuery()
                            .eq(BillPurchase::getId, t.getPurchaseId())
                    )).orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_BILL_NOT_EXIST));

            request.setPurchasePrice(billPurchaseLine.getPurchasePrice());
            request.setOriginSerialNo(billPurchase.getSerialNo());
        }

        request.setSerialNo(SerialNoGenerator.generatePricingSerialNo());

        return billPricingService.create(request);

    }

    @NotNull
    private PricingCreateResult completeStart(PricingCreateRequest request) {
        BillPricing billPricing = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> billPricingService.getOne(Wrappers.<BillPricing>lambdaQuery()
                        .eq(BillPricing::getPricingState, PricingStateEnum.COMPLETE)
                        .eq(BillPricing::getId, request.getId())
                        .or().eq(BillPricing::getSerialNo, request.getSerialNo())
                )).orElseThrow(() -> new BusinessException(ExceptionCode.PRICING_BILL_NOT_EXIST));

        Optional.ofNullable(billPricing)
                .filter(t -> Objects.nonNull(t.getStockId()))
                .map(t -> stockService.getById(t.getStockId()))
                .filter(t -> !Arrays.asList(StockStatusEnum.SOLD_OUT).contains(t.getStockStatus()))
                .orElseThrow(() -> new BusinessException(ExceptionCode.STOCK_RECE_CONFIRM_AMOUNT_ERR));

        request.setOriginSerialNo(billPricing.getOriginSerialNo());
        request.setStockId(billPricing.getStockId());
        request.setId(billPricing.getId());
        request.setPurchasePrice(billPricing.getPurchasePrice());

        //重新定价
        request.setSerialNo(SerialNoGenerator.generatePricingSerialNo());

        List<BillFix> fixList = billFixService.list(Wrappers.<BillFix>lambdaQuery()
                .eq(BillFix::getFixState, FixStateEnum.NORMAL)
                .eq(BillFix::getStockId, request.getStockId()));
        if (CollectionUtils.isNotEmpty(fixList)) {
            request.setFixPrice(fixList.stream().map(BillFix::getFixMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
            request.setFixDay(fixList.stream().mapToInt(BillFix::getFixDay).sum());
        } else {
            request.setFixPrice(BigDecimal.valueOf(0L));
            request.setFixDay(0);
        }

        return billPricingService.again(request);
    }

    @NotNull
    private PricingCreateResult cancelStart(PricingCreateRequest request) {
        BillPricing billPricing = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getStockId()))
                .map(t -> billPricingService.getOne(Wrappers.<BillPricing>lambdaQuery()
                        .eq(BillPricing::getPricingState, PricingStateEnum.CANCEL_WHOLE)
                        .eq(BillPricing::getStockId, t.getStockId())
                )).orElseThrow(() -> new BusinessException(ExceptionCode.PRICING_BILL_NOT_EXIST));

        request.setOriginSerialNo(billPricing.getOriginSerialNo());
        request.setStockId(billPricing.getStockId());
        request.setId(billPricing.getId());

        //重新定价
        request.setSerialNo(SerialNoGenerator.generatePricingSerialNo());

        // 商品转为待定价
        stockService.updateStockStatus(Arrays.asList(billPricing.getStockId()), StockStatusEnum.TransitionEnum.PURCHASE_RETURNED_ING_WAIT_PRICING);
        return billPricingService.again(request);
    }

    @NotNull
    private PricingCreateResult autoStart(PricingCreateRequest request) {

        if (StringUtils.isNotBlank(request.getOriginSerialNo())) {
            BillPurchase billPurchase = Optional.ofNullable(request)
                    .filter(t -> StringUtils.isNotBlank(t.getOriginSerialNo()))
                    .map(t -> billPurchaseService.getOne(Wrappers.<BillPurchase>lambdaQuery()
                            .eq(BillPurchase::getSerialNo, t.getOriginSerialNo())
                    )).orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_BILL_NOT_EXIST));

            BillPurchaseLine billPurchaseLine = Optional.ofNullable(request)
                    .filter(t -> Objects.nonNull(t.getStockId()))
                    .map(t -> billPurchaseLineService.getOne(Wrappers.<BillPurchaseLine>lambdaQuery()
                            .eq(BillPurchaseLine::getPurchaseId, billPurchase.getId())
                            .eq(BillPurchaseLine::getStockId, t.getStockId())
                    )).orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_BILL_NOT_EXIST));

            request.setPurchasePrice(billPurchaseLine.getPurchasePrice());
            request.setStockId(billPurchaseLine.getStockId());
        } else {

            BillPurchaseLine billPurchaseLine = Optional.ofNullable(request)
                    .filter(t -> Objects.nonNull(t.getStockId()))
                    .map(t -> billPurchaseLineService.getOne(Wrappers.<BillPurchaseLine>lambdaQuery()
                            .eq(BillPurchaseLine::getStockId, t.getStockId())
                    )).orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_BILL_NOT_EXIST));

            BillPurchase billPurchase = Optional.ofNullable(billPurchaseLine)
                    .filter(t -> ObjectUtils.isNotEmpty(t.getPurchaseId()))
                    .map(t -> billPurchaseService.getOne(Wrappers.<BillPurchase>lambdaQuery()
                            .eq(BillPurchase::getId, t.getPurchaseId())
                    )).orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_BILL_NOT_EXIST));

            request.setPurchasePrice(billPurchaseLine.getPurchasePrice());
            request.setOriginSerialNo(billPurchase.getSerialNo());
            request.setStockId(billPurchaseLine.getStockId());
        }

        request.setSerialNo(SerialNoGenerator.generatePricingSerialNo());
        //默认值
        request.setGoodsLevel("压货");
        request.setSalesPriority(2);

        //1.注意维修价是否注入
        List<BillFix> fixList = billFixService.list(Wrappers.<BillFix>lambdaQuery()
                .eq(BillFix::getFixState, FixStateEnum.NORMAL)
                .eq(BillFix::getStockId, request.getStockId()));
        if (CollectionUtils.isNotEmpty(fixList)) {
            request.setFixPrice(fixList.stream().map(BillFix::getFixMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
            request.setFixDay(fixList.stream().mapToInt(BillFix::getFixDay).sum());
        } else {
            request.setFixPrice(BigDecimal.valueOf(0L));
            request.setFixDay(0);
        }
        /**
         * 自动计算tobtoc价格
         */
        autoPrice(request);

        //3。换第三种创建方式
        PricingCreateResult pricingCreateResult = billPricingService.createAuto(request);
        pricingCreateResult.setAuto(2);

        return pricingCreateResult;
    }

    private void autoPrice(PricingCreateRequest request) {

        //2.价格区间规范 如何计算
        AutoPricingMappingEnum autoPricingMappingEnum = AutoPricingMappingEnum.fromCode(request.getPricingSource(), goodsWatchService.getWatchDataFusionListByStockIds(Arrays.asList(request.getStockId())).get(FlywheelConstant.INDEX).getBrandId());

        BigDecimal multiplyToB = this.priceOptimize(request.getPurchasePrice().multiply(autoPricingMappingEnum.getToBMargin()));
        request.setTobPrice(multiplyToB);
        request.setBMargin(autoPricingMappingEnum.getToBMargin());
        log.info("toB价格:{}", multiplyToB);

        BigDecimal multiplyToC = this.priceOptimize(request.getPurchasePrice().multiply(autoPricingMappingEnum.getToCMargin()));
        request.setTocPrice(multiplyToC);
        request.setCMargin(autoPricingMappingEnum.getToCMargin());
        log.info("toC价格:{}", multiplyToC);
        if (multiplyToC.compareTo(autoPricingMappingEnum.getToCCeilingPrice()) > 0) {
            multiplyToC = this.priceOptimize(request.getPurchasePrice().multiply(autoPricingMappingEnum.getToCOtherMargin()));
            request.setTocPrice(multiplyToC);
            request.setCMargin(autoPricingMappingEnum.getToCOtherMargin());
            log.info("变更toC价格:{}", multiplyToC);
        }
        BigDecimal consignmentPrice = stockMapper.getConsignmentPrice2(request.getStockId(), request.getFixPrice()).getConsignmentPrice();
        request.setConsignmentPrice(consignmentPrice);
        log.info("寄售价格:{}", consignmentPrice);

        if (multiplyToB.compareTo(multiplyToC) == 0) {
            return;
        }

        //寄售价比较 //进行b价c价比较，自动更改b价
        Boolean b = ImmutableRangeMap.<Comparable<BigDecimal>, Boolean>builder()
                .put(Range.open(multiplyToB, multiplyToC), true)
                .put(Range.atLeast(multiplyToC), false)
                .build().get(consignmentPrice);

        if (ObjectUtils.isNotEmpty(b) && b) {
            //变更//记录
            log.info("寄售价格:{},最终toB价格:{}", consignmentPrice, multiplyToB);
            request.setTobPrice(this.priceOptimize(consignmentPrice));
        } else if (ObjectUtils.isNotEmpty(b) && !b) {
            log.info("寄售价格:{},最终toC价格:{}", consignmentPrice, multiplyToC);
        }
    }

    @Override
    public PricingFinishResult finish(PricingFinishRequest request) {

        return billPricingService.finish(request);
    }

    @Override
    public PricingFinishBatchResult finishBatch(PricingFinishBatchRequest request) {
        return billPricingService.finishBatch(request);
    }

    @Override
    public PricingCompletedResult completed(PricingCompletedRequest request) {
        return billPricingService.completed(request);
    }

    @Override
    public List<PricingCompletedResult> batchPass(List<Integer> request) {

        return request.stream().map(item -> {
            PricingCompletedRequest pricingCompletedRequest = new PricingCompletedRequest();
            pricingCompletedRequest.setId(item);
            pricingCompletedRequest.setCheckState(WhetherEnum.YES.getValue());
            return billPricingService.completed(pricingCompletedRequest);
        }).collect(Collectors.toList());
    }

    @Override
    public PricingDetailsResult details(PricingDetailsRequest request) {
        return billPricingService.details(request);
    }

    @Override
    public PageResult<PricingListResult> list(PricingListRequest request) {
        return billPricingService.list(request);
    }

    @Override
    public PageResult<PricingLogListResult> logList(PricingLogListRequest request) {
        return billPricingService.logList(request);
    }

    @Override
    public PricingCancelResult cancel(PricingCancelRequest request) {
        return billPricingService.cancel(request);
    }

    @Override
    public ImportResult<PricingStockQueryImportResult> stockQueryImport(PricingStockQueryImportRequest request,Integer state) {

        List<PricingStockQueryImportResult> list = billPricingService.importList(request,state);

        return ImportResult.<PricingStockQueryImportResult>builder()
                .successList(list)
                .errList(request.getDataList().stream().filter(importDto -> !list.stream().map(PricingStockQueryImportResult::getStockSn).collect(Collectors.toList()).contains(importDto.getStockSn())).map(importDto -> importDto.getStockSn()).collect(Collectors.toList()))
                .build();
    }

    @Override
    public ImportResult<SalesPriorityModifyImportResult> salesPriorityModifyImport(SalesPriorityModifyImportRequest request) {

        Assert.isTrue(request.getDataList().stream().map(SalesPriorityModifyImportRequest.ImportDto::getBrandName)
                .distinct().count() == NumberUtils.LONG_ONE, "不支持多瓶品牌同时操作修改");

        Brand brand = brandService.getOne(Wrappers.<Brand>lambdaQuery()
                .eq(Brand::getName, request.getDataList().stream().map(SalesPriorityModifyImportRequest.ImportDto::getBrandName)
                        .findFirst().get()));

        Map<String, GoodsWatch> goodsMap = goodsWatchService.list(Wrappers.<GoodsWatch>lambdaQuery()
                        .eq(GoodsWatch::getBrandId, Optional.ofNullable(brand).map(Brand::getId).orElse(null))
                        .in(GoodsWatch::getModel, request.getDataList().stream().map(SalesPriorityModifyImportRequest.ImportDto::getModel)
                                .collect(Collectors.toList())))
                .stream()
                .collect(Collectors.toMap(GoodsWatch::getModel, Function.identity()));

        List<String> errList = new ArrayList<>();
        List<SalesPriorityModifyImportResult> successList = new ArrayList<>();

        LoginUser user = UserContext.getUser();
        request.getDataList().forEach(t -> {
            try {
                GoodsWatch goods = goodsMap.get(t.getModel());
                if (Objects.isNull(goods)) {
                    errList.add(t.getModel());
                    return;
                }
                //更新
                int rows = billPricingService.batchUpdateSalesPriority(SalesPriorityModifyDTO.builder()
                        .goodsId(goods.getId())
                        .salesPriority(Optional.ofNullable(t.getSalesPriority())
                                .filter(StringUtils::isNotBlank)
                                .map(Integer::valueOf)
                                .orElse(null))
                        .goodsLevel(Optional.ofNullable(t.getGoodsLevel())
                                .filter(StringUtils::isNotBlank)
                                .orElse(null))
                        .stockStatusList(StockStatusEnum.getInStoreStockStatusEnum()
                                .stream()
                                .map(StockStatusEnum::getValue)
                                .collect(Collectors.toList()))
                        .updatedId(user.getId())
                        .updatedBy(user.getUserName())
                        .build());

                successList.add(SalesPriorityModifyImportResult.builder()
                        .brandName(t.getBrandName())
                        .model(t.getModel())
                        .rowsGoods(rows / 2)
                        .build());
            } catch (Exception e) {
                errList.add(t.getModel());
                log.error("批量修改商品销售等级异常:{}", e.getMessage(), e);
            }
        });

        return ImportResult.<SalesPriorityModifyImportResult>builder()
                .errList(errList)
                .successList(successList)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void wuyuPricing(List<WuyuPricingRequest.Item> request) {
        request.forEach(v -> {
            Stock stock = stockService.getById(v.getId());

            Stock newStock = new Stock();
            newStock.setId(stock.getId());
            newStock.setPurchasePrice(v.getNewPurchasePrice());
            newStock.setWuyuPrice(v.getNewWuyuPrice());
            newStock.setWuyuBuyBackPrice(v.getNewWuyuBuyBackPrice());
            newStock.setTransitionStateEnum(null);

            stockService.updateById(newStock);

            WuyuPricing p = new WuyuPricing();
            BeanUtils.copyProperties(v, p);
            p.setId(null);

            wuyuPricingService.save(p);
        });
    }

    @Override
    public PageResult<WuyuPricingPageResult> page(WuyuPricingPageRequest request) {
        LambdaQueryWrapper<WuyuPricing> qw = Wrappers.<WuyuPricing>lambdaQuery()
                .like(StringUtils.isNotEmpty(request.getStockSn()), WuyuPricing::getStockSn, request.getStockSn())
                .between(request.getBeginCreateTime() != null && request.getEndCreateTime() != null, WuyuPricing::getCreatedTime, request.getBeginCreateTime(), request.getEndCreateTime())
                .orderByDesc(WuyuPricing::getCreatedTime);

        Page<WuyuPricing> page = new Page<>(request.getPage(),request.getLimit());
        wuyuPricingService.page(page, qw);

        return PageResult.<WuyuPricingPageResult>builder()
                .result(page.getRecords().stream().map(v->{
                    WuyuPricingPageResult p = new WuyuPricingPageResult();
                    BeanUtils.copyProperties(v,p);
                    p.setCreatedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(v.getCreatedTime()));
                    return p;
                }).collect(Collectors.toList()))
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void wuyuPricingImport(WuyuPricingImportRequest request) {
        request.getDataList().forEach(v->{

            List<Stock> stockList = stockService.findByStockSn(v.getStockSn());
            if (!stockList.isEmpty()){
                Stock stock = stockList.get(0);
                WuyuPricing p = new WuyuPricing();

                p.setStockSn(v.getStockSn());
                p.setReason(v.getReason());
                if (StringUtils.isNotEmpty(v.getNewPurchasePrice())){
                    p.setNewPurchasePrice(new BigDecimal(v.getNewPurchasePrice()));
                }
//                if (StringUtils.isNotEmpty(v.getNewWuyuPrice())){
//                    p.setNewWuyuPrice(new BigDecimal(v.getNewWuyuPrice()));
//                }
                if (StringUtils.isNotEmpty(v.getNewWuyuBuyBackPrice())){
                    p.setNewWuyuBuyBackPrice(new BigDecimal(v.getNewWuyuBuyBackPrice()));
                }

                p.setPurchasePrice(stock.getPurchasePrice());
                p.setWuyuPrice(stock.getWuyuPrice());
                p.setWuyuBuyBackPrice(stock.getWuyuBuyBackPrice());
                wuyuPricingService.save(p);

                Stock newStock = new Stock();
                newStock.setId(stock.getId());
                if (StringUtils.isNotEmpty(v.getNewPurchasePrice())){
                    newStock.setPurchasePrice(new BigDecimal(v.getNewPurchasePrice()));
                }
//                if (StringUtils.isNotEmpty(v.getNewWuyuPrice())){
//                    newStock.setWuyuPrice(new BigDecimal(v.getNewWuyuPrice()));
//                }
                if (StringUtils.isNotEmpty(v.getNewWuyuBuyBackPrice())){
                    newStock.setWuyuBuyBackPrice(new BigDecimal(v.getNewWuyuBuyBackPrice()));
                }
                newStock.setTransitionStateEnum(null);

                stockService.updateById(newStock);
            }

        });

    }

    @Override
    public ImportResult<PricingStockQueryImportResult> update(PricingStockQueryImportRequest request) {
        Map<String, PricingStockQueryImportRequest.ImportDto> map = request
                .getDataList()
                .stream()
                .collect(Collectors.toMap(PricingStockQueryImportRequest.ImportDto::getStockSn, Function.identity()));

        ImportResult<PricingStockQueryImportResult> result = stockQueryImport(request, 2);
        result.getSuccessList().forEach(item->{
            PricingStockQueryImportRequest.ImportDto e = map.get(item.getStockSn());

            BillPricing bp = new BillPricing();
            bp.setId(item.getId());
            bp.setBPrice(e.getBPrice());
            bp.setCPrice(e.getCPrice());
            bp.setSalesPriority(SalesPriorityEnum.fromCode(Integer.parseInt(e.getSalesPriority())));
            bp.setGoodsLevel(e.getGoodsLevel());
            bp.setConsignmentPrice(e.getConsignmentPrice());

            billPricingService.updateById(bp);
        });

        return result;
    }

    /**
     * 价格优化
     *
     * @param b
     * @return
     */
    private BigDecimal priceOptimize(BigDecimal b) {
        if (Objects.nonNull(b)) {
            b = BigDecimalUtil.roundNumberUp(b);
            int mol = b.intValue() % 100;
            if (mol != 0) {
                b = b.add(new BigDecimal(99 - mol));
            }
        }
        return b;
    }

}
