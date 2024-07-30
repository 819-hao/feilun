package com.seeease.flywheel.serve.goods.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.IStockPromotionFacade;
import com.seeease.flywheel.goods.request.*;
import com.seeease.flywheel.goods.result.StockPromotionImportResult;
import com.seeease.flywheel.goods.result.StockPromotionInfo;
import com.seeease.flywheel.goods.result.StockPromotionListResult;
import com.seeease.flywheel.goods.result.StockPromotionLogResult;
import com.seeease.flywheel.serve.base.BigDecimalUtil;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.goods.convert.StockConverter;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.StockPromotion;
import com.seeease.flywheel.serve.goods.entity.StockPromotionHistory;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.enums.StockPromotionEnum;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockPromotionHistoryService;
import com.seeease.flywheel.serve.goods.service.StockPromotionService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.entity.StoreManagementInfo;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@DubboService(version = "1.0.0")
public class StockPromotionFacade implements IStockPromotionFacade {

    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private PurchaseSubjectService purchaseSubjectService;
    @Resource
    private StockPromotionService promotionService;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private StockService stockService;
    @Resource
    private StockPromotionHistoryService promotionHistoryService;

    private static final List<Integer> STATUS_APPLY = ImmutableList.of(
            StockStatusEnum.MARKETABLE.getValue(),
            StockStatusEnum.WAIT_PRICING.getValue(),
            StockStatusEnum.ALLOCATE_IN_TRANSIT.getValue(),
            StockStatusEnum.EXCEPTION_IN.getValue(),
            StockStatusEnum.EXCEPTION.getValue()
    );
    private static final List<Integer> STOCK_SRC_APPLY = ImmutableList.of(
            BusinessBillTypeEnum.GR_JS.getValue(),
            BusinessBillTypeEnum.TH_JS.getValue()
    );

    @Override
    public PageResult<StockPromotionListResult> queryStockPromotionList(StockPromotionListRequest request) {
        Page<StockPromotionListResult> page = promotionService.pageByRequest(request);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageResult.<StockPromotionListResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }
        List<WatchDataFusion> fusionList = goodsWatchService.getWatchDataFusionListByStockIds(
                page.getRecords()
                        .stream()
                        .map(StockPromotionListResult::getStockId).collect(Collectors.toList()));
        Map<Integer, WatchDataFusion> watchDataFusionMap = fusionList
                .stream().collect(Collectors.toMap(WatchDataFusion::getStockId, Function.identity()));

        //商品位置
        Map<Integer, String> shopMap = storeManagementService.selectInfoByIds(fusionList
                        .stream()
                        .map(WatchDataFusion::getLocationId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName));

        //经营权
        Map<Integer, String> rightOfManagementMap = purchaseSubjectService.listByIds(fusionList
                        .stream()
                        .map(WatchDataFusion::getRightOfManagement)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));

        page.getRecords().forEach(result -> {
            WatchDataFusion fusion = watchDataFusionMap.get(result.getStockId());
            if (Objects.nonNull(fusion)) {
                result.setStockStatus(fusion.getStockStatus());
                result.setModel(fusion.getModel());
                result.setFiness(fusion.getFiness());
                result.setStockSrc(fusion.getStockSrc());
                result.setBrandName(fusion.getBrandName());
                result.setSeriesName(fusion.getSeriesName());
                result.setTotalStorageAge(fusion.getTotalStorageAge());
                result.setLocationName(shopMap.get(fusion.getLocationId()));
                result.setRightOfManagementName(rightOfManagementMap.get(fusion.getRightOfManagement()));
            }
        });

        return PageResult.<StockPromotionListResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(StockPromotionBatchUpdateRequest request) {
        Assert.isTrue(CollectionUtils.isNotEmpty(request.getIds()), "ids不能为空");
        Assert.isTrue(Objects.nonNull(request.getStatus()), "修改状态值不能为空");

        List<StockPromotion> collect = request.getIds()
                .stream()
                .map(id -> {
                    StockPromotion stockPromotion = new StockPromotion();
                    stockPromotion.setId(id);
                    stockPromotion.setStatus(StockPromotionEnum.fromValue(request.getStatus()));
                    return stockPromotion;
                }).collect(Collectors.toList());

        promotionService.updateBatchById(collect);

        //log
        List<StockPromotion> stockPromotions = promotionService.listByIds(request.getIds());
        if (stockPromotions.isEmpty()) {
            return;
        }

        if (request.getStatus().equals(StockPromotionEnum.STOP_PRODUCTION.getValue())) {
            List<StockPromotionHistory> logs = stockPromotions.stream().map(v -> {
                StockPromotionHistory log = new StockPromotionHistory();
                log.setStockSn(v.getStockSn());
                log.setStockId(v.getStockId());
                log.setPromotionPrice(v.getPromotionPrice());
                log.setPromotionConsignmentPrice(v.getPromotionConsignmentPrice());
                log.setConsignmentRatio(v.getConsignmentRatio());
                log.setStartTime(v.getStartTime());
                log.setEndTime(v.getEndTime());
                return log;
            }).collect(Collectors.toList());
            promotionHistoryService.saveBatch(logs);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResult<StockPromotionImportResult> stockQueryImport(StockPromotionImportRequest request) {
        Map<String, StockPromotionImportRequest.ImportDto> stockSnMap = request.getDataList()
                .stream()
                .collect(Collectors.toMap(StockPromotionImportRequest.ImportDto::getStockSn, Function.identity()));

        //查询的表身号条件
        List<String> stockSnList = new ArrayList<>(stockSnMap.keySet());

        Map<String, Stock> stockMap = new HashMap<>(request.getDataList().size());

        Lists.partition(stockSnList, 500).forEach(lis -> {
            //分批查询 状态符合 并且总库龄 >= 90天 同行寄售个人寄售排除
            Map<String, Stock> collectByStock = stockService.list(Wrappers.<Stock>lambdaQuery()
                            .in(Stock::getSn, lis)
                            .in(Stock::getStockStatus, STATUS_APPLY)
//                            .ge(Stock::getTotalStorageAge, 90)
//                            .notIn(Stock::getStockSrc, STOCK_SRC_APPLY)
                            .isNull(Stock::getTemp))
                    .stream().collect(Collectors.toMap(Stock::getSn, Function.identity()));
            stockMap.putAll(collectByStock);
        });

        //将之前上架过的做逻辑删除
        List<StockPromotion> stockPromotionList = promotionService.list(new LambdaQueryWrapper<StockPromotion>()
                .in(StockPromotion::getStockSn, stockSnList)
                .eq(StockPromotion::getDeleted, WhetherEnum.NO.getValue()));
        if (CollectionUtils.isNotEmpty(stockPromotionList)) {
            promotionService.removeBatchByIds(stockPromotionList
                    .stream().map(StockPromotion::getId).collect(Collectors.toList()));
            List<StockPromotionHistory> logs = stockPromotionList.stream().map(v -> {
                StockPromotionHistory log = new StockPromotionHistory();
                log.setStockSn(v.getStockSn());
                log.setStockId(v.getStockId());
                log.setPromotionPrice(v.getPromotionPrice());
                log.setPromotionConsignmentPrice(v.getPromotionConsignmentPrice());
                log.setConsignmentRatio(v.getConsignmentRatio());
                log.setStartTime(v.getStartTime());
                log.setEndTime(v.getEndTime());
                return log;
            }).collect(Collectors.toList());
            promotionHistoryService.saveBatch(logs);
        }
        //抛出表身号不符合活动的数据
        Set<String> errorList = stockSnList.stream().filter(sn -> !stockMap.containsKey(sn)).collect(Collectors.toSet());

        //组装数据
        List<StockPromotion> promotionList = stockMap.values().stream().map(stock -> {
            StockPromotionImportRequest.ImportDto dto = stockSnMap.get(stock.getSn());
            StockPromotion promotion = new StockPromotion();
            promotion.setStockId(stock.getId());
            promotion.setStockSn(stock.getSn());
            promotion.setStatus(StockPromotionEnum.ITEM_UP_SHELF);
            if (Objects.nonNull(dto)) {
                promotion.setPromotionPrice(dto.getPromotionPrice());
                promotion.setConsignmentRatio(dto.getConsignmentRatio());
                promotion.setPromotionConsignmentPrice(BigDecimalUtil.multiplyRoundHalfUp(dto.getPromotionPrice(), dto.getConsignmentRatio()));
                promotion.setStartTime(dto.getStartTime());
                promotion.setEndTime(DateUtils.getTimeOfAddDay(dto.getEndTime(), 0, 23, 59, 59));
            }
            return promotion;
        }).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(promotionList))
            promotionService.saveBatch(promotionList);

        return ImportResult.<StockPromotionImportResult>builder()
                .successList(promotionList.stream().map(stockPromotion ->
                                StockPromotionImportResult.builder().stockSn(stockPromotion.getStockSn()).build())
                        .collect(Collectors.toList()))
                .errList(Lists.newArrayList(errorList))
                .build();
    }

    @Override
    public void stockQueryTakeDownImport(StockPromotionTakeDownImportRequest request) {
        List<String> sn = request.getDataList().stream().map(StockPromotionTakeDownImportRequest.ImportDto::getStockSn).collect(Collectors.toList());
        LambdaQueryWrapper<StockPromotion> qw = Wrappers.<StockPromotion>lambdaQuery()
                .in(StockPromotion::getStockSn, sn)
                .eq(StockPromotion::getDeleted, WhetherEnum.NO.getValue())
                .eq(StockPromotion::getStatus, StockPromotionEnum.ITEM_UP_SHELF);
        List<Integer> promotionIds = promotionService.list(qw).stream().map(StockPromotion::getId).collect(Collectors.toList());
        if (promotionIds.isEmpty()) {
            return;
        }
        StockPromotionBatchUpdateRequest req = new StockPromotionBatchUpdateRequest();
        req.setIds(promotionIds);
        req.setStatus(StockPromotionEnum.STOP_PRODUCTION.getValue());
        batchUpdateStatus(req);
    }

    @Override
    public PageResult<StockPromotionLogResult> logs(StockPromotionListRequest request) {
        Page<StockPromotionLogResult> page = promotionService.pageOfLog(request);
        return PageResult.<StockPromotionLogResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public StockPromotionInfo get(StockPromotionInfoGetRequest request) {
        Date nowDate = DateUtils.getNowDate();
        return promotionService.list(new LambdaQueryWrapper<StockPromotion>()
                        .eq(StockPromotion::getStockId, request.getStockId())
                        .eq(StockPromotion::getStatus, StockPromotionEnum.ITEM_UP_SHELF)
                        .le(StockPromotion::getStartTime, nowDate)
                        .ge(StockPromotion::getEndTime, nowDate)
                        .eq(StockPromotion::getDeleted, WhetherEnum.NO.getValue()))
                .stream()
                .map(StockConverter.INSTANCE::convertStockPromotionInfo)
                .findFirst()
                .orElse(null);
    }
}
