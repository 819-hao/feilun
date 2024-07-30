package com.seeease.flywheel.serve.goods.rpc;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.IScrapStockFacade;
import com.seeease.flywheel.goods.request.*;
import com.seeease.flywheel.goods.result.ScrapOrderDetailResult;
import com.seeease.flywheel.goods.result.ScrapOrderPageResult;
import com.seeease.flywheel.goods.result.ScrapStockPageResult;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.goods.convert.ScrapStockConverter;
import com.seeease.flywheel.serve.goods.convert.StockLifeCycleConverter;
import com.seeease.flywheel.serve.goods.entity.*;
import com.seeease.flywheel.serve.goods.enums.ScrapStockStateEnum;
import com.seeease.flywheel.serve.goods.service.*;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.springframework.context.UserContext;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@DubboService(version = "1.0.0")
public class ScrapStockFacade implements IScrapStockFacade {
    @Resource
    private StockService stockService;
    @Resource
    private ScrapStockService scrapStockService;
    @Resource
    private BillStockScrapService stockScrapService;
    @Resource
    private BillStockScrapLineService stockScrapLineService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private PurchaseSubjectService purchaseSubjectService;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private CustomerContactsService contactsService;
    @Resource
    private BillLifeCycleService billLifeCycleService;

    @Override
    public void scrappingStock(ScrappingStockRequest request) {
        Assert.notNull(request, "request 不能为空");
        Assert.isTrue(CollectionUtil.isNotEmpty(request.getIds()), "request.getIds() 不能为空");
        Assert.notNull(request.getScrapReason(), "报废原因不能为空");
        scrapStockService.scrappingStock(request);
    }

    @Override
    public PageResult<ScrapStockPageResult> queryPage(ScrapStockPageRequest request) {
        Page<ScrapStockPageResult> page = scrapStockService.queryPage(request);
        if (CollectionUtil.isEmpty(page.getRecords())) {
            return PageResult.<ScrapStockPageResult>builder()
                    .result(Lists.newArrayList())
                    .totalCount(page.getTotal())
                    .totalPage(page.getPages())
                    .build();
        }
        //商品位置
        Map<Integer, String> shopMap = storeManagementService.getStoreMap();

        //经营权
        Map<Integer, String> subjectMap = purchaseSubjectService.list()
                .stream()
                .collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));

        Map<Integer, WatchDataFusion> fusionMap = goodsWatchService.getWatchDataFusionListByStockIds(page.getRecords()
                        .stream()
                        .map(ScrapStockPageResult::getStockId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(WatchDataFusion::getStockId, Function.identity()));
        Map<Integer, CustomerContacts> contactsMap = contactsService.listByIds(page.getRecords()
                        .stream()
                        .map(ScrapStockPageResult::getCcId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(CustomerContacts::getId, Function.identity()));
        page.getRecords().forEach(s -> {
            s.setBelongName(subjectMap.get(s.getBelongId()));
            s.setRightOfManagementName(subjectMap.get(s.getRightOfManagement()));
            s.setLocationName(shopMap.get(s.getLocationId()));
            WatchDataFusion fusion = fusionMap.get(s.getStockId());
            if (Objects.nonNull(fusion)) {
                s.setModel(fusion.getModel());
                s.setBrandName(fusion.getBrandName());
                s.setSeriesName(fusion.getSeriesName());
                s.setImage(fusion.getImage());
                s.setStockSn(fusion.getStockSn());
                s.setPricePub(fusion.getPricePub());
                s.setAttachment(fusion.getAttachment());
                s.setWno(fusion.getWno());
            }
            s.setCustomerName(contactsMap.getOrDefault(s.getCcId(), new CustomerContacts()).getName());
        });
        return PageResult.<ScrapStockPageResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public void scrapTransitionAnomaly(ScrapTransitionAnomalyRequest request) {
        Assert.notNull(request, "request 不能为空");
        Assert.isTrue(CollectionUtil.isNotEmpty(request.getIds()), "request.getIds() 不能为空");
        scrapStockService.scrapTransitionAnomaly(request);
    }

    @Override
    @Transactional
    public void scrapStorage(ScrapStorageRequest request) {
        Assert.notNull(request, "request 不能为空");
        Assert.isTrue(CollectionUtil.isNotEmpty(request.getLines()), "request.getLines() 不能为空");
        Assert.notNull(request.getScrapReason(), "报废原因不能为空");
        BillStockScrap stockScrap = new BillStockScrap();
        stockScrap.setScrapReason(request.getScrapReason());
        stockScrap.setBatchImagUrl(request.getBatchImagUrl());
        stockScrap.setSerialNo(SerialNoGenerator.generateScrapStockSerialNo());
        stockScrap.setNumber(request.getLines().size());
        stockScrapService.save(stockScrap);

        List<Integer> stockIds = request.getLines().stream().map(ScrapStorageRequest.LineDto::getStockId).collect(Collectors.toList());
        List<Stock> stockList = stockService.listByIds(stockIds);

        List<BillStockScrapLine> lineList = stockList
                .stream()
                .map(s -> {
                    BillStockScrapLine line = ScrapStockConverter.INSTANCE.convertStockToLine(s);
                    line.setScrapId(stockScrap.getId());
                    return line;
                }).collect(Collectors.toList());
        stockScrapLineService.saveBatch(lineList);

        scrapStockService.updateStateByStockIds(stockIds, ScrapStockStateEnum.SCRAPPED);

        List<BillLifeCycle> collect = stockList.stream()
                .map(s ->
                        StockLifeCycleCreateRequest.builder()
                                .wno(s.getWno())
                                .stockId(s.getId())
                                .originSerialNo(stockScrap.getSerialNo())
                                .operationDesc("报废出库")
                                .storeId(UserContext.getUser().getStore().getId())
                                .createdBy(UserContext.getUser().getUserName())
                                .updatedBy(UserContext.getUser().getUserName())
                                .createdId(UserContext.getUser().getId())
                                .updatedId(UserContext.getUser().getId())
                                .build()
                ).map(StockLifeCycleConverter.INSTANCE::convert)
                .collect(Collectors.toList());

        billLifeCycleService.insertBatchSomeColumn(collect);
    }

    @Override
    public PageResult<ScrapOrderPageResult> queryScrapOrderPage(ScrapOrderPageRequest request) {
        Page<ScrapOrderPageResult> page = stockScrapService.queryScrapOrderPage(request);
        return PageResult.<ScrapOrderPageResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public ScrapOrderDetailResult queryScrapOrderDetail(ScrapOrderDetailRequest request) {
        BillStockScrap billStockScrap = stockScrapService.getById(request.getId());
        ScrapOrderDetailResult result = ScrapStockConverter.INSTANCE.convertBillStockScrap(billStockScrap);
        //商品位置
        Map<Integer, String> shopMap = storeManagementService.getStoreMap();

        //经营权
        Map<Integer, String> subjectMap = purchaseSubjectService.list()
                .stream()
                .collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));
        List<BillStockScrapLine> lineList = stockScrapLineService.list(new LambdaQueryWrapper<BillStockScrapLine>()
                .eq(BillStockScrapLine::getScrapId, request.getId()));

        Map<Integer, WatchDataFusion> fusionMap = goodsWatchService.getWatchDataFusionListByStockIds(lineList
                        .stream()
                        .map(BillStockScrapLine::getStockId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(WatchDataFusion::getStockId, Function.identity()));
        result.setLines(stockScrapLineService.list(new LambdaQueryWrapper<BillStockScrapLine>()
                        .eq(BillStockScrapLine::getScrapId, request.getId()))
                .stream()
                .map(b -> {
                    ScrapOrderDetailResult.LineDto s = new ScrapOrderDetailResult.LineDto();
                    s.setStockId(b.getStockId());
                    s.setBelongId(b.getBelongId());
                    s.setLocationId(b.getLocationId());
                    WatchDataFusion fusion = fusionMap.get(s.getStockId());
                    if (Objects.nonNull(fusion)) {
                        s.setModel(fusion.getModel());
                        s.setBrandName(fusion.getBrandName());
                        s.setSeriesName(fusion.getSeriesName());
                        s.setStockSn(fusion.getStockSn());
                        s.setPricePub(fusion.getPricePub());
                        s.setAttachment(fusion.getAttachment());
                        s.setFiness(fusion.getFiness());
                        s.setPurchasePrice(fusion.getPurchasePrice());
                    }
                    s.setTotalPrice(b.getTotalPrice());
                    s.setBelongName(subjectMap.get(s.getBelongId()));
                    s.setLocationName(shopMap.get(s.getLocationId()));
                    return s;
                })
                .collect(Collectors.toList()));
        return result;
    }
}