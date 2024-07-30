package com.seeease.flywheel.serve.allocate.rpc;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.allocate.IAllocateFacade;
import com.seeease.flywheel.allocate.request.*;
import com.seeease.flywheel.allocate.result.*;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.serve.allocate.convert.AllocateConverter;
import com.seeease.flywheel.serve.allocate.entity.BillAllocate;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateLine;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateTask;
import com.seeease.flywheel.serve.allocate.enums.AllocateLineStateEnum;
import com.seeease.flywheel.serve.allocate.enums.AllocateStateEnum;
import com.seeease.flywheel.serve.allocate.service.BillAllocateLineService;
import com.seeease.flywheel.serve.allocate.service.BillAllocateService;
import com.seeease.flywheel.serve.allocate.service.BillAllocateTaskService;
import com.seeease.flywheel.serve.allocate.strategy.AllocateContext;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockGuaranteeCardManageService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.Store;
import com.seeease.flywheel.serve.maindata.entity.StoreManagement;
import com.seeease.flywheel.serve.maindata.entity.StoreManagementInfo;
import com.seeease.flywheel.serve.maindata.entity.StoreRelationshipSubject;
import com.seeease.flywheel.serve.maindata.mapper.StoreManagementMapper;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.StoreRelationshipSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreService;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/3/7
 */
@DubboService(version = "1.0.0")
public class AllocateFacade implements IAllocateFacade {
    @Resource
    private AllocateContext allocateContext;
    @Resource
    private BillAllocateService billAllocateService;
    @Resource
    private BillAllocateLineService billAllocateLineService;
    @Resource
    private StockService stockService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private StoreService storeService;
    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;
    @Resource
    private StoreRelationshipSubjectService storeRelationshipSubjectService;
    @Resource
    private BillAllocateTaskService billAllocateTaskService;
    @Resource
    private StockGuaranteeCardManageService cardManageService;

    @Resource
    private StoreManagementMapper storeManagementMapper;


    @Override
    public AllocateCreateResult create(AllocateCreateRequest request) {
        return allocateContext.create(request);
    }


    @Override
    public PageResult<AllocateListResult> list(AllocateListRequest request) {
        if (StringUtils.isNotBlank(request.getStockSn())) {
            List<Stock> stockList = stockService.findByStockSn(request.getStockSn());
            if (CollectionUtils.isEmpty(stockList)) {
                return PageResult.<AllocateListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .build();
            }
            request.setStockIds(stockList.stream().map(Stock::getId).collect(Collectors.toList()));
        }
        //设置登陆用户门店id
        request.setShopId(Objects.requireNonNull(UserContext.getUser().getStore().getId()));

        Page<BillAllocate> pageResult = billAllocateService.listByRequest(request);
        if (CollectionUtils.isEmpty(pageResult.getRecords())) {
            return PageResult.<AllocateListResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .build();
        }

        //调出调入方
        List<Integer> ids = pageResult.getRecords().stream()
                .map(t -> Lists.newArrayList(t.getFromId(), t.getToId()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, String> tagList = storeManagementService.selectInfoByIds(ids)
                .stream()
                .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName));

        return PageResult.<AllocateListResult>builder()
                .result(pageResult.getRecords()
                        .stream()
                        .map(t -> {
                            AllocateListResult r = AllocateConverter.INSTANCE.convertAllocateListResult(t);
                            r.setFromName(tagList.get(t.getFromId())); //调出方
                            r.setToName(tagList.get(t.getToId())); //调入方
                            return r;
                        })
                        .collect(Collectors.toList())
                )
                .totalCount(pageResult.getTotal())
                .totalPage(pageResult.getPages())
                .build();
    }


    @Override
    public AllocateDetailsResult details(AllocateDetailsRequest request) {
        Integer shopId = UserContext.getUser().getStore().getId();

        BillAllocate allocate = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> billAllocateService.getOne(Wrappers.<BillAllocate>lambdaQuery()
                        .eq(BillAllocate::getId, t.getId())
                        .or().eq(BillAllocate::getSerialNo, t.getSerialNo())
                        //总部可以查看所有调拨单，门店只能查看调入方或调出方是本店的调拨单
                        .and(FlywheelConstant._ZB_ID != shopId, wq -> wq.eq(BillAllocate::getFromId, shopId)
                                .or().eq(BillAllocate::getToId, shopId))
                ))
                .orElseThrow(() -> new BusinessException(ExceptionCode.ALLOCATE_BILL_NOT_EXIST));

        if (Objects.isNull(allocate)) {
            throw new BusinessException(ExceptionCode.ALLOCATE_BILL_NOT_EXIST);
        }
        AllocateDetailsResult result = AllocateConverter.INSTANCE.convertAllocateDetailsResult(allocate);

        //是否来自调拨任务
        result.setBrandTask(billAllocateTaskService.count(Wrappers.<BillAllocateTask>lambdaUpdate().eq(BillAllocateTask::getAllocateId, allocate.getId())) > 0);

        //调出调入方
        Map<Integer, String> tagList = storeManagementService.selectInfoByIds(Lists.newArrayList(allocate.getFromId(), allocate.getToId()))
                .stream()
                .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName));
        result.setFromName(tagList.get(allocate.getFromId())); //调出方
        result.setToName(tagList.get(allocate.getToId())); //调入方

        //调入方仓库
        Store store = storeService.getById(allocate.getToStoreId());
        result.setToStoreName(Optional.ofNullable(store).map(Store::getStoreName).orElse(StringUtils.EMPTY));
        StoreManagement storeManagement = storeManagementService.selectByStoreId(store.getId());
        if (storeManagement != null) {
            result.setAddress(storeManagement.getAddress());
        }
        //调拨行
        List<BillAllocateLine> line = billAllocateLineService.list(Wrappers.<BillAllocateLine>lambdaQuery()
                .eq(BillAllocateLine::getAllocateId, request.getId()));

        //型号信息
        Map<Integer, WatchDataFusion> goodsMap = goodsWatchService.getWatchDataFusionListByGoodsIds(line.stream()
                        .map(BillAllocateLine::getGoodsId)
                        .collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));

        Map<Integer, StockExt> stockMap = Optional.ofNullable(line.stream().map(BillAllocateLine::getStockId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(ids -> stockService.selectByStockIdList(ids)
                        .stream()
                        .collect(Collectors.toMap(StockExt::getStockId, Function.identity())))
                .orElse(Collections.EMPTY_MAP);

        result.setDetails(line.stream()
                .map(t -> {
                    AllocateDetailsResult.AllocateLineVO r = AllocateConverter.INSTANCE.convertAllocateLineVO(t);
                    StockExt stockExt = stockMap.get(r.getStockId());
                    if (Objects.nonNull(stockExt)) {
                        r.setStockSn(stockExt.getStockSn());
                        r.setAttachment(stockExt.getAttachmentDetails());
                        r.setFiness(stockExt.getFiness());
                    }
                    WatchDataFusion goods = goodsMap.get(r.getGoodsId());
                    if (Objects.nonNull(goods)) {
                        r.setBrandName(goods.getBrandName());
                        r.setSeriesName(goods.getSeriesName());
                        r.setModel(goods.getModel());
                        r.setPricePub(goods.getPricePub());
                    }
                    r.setNewSettlePrice(t.getNewSettlePrice() == null ? BigDecimal.ZERO : t.getNewSettlePrice());
                    r.setTransferPrice(t.getTransferPrice() == null ? BigDecimal.ZERO : t.getTransferPrice());
                    if (null != t.getNewSettlePrice() && null != t.getTransferPrice()){
                        r.setProfit(t.getTransferPrice().subtract(t.getNewSettlePrice()));
                    }else {
                        r.setProfit(BigDecimal.ZERO);
                    }
                    return r;
                })
                .collect(Collectors.toList())
        );

        result.setTotalProfit(result.getDetails().stream().map(AllocateDetailsResult.AllocateLineVO::getProfit).reduce(BigDecimal::add).get());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AllocateCancelResult cancel(AllocateCancelRequest request) {
        AllocateCancelResult result = billAllocateLineService.cancel(request);
        //调拨取消原因
        if (StringUtils.isNotBlank(request.getCancelReason())) {
            BillAllocateTask task = new BillAllocateTask();
            task.setCancelReason(request.getCancelReason());
            billAllocateTaskService.update(task, Wrappers.<BillAllocateTask>lambdaUpdate().eq(BillAllocateTask::getAllocateId, request.getAllocateId()));
        }
        //取消作业单
        billStoreWorkPreService.cancel(result.getSerialNo());
        //更新商品状态
        stockService.updateStockStatus(result.getStockIdList(), StockStatusEnum.TransitionEnum.ALLOCATE_CANCEL_OR_IN_STORAGE);
        //调拨取消保卡管理
        cardManageService.allocateCancel(result.getSerialNo());
        return result;
    }

    @Override
    public ImportResult<AllocateStockQueryImportResult> stockQueryImport(AllocateStockQueryImportRequest request) {

        List<String> stockSnList = request.getDataList().stream()
                .map(AllocateStockQueryImportRequest.ImportDto::getStockSn)
                .collect(Collectors.toList());

        StoreRelationshipSubject storeRelationshipSubject = storeRelationshipSubjectService.getByShopId(UserContext.getUser().getStore().getId());


        Map<String, StockBaseInfo> stockMap = Lists.partition(stockSnList, 500)
                .stream()
                .map(snList -> stockService.listSaleableStockBySn(snList, storeRelationshipSubject.getSubjectId()))
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(StockBaseInfo::getStockSn, Function.identity()));

        //校验不可改的数据
        Collection<String> errorStock = CollectionUtils.subtract(stockSnList, stockMap.keySet().stream().collect(Collectors.toList()));

        List<StockBaseInfo> resultList = stockMap.values().stream().collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(resultList)) {
            //商品位置
            Map<Integer, String> shopMap = storeManagementService.selectInfoByIds(resultList
                            .stream()
                            .map(StockBaseInfo::getLocationId)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList()))
                    .stream()
                    .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName));

            resultList.forEach(t -> {
                t.setLocationName(shopMap.get(t.getLocationId()));
                t.setRightOfManagementName(storeRelationshipSubject.getName());
            });
        }

        return ImportResult.<AllocateStockQueryImportResult>builder()
                .successList(resultList.stream()
                        .map(AllocateConverter.INSTANCE::convertAllocateStockQueryImportResult)
                        .collect(Collectors.toList()))
                .errList(errorStock.stream().collect(Collectors.toList()))
                .build();
    }

    @Override
    public PageResult<AllocateExportListResult> export(AllocateExportListRequest request) {
        if (StringUtils.isNotBlank(request.getStockSn())) {
            List<Stock> stockList = stockService.findByStockSn(request.getStockSn());
            if (CollectionUtils.isEmpty(stockList)) {
                return PageResult.<AllocateExportListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .build();
            }
            request.setStockIds(stockList.stream().map(Stock::getId).collect(Collectors.toList()));
        }
        //设置登陆用户门店id
        request.setShopId(Objects.requireNonNull(UserContext.getUser().getStore().getId()));

        Page<BillAllocate> pageResult = billAllocateService.exportListByRequest(request);
        if (CollectionUtils.isEmpty(pageResult.getRecords())) {
            return PageResult.<AllocateExportListResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .build();
        }

        //调出调入方
        List<Integer> baIds = pageResult.getRecords().stream()
                .map(t -> Lists.newArrayList(t.getFromId(), t.getToId()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, String> tagList = storeManagementService.selectInfoByIds(baIds)
                .stream()
                .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName));

        Map<Integer, String> storeMap = storeService.listByIds(pageResult.getRecords().stream().map(BillAllocate::getToStoreId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Store::getId, Store::getStoreName));

        List<AllocateExportListResult> list = pageResult.getRecords().stream().flatMap(billAllocate -> {

            return billAllocateLineService.list(Wrappers.<BillAllocateLine>lambdaQuery()
                    .eq(BillAllocateLine::getAllocateId, billAllocate.getId())).stream().map(t -> {
                AllocateExportListResult r = AllocateConverter.INSTANCE.convertAllocateExportList(t);
                r.setAllocateSource(billAllocate.getAllocateSource().getValue());
                r.setFromName(tagList.get(billAllocate.getFromId())); //调出方
                r.setToName(tagList.get(billAllocate.getToId())); //调入方
                r.setAllocateType(billAllocate.getAllocateType().getValue());
                r.setSerialNo(billAllocate.getSerialNo());
                r.setCreatedBy(billAllocate.getCreatedBy());
                //调入方仓库
                r.setToStoreName(storeMap.getOrDefault(billAllocate.getToStoreId(), StringUtils.EMPTY));
                r.setCreatedTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", billAllocate.getCreatedTime()));
                return r;
            });
        }).collect(Collectors.toList());

        Map<Integer, WatchDataFusion> goodsMap = goodsWatchService.getWatchDataFusionListByGoodsIds(list.stream()
                        .map(AllocateExportListResult::getGoodsId)
                        .collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));

        Map<Integer, StockExt> stockMap = Optional.ofNullable(list.stream().map(AllocateExportListResult::getStockId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(ids -> stockService.selectByStockIdList(ids)
                        .stream()
                        .collect(Collectors.toMap(StockExt::getStockId, Function.identity())))
                .orElse(Collections.EMPTY_MAP);

        list.forEach(r -> {
            StockExt stockExt = stockMap.get(r.getStockId());
            if (Objects.nonNull(stockExt)) {
                r.setStockSn(stockExt.getStockSn());
                //
                r.setWno(stockExt.getWno());
                r.setAttachment(stockExt.getAttachmentDetails());
                r.setFiness(stockExt.getFiness());
                r.setTobPrice(stockExt.getTobPrice());
                r.setTagPrice(stockExt.getTagPrice());
                r.setTocPrice(stockExt.getTocPrice());
            }
            WatchDataFusion goods = goodsMap.get(r.getGoodsId());
            if (Objects.nonNull(goods)) {
                r.setBrandName(goods.getBrandName());
                r.setSeriesName(goods.getSeriesName());
                r.setModel(goods.getModel());
                r.setPricePub(goods.getPricePub());
            }
        });

        return PageResult.<AllocateExportListResult>builder()
                .result(list)
                .totalCount(pageResult.getTotal())
                .totalPage(pageResult.getPages())
                .build();
    }

    @Override
    public ImportResult<AllocateStockBaseInfoImportResult> allocateStockImport(AllocateStockImportRequest request, List<StockBaseInfo> stockBaseInfos) {
        //查询导入表身号
        List<String> snList = request.getDataList().stream().map(m -> m.getStockSn().trim()).collect(Collectors.toList());
        List<AllocateStockBaseInfoImportResult> result = new ArrayList();
        //查询出的list转还map
        Map<String, StockBaseInfo> stringStockBaseInfoMap = stockBaseInfos.stream().collect(Collectors.toMap(StockBaseInfo::getStockSn, a -> a, (k1, k2) -> k1));

        //交集的补集
        Collection<String> errSn = CollectionUtils.disjunction(snList, stringStockBaseInfoMap.keySet().stream().collect(Collectors.toList()));
        //最终返回
        request.getDataList().forEach(f -> {
            StockBaseInfo stockBaseInfo = stringStockBaseInfoMap.get(f.getStockSn());
            if (Objects.nonNull(stockBaseInfo)) {
                AllocateStockBaseInfoImportResult allocateStockBaseInfoImportResult = AllocateConverter.INSTANCE.convertAllocateStockBaseInfoImportResult(stockBaseInfo);
                allocateStockBaseInfoImportResult.setToId(f.getToId());
                allocateStockBaseInfoImportResult.setToStoreId(f.getToStoreId());
                result.add(allocateStockBaseInfoImportResult);
            }
        });
        return ImportResult.<AllocateStockBaseInfoImportResult>builder()
                .successList(result)
                .errList(errSn.stream().collect(Collectors.toList()))
                .build();
    }

    @Override
    public ImportResult<BorrowStockBaseInfoImportResult> allocateStockImport2(BorrowStockImportRequest request, List<StockBaseInfo> stockBaseInfos) {
        //查询导入表身号
        List<String> snList = request.getDataList().stream().map(m -> m.getStockSn().trim()).collect(Collectors.toList());
        List<BorrowStockBaseInfoImportResult> result = new ArrayList();
        //查询出的list转还map
        Map<String, StockBaseInfo> stringStockBaseInfoMap = stockBaseInfos.stream().collect(Collectors.toMap(StockBaseInfo::getStockSn, a -> a, (k1, k2) -> k1));

        //交集的补集
        Collection<String> errSn = CollectionUtils.disjunction(snList, stringStockBaseInfoMap.keySet().stream().collect(Collectors.toList()));
        //最终返回
        request.getDataList().forEach(f -> {
            StockBaseInfo stockBaseInfo = stringStockBaseInfoMap.get(f.getStockSn());
            if (Objects.nonNull(stockBaseInfo)) {
                BorrowStockBaseInfoImportResult allocateStockBaseInfoImportResult = AllocateConverter.INSTANCE.convertBorrowStockBaseInfoImportResult(stockBaseInfo);
//                allocateStockBaseInfoImportResult.setToId(f.getToId());
//                allocateStockBaseInfoImportResult.setToStoreId(f.getToStoreId());
                result.add(allocateStockBaseInfoImportResult);
            }
        });
        return ImportResult.<BorrowStockBaseInfoImportResult>builder()
                .successList(result)
                .errList(errSn.stream().collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<AllocateToTimeoutResult> toTimeout(AllocateToTimeoutRequest request) {

        Assert.isTrue(Objects.nonNull(request.getTimeoutDay()) || StringUtils.isNotBlank(request.getTimeoutDate()), "没有指定超时参数");

        //进行中 门店通知 的单据
        List<BillAllocate> billAllocateList = billAllocateService.list(Wrappers.<BillAllocate>lambdaQuery()
                .eq(BillAllocate::getAllocateState, AllocateStateEnum.OUT_STOCK)
                .in(CollectionUtils.isNotEmpty(request.getStoreIdList()), BillAllocate::getToId, request.getStoreIdList())
        );

        /**
         * 门店名称
         */
        Map<Integer, StoreManagementInfo> storeManagementInfoMap = new HashMap<>();
        /**
         * 门店店长列表
         */
        Map<Integer, List<String>> shopManagerListMap = new HashMap<>();

        List<AllocateToTimeoutResult> result = new ArrayList<>();

        for (BillAllocate billAllocate : billAllocateList) {

            List<BillAllocateLine> billAllocateLineList = billAllocateLineService.list(Wrappers.<BillAllocateLine>query()
                    .lambda()
                    .eq(BillAllocateLine::getAllocateLineState, AllocateLineStateEnum.DELIVERED)
                    .eq(BillAllocateLine::getAllocateId, billAllocate.getId())
                    .and(StringUtils.isNotBlank(request.getTimeoutDate()) && Objects.nonNull(request.getTimeoutDay()),
                            i -> i.lt(BillAllocateLine::getUpdatedTime, DateUtil.parse(request.getTimeoutDate()))
                                    .or().apply(" (DATEDIFF(now(),updated_time)) > " + request.getTimeoutDay())
                    )
                    .le(StringUtils.isNotBlank(request.getTimeoutDate()), BillAllocateLine::getUpdatedTime, DateUtil.parse(request.getTimeoutDate()))
                    .and(Objects.nonNull(request.getTimeoutDay()), i -> i.apply(" (DATEDIFF(now(),updated_time)) > " + request.getTimeoutDay()))
            );

            StoreManagementInfo storeManagementInfo;

            List<String> shopManagerList;

            if (storeManagementInfoMap.containsKey(billAllocate.getToId())) {
                storeManagementInfo = storeManagementInfoMap.get(billAllocate.getToId());
            } else {
                storeManagementInfo = storeManagementService.selectInfoById(billAllocate.getToId());

                if (Objects.isNull(storeManagementInfo)) {
                    storeManagementInfo = new StoreManagementInfo();
                    storeManagementInfo.setName("查询不到此门店");
                }
                storeManagementInfoMap.put(billAllocate.getToId(), storeManagementInfo);
            }

            if (shopManagerListMap.containsKey(billAllocate.getToId())) {
                shopManagerList = shopManagerListMap.get(billAllocate.getToId());
            } else {
                shopManagerList = storeManagementMapper.listByShopManager(billAllocate.getToId(), request.getRoleId());

                if (CollectionUtils.isEmpty(shopManagerList)) {
                    shopManagerList = Lists.newArrayList("@all");
                }
                shopManagerListMap.put(billAllocate.getToId(), shopManagerList);
            }

            Map<Integer, StockExt> stockMap = Optional.ofNullable(billAllocateLineList.stream()
                            .map(BillAllocateLine::getStockId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()))
                    .map(stockService::selectByStockIdList)
                    .orElseGet(() -> new ArrayList<>())
                    .stream()
                    .collect(Collectors.toMap(StockExt::getStockId, Function.identity()));

            for (BillAllocateLine billAllocateLine : billAllocateLineList) {

                StockExt stock = stockMap.get(billAllocateLine.getStockId());
                result.add(AllocateToTimeoutResult.builder()
                        .serialNo(billAllocate.getSerialNo())
                        .storeName(storeManagementInfo.getName())
                        .brandName(Objects.nonNull(stock) ? stock.getBrandName() : "未知")
                        .seriesName(Objects.nonNull(stock) ? stock.getSeriesName() : "未知")
                        .model(Objects.nonNull(stock) ? stock.getModel() : "未知")
                        .stockSn(Objects.nonNull(stock) ? stock.getStockSn() : "未知")
                        .timeoutMsg(String.format("您已超过%s天未接收该表",
                                StringUtils.isNotBlank(request.getTimeoutDate()) ? DateUtil.betweenDay(DateUtil.parse(request.getTimeoutDate()), DateUtil.date(), true) :
                                        DateUtil.betweenDay(billAllocateLine.getUpdatedTime(), DateUtil.date(), true)))
                        .msgManList(shopManagerList)
                        .build());
            }
        }

        return result;
    }
}
