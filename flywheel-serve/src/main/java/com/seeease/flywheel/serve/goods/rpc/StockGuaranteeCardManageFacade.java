package com.seeease.flywheel.serve.goods.rpc;

/**
 * @author Tiro
 * @date 2023/11/20
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.IStockGuaranteeCardManageFacade;
import com.seeease.flywheel.goods.entity.StockGuaranteeCardManageInfo;
import com.seeease.flywheel.goods.request.StockGuaranteeCardManageEditRequest;
import com.seeease.flywheel.goods.request.StockGuaranteeCardManageFindRequest;
import com.seeease.flywheel.goods.request.StockGuaranteeCardManageListRequest;
import com.seeease.flywheel.goods.request.StockGuaranteeCardManageUpdateRequest;
import com.seeease.flywheel.pricing.request.StockGuaranteeCardManageImportRequest;
import com.seeease.flywheel.pricing.result.StockGuaranteeCardManageImportResult;
import com.seeease.flywheel.serve.allocate.entity.BillAllocatePO;
import com.seeease.flywheel.serve.allocate.service.BillAllocateService;
import com.seeease.flywheel.serve.goods.convert.StockGuaranteeCardManageConverter;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.StockGuaranteeCardManage;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.enums.SeriesTypeEnum;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.SeriesService;
import com.seeease.flywheel.serve.goods.service.StockGuaranteeCardManageService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@DubboService(version = "1.0.0")
public class StockGuaranteeCardManageFacade implements IStockGuaranteeCardManageFacade {
    @Resource
    private StockService stockService;
    @Resource
    private SeriesService seriesService;
    @Resource
    private StockGuaranteeCardManageService manageService;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private BillAllocateService allocateService;
    @Resource
    private StoreManagementService storeManagementService;

    @Override
    public PageResult<StockGuaranteeCardManageInfo> list(StockGuaranteeCardManageListRequest request) {

        LambdaQueryWrapper<StockGuaranteeCardManage> wrapper = Wrappers.<StockGuaranteeCardManage>lambdaQuery().orderByDesc(StockGuaranteeCardManage::getId);
        if (StringUtils.isNotBlank(request.getStockSn())) {
            List<Integer> stockIdList = stockService.findByStockSn(request.getStockSn())
                    .stream()
                    .map(Stock::getId)
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(stockIdList)) {
                return PageResult.<StockGuaranteeCardManageInfo>builder()
                        .totalCount(NumberUtils.INTEGER_ZERO)
                        .totalPage(NumberUtils.INTEGER_ZERO)
                        .build();
            }
            wrapper.in(StockGuaranteeCardManage::getStockId, stockIdList);
        }

        Page<StockGuaranteeCardManage> result = manageService.page(Page.of(request.getPage(), request.getLimit()), wrapper
                .eq(StringUtils.isNotEmpty(request.getAllocateNo()), StockGuaranteeCardManage::getAllocateNo, request.getAllocateNo())
                .eq(Objects.nonNull(request.getAllocateState())
                        && request.getAllocateState().intValue() != NumberUtils.INTEGER_MINUS_ONE, StockGuaranteeCardManage::getAllocateState, request.getAllocateState()));


        Map<Integer, WatchDataFusion> stockMap = CollectionUtils.isNotEmpty(result.getRecords()) ?
                goodsWatchService.getWatchDataFusionListByStockIds(result.getRecords().stream().map(StockGuaranteeCardManage::getStockId).collect(Collectors.toList()))
                        .stream()
                        .collect(Collectors.toMap(WatchDataFusion::getStockId, Function.identity())) : Collections.emptyMap();

        Map<Integer, BillAllocatePO> allocateMap = CollectionUtils.isNotEmpty(result.getRecords()) ?
                allocateService.selectByStockIds(result.getRecords().stream().map(StockGuaranteeCardManage::getStockId).collect(Collectors.toList()))
                        .stream()
                        .collect(Collectors.toMap(BillAllocatePO::getStockId, Function.identity())) : Collections.emptyMap();

        Map<Integer, String> storeMap = storeManagementService.getStoreMap();

        return PageResult.<StockGuaranteeCardManageInfo>builder()
                .result(result.getRecords()
                        .stream()
                        .map(t -> {
                            StockGuaranteeCardManageInfo info = StockGuaranteeCardManageConverter.INSTANCE.convertInfo(t);

                            WatchDataFusion stock = stockMap.get(t.getStockId());
                            if (Objects.nonNull(stock)) {
                                info.setStockSn(stock.getStockSn());
                                info.setModel(stock.getModel());
                                info.setBrandName(stock.getBrandName());
                                info.setSeriesName(stock.getSeriesName());
                            }
                            BillAllocatePO allocatePO = allocateMap.get(t.getStockId());
                            if (Objects.nonNull(allocatePO)) {
                                info.setAllocateNo(allocatePO.getSerialNo());
                                info.setToName(storeMap.get(allocatePO.getToId()));
                            }
                            return info;
                        })
                        .collect(Collectors.toList()))
                .totalCount(result.getTotal())
                .totalPage(result.getPages())
                .build();
    }

    @Override
    public List<StockGuaranteeCardManageInfo> find(StockGuaranteeCardManageFindRequest request) {
        return manageService.list(Wrappers.<StockGuaranteeCardManage>lambdaQuery()
                        .in(StockGuaranteeCardManage::getStockId, request.getStockIdList()))
                .stream()
                .map(StockGuaranteeCardManageConverter.INSTANCE::convertInfo)
                .collect(Collectors.toList());
    }

    @Override
    public void update(StockGuaranteeCardManageUpdateRequest request) {
        if (CollectionUtils.isNotEmpty(request.getIdList())) {
            request.getIdList().forEach(id -> {
                StockGuaranteeCardManage up = StockGuaranteeCardManageConverter.INSTANCE.convert(request);
                up.setId(id);
                manageService.updateById(up);
            });
        } else if (Objects.nonNull(request.getId())) {
            manageService.updateById(StockGuaranteeCardManageConverter.INSTANCE.convert(request));
        }
    }


    @Override
    public ImportResult<StockGuaranteeCardManageImportResult> importHandle(StockGuaranteeCardManageImportRequest request) {
        List<Stock> stockList = stockService.list(Wrappers.<Stock>lambdaQuery()
                .in(Stock::getSn, request.getDataList().stream()
                        .map(StockGuaranteeCardManageImportRequest.ImportDto::getStockSn).collect(Collectors.toList()))
                .in(Stock::getIsCard, 1, 2)
                .in(Stock::getStockStatus, StockStatusEnum.getInStoreStockStatusEnum()));

        Map<String, Stock> stockMap = stockList.stream()
                .collect(Collectors.toMap(Stock::getSn, Function.identity(), (k1, k2) -> k2));


        List<Integer> existStockId = CollectionUtils.isNotEmpty(stockList) ?
                manageService.list(Wrappers.<StockGuaranteeCardManage>lambdaQuery()
                                .in(StockGuaranteeCardManage::getStockId, stockList.stream().map(Stock::getId).collect(Collectors.toList())))
                        .stream()
                        .map(StockGuaranteeCardManage::getStockId)
                        .collect(Collectors.toList()) : Collections.EMPTY_LIST;

        List<String> errList = new ArrayList<>();
        List<StockGuaranteeCardManage> manageList = new ArrayList<>();
        AtomicReference<String> cardStr = new AtomicReference<>("空白保卡");
        AtomicReference<String> cardDateStr = new AtomicReference<>("保卡(date)");
        request.getDataList()
                .forEach(t -> {
                    Stock stock = stockMap.get(t.getStockSn());
                    if (Objects.isNull(stock) || existStockId.contains(stock.getId())) {
                        errList.add(t.getStockSn());
                    } else {
                        StockGuaranteeCardManage manage = new StockGuaranteeCardManage();
                        Integer seriesType = seriesService.getSeriesTypeByStockId(stock.getId());
                        if (Objects.nonNull(seriesType) && SeriesTypeEnum.BAGS.getValue().equals(seriesType)) {
                            cardStr.set("空白身份卡");
                            cardDateStr.set("身份卡(date)");
                        }
                        manage.setStockId(stock.getId());
                        manage.setCardInfo(stock.getIsCard() == 2 ? cardStr.get() :
                                StringUtils.isNotBlank(stock.getWarrantyDate())
                                        ? StringUtils.replace(cardDateStr.get(), "date", stock.getWarrantyDate()) : cardStr.get());
                        manage.setAllocateState(WhetherEnum.NO.getValue());
                        manage.setWhetherEdit(WhetherEnum.NO.getValue());
                        manageList.add(manage);
                    }
                });

        if (CollectionUtils.isNotEmpty(manageList)) {
            manageService.insertBatchSomeColumn(manageList);
        }

        return ImportResult.<StockGuaranteeCardManageImportResult>builder()
                .errList(errList)
                .successList(manageList.stream()
                        .map(StockGuaranteeCardManageConverter.INSTANCE::convertResult)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public void edit(StockGuaranteeCardManageEditRequest request) {
        Assert.notNull(request, "request 为空");
        Assert.notNull(request.getId(), "request id 为空");
        StockGuaranteeCardManage s = manageService.getById(request.getId());
        switch (request.getUseScenario()) {
            case COST:
                if (Objects.isNull(s.getCost())) {
                    StockGuaranteeCardManage sg = new StockGuaranteeCardManage();
                    sg.setId(s.getId());
                    sg.setCost(request.getCost());
                    manageService.updateById(sg);
                }
                break;
            case CARD_INFO:
                if (s.getWhetherEdit() == 0) {
                    StockGuaranteeCardManage sg = new StockGuaranteeCardManage();
                    sg.setId(s.getId());
                    sg.setCardInfo(request.getCardInfo());
                    sg.setWhetherEdit(1);
                    manageService.updateById(sg);

                    Stock stock = new Stock();
                    stock.setId(s.getStockId());
                    stock.setIsCard(1);
                    stock.setWarrantyDate(request.getCardInfo());
                    stockService.updateById(stock);
                }
                break;
        }

    }

}
