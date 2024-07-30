package com.seeease.flywheel.serve.goods.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.goods.entity.StockInfo;
import com.seeease.flywheel.goods.request.*;
import com.seeease.flywheel.goods.result.StockExceptionListResult;
import com.seeease.flywheel.goods.result.StockGoodQueryResult;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.dict.entity.DictData;
import com.seeease.flywheel.serve.dict.entity.StockDict;
import com.seeease.flywheel.serve.dict.mapper.DictDataMapper;
import com.seeease.flywheel.serve.dict.mapper.StockDictMapper;
import com.seeease.flywheel.serve.dict.service.DictDataService;
import com.seeease.flywheel.serve.goods.entity.*;
import com.seeease.flywheel.serve.goods.enums.*;
import com.seeease.flywheel.serve.goods.mapper.LogStockOptMapper;
import com.seeease.flywheel.serve.goods.mapper.ScrapStockMapper;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.SeriesService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.GpmConfig;
import com.seeease.flywheel.serve.maindata.enums.GpmConfigEnums;
import com.seeease.flywheel.serve.maindata.mapper.GpmConfigMapper;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseLineMapper;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderLineMapper;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.Tuple2;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.utils.StrFormatterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @description 针对表【stock(库存)】的数据库操作Service实现
 * @createDate 2023-01-10 14:36:05
 */
@Service
@Slf4j
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock>
        implements StockService {

    private static final List<Integer> IP_SHOP = Lists.newArrayList(1, 42, 19, 20, 21, 24, 25, 36, 37, 38, 39, 40);

    @Resource
    private GoodsWatchService goodsWatchService;

    @Resource
    private DictDataMapper dictDataMapper;

    @Resource
    private StockDictMapper stockDictMapper;
    @Resource
    private SeriesService seriesService;
    @Resource
    private BillPurchaseLineMapper purchaseLineMapper;

    @Resource
    private GpmConfigMapper gpmConfigMapper;

    @Resource
    private BillSaleOrderLineMapper billSaleOrderLineMapper;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private ScrapStockMapper scrapStockMapper;

    @Resource
    private LogStockOptMapper logStockOptMapper;

    @Override
    public List<Stock> findByStockSn(String sn) {
        return baseMapper.selectList(Wrappers.<Stock>lambdaQuery()
                .like(Stock::getSn, sn));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockStatus(List<Integer> stockIds, StockStatusEnum.TransitionEnum transitionEnum) {
        stockIds.stream()
                .filter(Objects::nonNull)
                .sorted()
                .forEach(item -> {
                    Stock stock = new Stock();
                    stock.setId(item);
                    stock.setTransitionStateEnum(transitionEnum);
                    UpdateByIdCheckState.update(baseMapper, stock);
                });
    }


    /**
     * @param stockIdList
     * @return
     */
    @Override
    public List<StockExt> selectByStockIdList(List<Integer> stockIdList) {
        if (CollectionUtils.isEmpty(stockIdList))
            return Collections.EMPTY_LIST;
        List<StockExt> stockExtList = baseMapper.selectByStockIdList(stockIdList);
        //拼接保卡时间
        stockExtList.forEach(t -> {
            String attachmentDetails = Lists.newArrayList(Optional.ofNullable(t.getAttachmentDetails()).map(a -> a.replaceAll(",", "/")).orElse(null)
                            , StockCardEnum.joinCard(t.getIsCard(), t.getWarrantyDate(), t.getSeriesType()))
                    .stream()
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("/"));
            t.setAttachmentDetails(attachmentDetails);
        });
        return stockExtList;
    }

    /**
     * @param request
     * @return
     */
    @Override
    public Page<StockBaseInfo> listStock(StockListRequest request) {
        return baseMapper.listStock(Page.of(request.getPage(), request.getLimit()), request);
    }

    /**
     * @param request
     * @return
     */
    @Override
    public List<StockBaseInfo> listStockByStockSnList(StockListRequest request) {
        return baseMapper.listStock(request);
    }


    @Override
    public Page<StockBaseInfo> listStockByApply(StockListRequest request) {
        return baseMapper.listStockByApply(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    public List<StockBaseInfo> listSaleableStockBySn(List<String> stockSnList, Integer rightOfManagement) {
        return baseMapper.listSaleableStockBySn(stockSnList, rightOfManagement);
    }

    @Override
    public int cleanDemandIdByIds(List<Integer> ids) {
        return baseMapper.cleanDemandIdByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUnderselling(List<Integer> stockIds, StockUndersellingEnum i) {
        stockIds.stream()
                .filter(Objects::nonNull)
                .sorted()
                .forEach(item -> {
                    Stock stock = new Stock();
                    stock.setId(item);
                    stock.setIsUnderselling(i);
                    baseMapper.updateById(stock);
                });
    }

    @Override
    public void cleanCkTimeById(List<Integer> ids) {
        baseMapper.cleanCkTimeByIds(ids);
    }

    @Override
    public PageResult<StockExceptionListResult> exceptionStock(StockExceptionListRequest request) {

        if (CollectionUtils.isNotEmpty(request.getBrandIdList())) {

            List<Integer> collect = goodsWatchService.list(Wrappers.<GoodsWatch>lambdaQuery()
                    .in(GoodsWatch::getBrandId, request.getBrandIdList())).stream().map(GoodsWatch::getId).collect(Collectors.toList());

            request.setGoodsIdList(CollectionUtils.isNotEmpty(collect) ? collect : null);
        }

        request.setStockStatus(Optional.ofNullable(request.getStockStatus())
                .filter(v -> v != -1)
                .orElse(null));

        request.setStockSrc(Optional.ofNullable(request.getStockSrc())
                .filter(v -> v != -1)
                .orElse(null));

        Page<StockExceptionListResult> page = baseMapper.exceptionStock(new Page<>(request.getPage(), request.getLimit()), request);

        List<StockExceptionListResult> records = page.getRecords();

        List<WatchDataFusion> fusionList = goodsWatchService.getWatchDataFusionListByGoodsIds(records.stream().map(StockExceptionListResult::getGoodsId).distinct().collect(Collectors.toList()));

        records.forEach(pricingListResult -> {
            WatchDataFusion watchDataFusion = fusionList.stream().filter(r -> r.getGoodsId().equals(pricingListResult.getGoodsId())).findAny().get();
            pricingListResult.setBrandName(watchDataFusion.getBrandName());
            pricingListResult.setSeriesName(watchDataFusion.getSeriesName());
            pricingListResult.setModel(watchDataFusion.getModel());
            pricingListResult.setImage(watchDataFusion.getImage());
            pricingListResult.setPricePub(watchDataFusion.getPricePub());
        });

        return PageResult.<StockExceptionListResult>builder()
                .result(records)
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void attachment(StockAttachmentRequest request) {

        Assert.notNull(request.getStockId(), "stockId不能为空");

        //原始信息
        Stock oldStock = baseMapper.selectById(request.getStockId());

        List<WatchDataFusion> oldWatchDataFusionList = goodsWatchService.getWatchDataFusionListByGoodsIds(Arrays.asList(oldStock.getGoodsId()));

        if (CollectionUtils.isEmpty(oldWatchDataFusionList)) {
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
        }

        WatchDataFusion oldWatchDataFusion = oldWatchDataFusionList.get(FlywheelConstant.INDEX);

        //插入日志
        LogStockOpt logStockOpt = new LogStockOpt();

        Stock stock = new Stock();
        stock.setId(request.getStockId());

        switch (request.getUseScenario()) {
            case UNUSUAL_DESC:
                stock.setUnusualDesc(request.getUnusualDesc());
                break;
            case STOCK:
                Assert.notNull(request.getBrandId(), "品牌不能为空");
                Assert.notNull(request.getSeriesId(), "系列不能为空");
                Assert.notNull(request.getGoodsId(), "型号不能为空");
                Assert.notEmpty(request.getStockSn(), "表身号不能为空");
                //stockSn 要修改的表身号 有无存在
                List<Stock> stockList = baseMapper.selectList(Wrappers.<Stock>lambdaQuery()
                        .eq(Stock::getSn, request.getStockSn().trim())
                        //排出不在库的情况
                        .notIn(Stock::getStockStatus, Arrays.asList(StockStatusEnum.SOLD_OUT, StockStatusEnum.CONSIGNMENT, StockStatusEnum.PURCHASE_RETURNED)));

                if (CollectionUtils.isNotEmpty(stockList) && (ObjectUtils.isEmpty(oldStock) || !stockList.get(FlywheelConstant.INDEX).getSn().equals(oldStock.getSn()))) {
                    throw new OperationRejectedException(OperationExceptionCode.STOCK_PARAMETER_EXISTS);
                }
                //校验
                List<WatchDataFusion> watchDataFusionList = goodsWatchService.getWatchDataFusionListByGoodsIds(Arrays.asList(request.getGoodsId()));

                if (CollectionUtils.isEmpty(watchDataFusionList)) {
                    throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
                }

                WatchDataFusion watchDataFusion = watchDataFusionList.get(FlywheelConstant.INDEX);

                if (StringUtils.join(Arrays.asList(request.getBrandId(), request.getSeriesId(), request.getGoodsId()), "-")
                        .equals(StringUtils.join(Arrays.asList(watchDataFusion.getBrandId(), watchDataFusion.getSeriesId(), watchDataFusion.getGoodsId()), "-"))) {
                } else {
                    throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
                }

                stock.setUnusualDesc(request.getUnusualDesc());
                stock.setStrap(request.getStrap());
                stock.setLevel(request.getLevel());
                stock.setSn(request.getStockSn());
                stock.setWeek(request.getWeek());
                stock.setWatchSection(ObjectUtils.isEmpty(request.getWatchSection()) ? StringUtils.EMPTY : request.getWatchSection());
                stock.setGoodsId(request.getGoodsId());

                if (Objects.nonNull(request.getFiness())
                        && !request.getFiness().equals(oldStock.getFiness())) {
                    //成色修改校验
                    stock.setFiness(request.getFiness());
                    if (Lists.newArrayList(StockStatusEnum.SOLD_OUT, StockStatusEnum.CONSIGNMENT).contains(oldStock.getStockStatus())) {
                        throw new OperationRejectedException(OperationExceptionCode.FINESS_PROHIBIT_MODIFICATION);
                    }
                }


                logStockOpt.setOptMode(3);
                logStockOpt.setStockId(request.getStockId());
                logStockOpt.setShopId(UserContext.getUser().getStore().getId());
                logStockOpt.setOpeningStockSn(oldStock.getSn());
                logStockOpt.setClosingStockSn(request.getStockSn());
                logStockOpt.setOpeningStockAttachment(oldStock.getAttachment());
                logStockOpt.setClosingStockAttachment(oldStock.getAttachment());
                logStockOpt.setOpeningStockOther(StrFormatterUtil.format("【品牌：{}】【系列：{}】【型号：{}】【表身号：{}】【附件：{}】【成色：{}】【经营类型：{}】【表带号：{}】【腕周：{}】【表节：{}】【异常原因：{}】",
                        oldWatchDataFusion.getBrandName(), oldWatchDataFusion.getSeriesName(), oldWatchDataFusion.getModel(),
                        oldStock.getSn(), oldStock.getAttachment(), oldStock.getFiness(), oldStock.getLevel(), oldStock.getStrap(),
                        oldStock.getWeek(), oldStock.getWatchSection(), oldStock.getUnusualDesc()));
                logStockOpt.setClosingStockOther(StrFormatterUtil.format("【品牌：{}】【系列：{}】【型号：{}】【表身号：{}】【附件：{}】【成色：{}】【经营类型：{}】【表带号：{}】【腕周：{}】【表节：{}】【异常原因：{}】",
                        watchDataFusion.getBrandName(), watchDataFusion.getSeriesName(), watchDataFusion.getModel(),
                        request.getStockSn(), oldStock.getAttachment(), request.getFiness(), request.getLevel(), request.getStrap(),
                        request.getWeek(), request.getWatchSection(), request.getUnusualDesc()));

                logStockOptMapper.insert(logStockOpt);

                break;
            case ATTACHMENT:

                Map<String, List<Integer>> attachmentMap = request.getAttachmentMap();

                List<DictData> dictDataList = dictDataMapper.selectList(Wrappers.<DictData>lambdaQuery().likeRight(DictData::getDictType, "stock_"));

                /**
                 * 附件列表
                 */
                List<Integer> convert = null;
                if (ObjectUtils.isNotEmpty(attachmentMap)) {
                    convert = convert(dictDataList, attachmentMap);
                }

                /**
                 * 非空判定
                 */
                if (CollectionUtils.isEmpty(convert) && (ObjectUtils.isEmpty(request.getIsCard()) || request.getIsCard().intValue() == 0)) {
                    throw new OperationRejectedException(OperationExceptionCode.ATTACHMENT_EXISTS);
                }

                //更新stock 字符串 is_card
                String attachment = convert(dictDataList, convert, ObjectUtils.isEmpty(request.getIsCard()) ? 0 : request.getIsCard(), request.getWarrantyDate(), request.getStockId());
                stock.setAttachment(attachment);
                stock.setIsCard(ObjectUtils.isEmpty(request.getIsCard()) ? 0 : request.getIsCard());
                stock.setWarrantyDate(request.getWarrantyDate());

                stockDictMapper.delete(Wrappers.<StockDict>lambdaQuery().eq(StockDict::getStockId, request.getStockId()));

                List<Integer> finalConvert = convert;
                purchaseLineMapper.selectList(Wrappers.<BillPurchaseLine>lambdaQuery()
                                .eq(BillPurchaseLine::getStockId, request.getStockId()))
                        .forEach(billPurchaseLine -> {
                            BillPurchaseLine line = new BillPurchaseLine();
                            line.setId(billPurchaseLine.getId());
                            line.setAttachmentList(finalConvert);
                            line.setIsCard(ObjectUtils.isEmpty(request.getIsCard()) ? 0 : request.getIsCard());
                            line.setWarrantyDate(request.getWarrantyDate());
                            if (StringUtils.isBlank(billPurchaseLine.getOldAttachment())) {
                                //附件字符串
                                String oldAttachment = convert(dictDataList, billPurchaseLine.getAttachmentList(),
                                        billPurchaseLine.getIsCard(), billPurchaseLine.getWarrantyDate(), request.getStockId());
                                line.setOldAttachment(oldAttachment);
                            }
                            purchaseLineMapper.updateById(line);
                        });
                if (CollectionUtils.isNotEmpty(convert)) {
                    stockDictMapper.insertBatchSomeColumn(convert.stream().map(item -> {
                        StockDict stockDict = new StockDict();
                        stockDict.setStockId(request.getStockId());
                        stockDict.setDictId(item.longValue());
                        return stockDict;
                    }).collect(Collectors.toList()));
                }


                //插入日志
                logStockOpt.setOptMode(2);
                logStockOpt.setStockId(request.getStockId());
                logStockOpt.setShopId(UserContext.getUser().getStore().getId());
                logStockOpt.setOpeningStockSn(oldStock.getSn());
                logStockOpt.setClosingStockSn(oldStock.getSn());
                logStockOpt.setOpeningStockAttachment(oldStock.getAttachment());
                logStockOpt.setClosingStockAttachment(attachment);
                logStockOpt.setOpeningStockOther(StrFormatterUtil.format("【品牌：{}】【系列：{}】【型号：{}】【表身号：{}】【附件：{}】【成色：{}】【经营类型：{}】【表带号：{}】【腕周：{}】【表节：{}】【异常原因：{}】",
                        oldWatchDataFusion.getBrandName(), oldWatchDataFusion.getSeriesName(), oldWatchDataFusion.getModel(),
                        oldStock.getSn(), oldStock.getAttachment(), oldStock.getFiness(), oldStock.getLevel(), oldStock.getStrap(),
                        oldStock.getWeek(), oldStock.getWatchSection(), oldStock.getUnusualDesc()));
                logStockOpt.setClosingStockOther(StrFormatterUtil.format("【品牌：{}】【系列：{}】【型号：{}】【表身号：{}】【附件：{}】【成色：{}】【经营类型：{}】【表带号：{}】【腕周：{}】【表节：{}】【异常原因：{}】",
                        oldWatchDataFusion.getBrandName(), oldWatchDataFusion.getSeriesName(), oldWatchDataFusion.getModel(),
                        oldStock.getSn(), attachment, oldStock.getFiness(), oldStock.getLevel(), oldStock.getStrap(),
                        oldStock.getWeek(), oldStock.getWatchSection(), oldStock.getUnusualDesc()));

                logStockOptMapper.insert(logStockOpt);
                break;
            default:
                return;
        }
        baseMapper.updateById(stock);
    }

    @Override
    public void recalculateConsignmentPrice(List<Integer> ids) {
        baseMapper.recalculateConsignmentPrice(ids);
    }

    @Override
    public int refreshStorageAge(List<Integer> stockIdList) {
        return baseMapper.refreshStorageAge(CollectionUtils.isNotEmpty(stockIdList) ? stockIdList : null);
    }

    @Override
    public List<Stock> getConsignmentPrice(List<Integer> stockIdList) {
        return baseMapper.getConsignmentPrice(stockIdList);
    }

    @Override
    public List<Stock> list(Integer storeId, String brand, String model) {
        return this.getBaseMapper().list(storeId, brand, model);
    }

    @Override
    public Page<StockInfo> listByRequest(StockInfoListRequest request) {
        return this.baseMapper.listByRequest(Page.of(request.getPage(), request.getLimit()), request);
    }

    private List<Integer> convert(List<DictData> dataList, Map<String, List<Integer>> map) {
        if (map.isEmpty()) {
            return Arrays.asList();
        }
        List<Integer> list = new ArrayList<Integer>();
        Map<String, List<DictData>> collect = dataList.stream().collect(Collectors.groupingBy(DictData::getDictType));
        map.forEach((k, v) -> {
            List<DictData> dictDataList = collect.get(k);
            if (ObjectUtils.isNotEmpty(v)) {
                v.forEach(item -> {
                    DictData data = dictDataList.stream().filter(dictData -> (item == Integer.parseInt(dictData.getDictValue()))).findAny().orElse(null);
                    if (ObjectUtils.isNotEmpty(data)) {
                        list.add(data.getDictCode().intValue());
                    }
                });
            }
        });
        return list;
    }

    private String convert(List<DictData> dataList, List<Integer> itemList, Integer isCard, String warrantyDate, Integer stockId) {
        String card = "空白保卡";
        String cardDate = "保卡(date)";
        if (Objects.nonNull(stockId)) {
            Integer seriesType = seriesService.getSeriesTypeByStockId(stockId);
            if (Objects.nonNull(seriesType) && SeriesTypeEnum.BAGS.getValue().equals(seriesType)) {
                card = "空白身份卡";
                cardDate = "身份卡(date)";
            }
        }
        String join = ObjectUtils.isEmpty(isCard) ? StringUtils.EMPTY : (isCard.equals(1) ? StringUtils.replace(cardDate, "date", warrantyDate) : isCard.equals(0) ? StringUtils.EMPTY : card);

        String attachment = StringUtils.EMPTY;

        if (CollectionUtils.isNotEmpty(dataList)) {

            List<String> collect = null;
            if (CollectionUtils.isNotEmpty(itemList)) {
                collect = itemList.stream().flatMap(item -> dataList.stream().filter(dictData -> item.intValue() == (dictData.getDictCode().intValue()))).map(dictData -> dictData.getDictLabel()).collect(Collectors.toList());
            }
            attachment = CollectionUtils.isEmpty(itemList) ? StringUtils.EMPTY + (ObjectUtils.isEmpty(join) ? StringUtils.EMPTY : join) : StringUtils.join(collect, "/") + (ObjectUtils.isEmpty(join) ? StringUtils.EMPTY : "/" + join);

        } else {
            return join;
        }

        return attachment;
    }

    @Override
    public Page<StockGoodQueryResult> modelStockFoldList(StockGoodQueryRequest request) {
        request.setVisibleShop(!IP_SHOP.contains(UserContext.getUser().getStore().getId().intValue()));
        Page<StockGoodQueryResult> stockGoodQueryResultPage = this.baseMapper.modelStockFoldList(Page.of(request.getPage(), request.getLimit()), request);
        List<StockGoodQueryResult> records = stockGoodQueryResultPage.getRecords();
        List<StockGoodQueryResult> resultList = Lists.newLinkedList();
        if (CollectionUtil.isNotEmpty(records)) {
            request.setGroupingGoodsId(Boolean.FALSE);
            records.forEach(f -> {
                request.setGoodsId(f.getGoodsId());
                request.setId(f.getId());
                List<StockGoodQueryResult> stockGoodQueryResults = this.baseMapper.modelStockFoldList(request);
                //设置商品总库存,本身默认为1
                f.setStockCount(stockGoodQueryResults.size() + 1);
                resultList.add(f);
                if (CollectionUtil.isNotEmpty(stockGoodQueryResults)) {
                    stockGoodQueryResults.forEach(v -> {
                        v.setParentId(f.getId());
                        resultList.add(v);
                    });
                }
            });
        }

        //ToB,ToC毛利率配置
        Tuple2<GpmConfig /*ToB*/, GpmConfig/*ToC*/> bcGpmConfig = this.getBCGpmConfig();
        //成交价，在库存列表不需要成交价 可优化 ,在库存列表场景空成交价
        Map<Integer/*stockId*/, List<BillSaleOrderLine>> cacheSnapshot =
                request.getQueryBatchBorrowing() ? Maps.newHashMap() :
                        this.getBatchSaleSnapshot(records.stream().map(StockGoodQueryResult::getId).distinct().collect(Collectors.toList()));
        //组装数据
        if (CollectionUtil.isNotEmpty(resultList)) {
            resultList.forEach(item -> {
                if (Objects.isNull(item.getPromotionPrice())) {
                    this.setActualPerformance(item, bcGpmConfig);
                } else {
                    item.setTobActualPerformance(item.getPromotionPrice().toString());
                    item.setTocActualPerformance(item.getPromotionPrice().toString());
                }
                this.setSaleMoney(item, cacheSnapshot);
                //计算销售时间成本
                this.strategySuggestion(item);
                item.setStorePrice(null);//门店采购价设置为空

                Map job = (Map) dictDataService.dictData(item.getId(), item.getIsCard(), item.getWarrantyDate());

                item.setDictChildList(job.get("dictChildList"));
                item.setAttachmentLabel(job.get("attachmentLabel"));
            });
            stockGoodQueryResultPage.setRecords(resultList);
        }

        return stockGoodQueryResultPage;
    }

    @Override
    public Page<StockBaseInfo> listStockBySettlement(StockListRequest request) {
        return baseMapper.listStockBySettlement(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    public Page<StockBaseInfo> listStockByInvoice(StockListRequest request) {
        return baseMapper.listStockByInvoice(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int inExceptionStock(List<Integer> ids) {

        List<Stock> stocks = this.baseMapper.selectBatchIds(ids);
        if (CollectionUtil.isNotEmpty(ids) && CollectionUtils.isNotEmpty(stocks)) {
            Map<Integer, Stock> stockMap = stocks.stream().collect(Collectors.toMap(Stock::getId, a -> a, (k1, k2) -> k1));
            ids.forEach(f -> {
                updateStockStaus(f, stockMap);
            });
        }
        return ids.size();
    }

    public void updateStockStaus(Integer id, Map<Integer, Stock> stockMap) {

        Stock stock = stockMap.get(id);
        if (Objects.nonNull(stock)) {
            Stock stockUpdate = new Stock();
            stockUpdate.setId(id);
            stockUpdate.setTransitionStateEnum(StockStatusEnum.TransitionEnum.STOCK_IN_EXCEPTION);
            if (stock.getStockStatus() == StockStatusEnum.WAIT_PRICING) {
                stockUpdate.setTransitionStateEnum(StockStatusEnum.TransitionEnum.STOCK_IN_EXCEPTION_WAIT_PRICING);
            }
            UpdateByIdCheckState.update(baseMapper, stockUpdate);
        }

    }

    @Override
    public Page<StockGoodQueryResult> queryStockPage(StockGoodQueryRequest request) {
        request.setVisibleShop(!IP_SHOP.contains(UserContext.getUser().getStore().getId()));
        Page<StockGoodQueryResult> stockGoodQueryResultPage = this.baseMapper.modelStockFoldList(Page.of(request.getPage(), request.getLimit()), request);
        List<StockGoodQueryResult> records = stockGoodQueryResultPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return stockGoodQueryResultPage;
        }

        //ToB,ToC毛利率配置
        Tuple2<GpmConfig /*ToB*/, GpmConfig/*ToC*/> bcGpmConfig = this.getBCGpmConfig();
        //成交价，在库存列表不需要成交价 可优化 ,在库存列表场景空成交价
        Map<Integer/*stockId*/, List<BillSaleOrderLine>> cacheSnapshot =
                request.getQueryBatchBorrowing() ? Maps.newHashMap() :
                        this.getBatchSaleSnapshot(records.stream().map(StockGoodQueryResult::getId).distinct().collect(Collectors.toList()));
        //组装数据
        records.forEach(item -> {
            if (Objects.isNull(item.getPromotionPrice())) {
                this.setActualPerformance(item, bcGpmConfig);
            } else {
                item.setTobActualPerformance(item.getPromotionPrice().toString());
                item.setTocActualPerformance(item.getPromotionPrice().toString());
            }
            this.setSaleMoney(item, cacheSnapshot);
            //计算销售时间成本
            this.strategySuggestion(item);
            item.setStorePrice(null);//门店采购价设置为空

            Map job = (Map) dictDataService.dictData(item.getId(), item.getIsCard(), item.getWarrantyDate());

            item.setDictChildList(job.get("dictChildList"));
            item.setAttachmentLabel(job.get("attachmentLabel"));
        });

        return stockGoodQueryResultPage;
    }

    @Override
    public long queryUnallocatedGoodsCount() {
        return this.baseMapper.queryUnallocatedGoodsCount();
    }

    @Override
    public Integer selectWhetherProtectById(Integer stockId) {
        return this.baseMapper.selectWhetherProtectById(stockId);
    }

    /**
     * 获取当前ToB,ToC毛利率配置
     *
     * @return
     */
    Tuple2<GpmConfig /*ToB*/, GpmConfig/*ToC*/> getBCGpmConfig() {
        GpmConfig tobGpmConfig = gpmConfigMapper.selectGpmConfigByCreateTime(new Date(), GpmConfigEnums.ToTarget.ToB.name());
        GpmConfig tocGpmConfig = gpmConfigMapper.selectGpmConfigByCreateTime(new Date(), GpmConfigEnums.ToTarget.ToC.name());
        return Tuple2.of(tobGpmConfig, tocGpmConfig);
    }

    /**
     * 根据stockId获取成交价
     *
     * @param stockIds
     * @return
     */
    Map<Integer/*stockId*/, List<BillSaleOrderLine>> getBatchSaleSnapshot(List<Integer> stockIds) {
        return Lists.partition(stockIds, 200)
                .stream()
                .map(ids -> billSaleOrderLineMapper.listByStockIds(ids))
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(BillSaleOrderLine::getStockId));
    }

    /**
     * gmv设置
     */
    private void setActualPerformance(StockGoodQueryResult stockGoodQueryVo, Tuple2<GpmConfig /*ToB*/, GpmConfig/*ToC*/> bcGpmConfig) {
        try {
            GpmConfig tobGpmConfig = bcGpmConfig.getV1();
            GpmConfig tocGpmConfig = bcGpmConfig.getV2();

            BigDecimal tocActualPerformance = BigDecimal.ZERO;
            BigDecimal tobActualPerformance = BigDecimal.ZERO;
            stockGoodQueryVo.convert();
            if (Objects.nonNull(tocGpmConfig) && Objects.nonNull(tobGpmConfig) && BigDecimal.ZERO.compareTo(stockGoodQueryVo.getTocPrice()) != 0 && BigDecimal.ZERO.compareTo(stockGoodQueryVo.getTobPrice()) != 0) {
                if (stockGoodQueryVo.getLocationId() == 1) {//总部
                    tocActualPerformance = (stockGoodQueryVo.getTocPrice().subtract(stockGoodQueryVo.getTotalPrice())).divide(tocGpmConfig.getGpmTarget(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                    tobActualPerformance = (stockGoodQueryVo.getTobPrice().subtract(stockGoodQueryVo.getTotalPrice())).divide(tobGpmConfig.getGpmTarget(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                } else {//门店
                    tocActualPerformance = (stockGoodQueryVo.getTocPrice().subtract(Optional.ofNullable(stockGoodQueryVo.getConsignmentPrice()).orElse(stockGoodQueryVo.getStorePrice()))).divide(tocGpmConfig.getGpmTarget(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                    tobActualPerformance = (stockGoodQueryVo.getTobPrice().subtract(Optional.ofNullable(stockGoodQueryVo.getConsignmentPrice()).orElse(stockGoodQueryVo.getStorePrice()))).divide(tobGpmConfig.getGpmTarget(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                }
            }
            stockGoodQueryVo.setTobActualPerformance(tobActualPerformance.toString());
            stockGoodQueryVo.setTocActualPerformance(tocActualPerformance.toString());
        } catch (Exception e) {
            log.error("gmv设置异常,{}", e.getMessage(), e);
        }
    }

    /**
     * 设置销售价格
     */
    private void setSaleMoney(StockGoodQueryResult stockGoodQueryVo, Map<Integer/*stockId*/, List<BillSaleOrderLine>> cacheSnapshot) {
        try {
            if (Objects.isNull(stockGoodQueryVo) || Objects.isNull(stockGoodQueryVo.getId())
                    || MapUtils.isEmpty(cacheSnapshot) || !cacheSnapshot.containsKey(stockGoodQueryVo.getId())) {
                return;
            }

            List<BillSaleOrderLine> batchSaleSnapshots = cacheSnapshot.get(stockGoodQueryVo.getId());
            if (org.springframework.util.CollectionUtils.isEmpty(batchSaleSnapshots)) {
                return;
            }
            List<BillSaleOrderLine> batchSaleSnapshotList = batchSaleSnapshots.stream().sorted((item1, item2) -> item2.getId().compareTo(item1.getId())).collect(Collectors.toList());
            if (org.springframework.util.CollectionUtils.isEmpty(batchSaleSnapshotList)) {
                return;
            }
            BillSaleOrderLine batchSaleSnapshot = batchSaleSnapshotList.stream().findFirst().get();
            stockGoodQueryVo.setClinchPrice(batchSaleSnapshot.getClinchPrice());
        } catch (Exception e) {
            log.error("设置销售价格异常,{}", e.getMessage(), e);
        }
    }

    /**
     * 计算销售时间成本
     *
     * @param stockGoodQueryVo Desc:
     *                         举例说明：
     *                         某商品：成本价9000，B价9500，C价11500
     *                         按资金成本30天1%算，得出当前的 【资产每日成本】= 成本价*1%/30 = 9000 * 1%/30 = 3
     *                         当【总库龄】= （B价 - 成本价）*50% /【资产每日成本】= （9500 - 9000）*50% / 3 = 83天时，需要出流转策略：
     */
    private void strategySuggestion(StockGoodQueryResult stockGoodQueryVo) {
        try {
            if (StringUtils.isEmpty(stockGoodQueryVo.getTotalStorageAge())
                    || !StringUtils.isNumeric(stockGoodQueryVo.getTotalStorageAge())
                    || org.springframework.util.ObjectUtils.isEmpty(stockGoodQueryVo.getTotalPrice())
                    || org.springframework.util.ObjectUtils.isEmpty(stockGoodQueryVo.getTobPrice())) {
                return;
            }
            BigDecimal totalStorageAge = new BigDecimal(stockGoodQueryVo.getTotalStorageAge());//总库龄
            BigDecimal totalPrice = stockGoodQueryVo.getTotalPrice();
            BigDecimal tobPrice = stockGoodQueryVo.getTobPrice();
            //默认推荐门店销售
            stockGoodQueryVo.setStrategySuggestion(StrategySuggestionEnums.SHOP_SUGGESTION.getName());
            if (totalPrice.compareTo(BigDecimal.ZERO) == 0 || tobPrice.compareTo(BigDecimal.ZERO) == 0) {
                return;
            }
            //资产每日成本
            BigDecimal daydivide = totalPrice.multiply(new BigDecimal(0.01)).divide(new BigDecimal(30), 2, RoundingMode.HALF_UP);
            //时间成本四舍五入 当总库龄大于等于商品的利息，流转到推荐商家销售 B价
            BigDecimal divide = tobPrice.subtract(totalPrice).multiply(new BigDecimal(0.5)).divide(daydivide, 2, RoundingMode.HALF_UP).setScale(0, BigDecimal.ROUND_HALF_UP);
            //当总库龄大于利息的时候；需要推荐商家销售totalStorageAge
            if (totalStorageAge.compareTo(divide) > -1) {
                stockGoodQueryVo.setStrategySuggestion(StrategySuggestionEnums.MERCHANT_SUGGESTION.getName());
            }
        } catch (Exception e) {
            log.error("计算销售时间成本异常,{}", e.getMessage(), e);
        }
    }

    @Override
    public List<StockQuantityDTO> countAllocateStockQuantity(List<Integer> goodsIdList, Integer rightOfManagement) {
        return baseMapper.countAllocateStockQuantity(goodsIdList, rightOfManagement);
    }


    @Override
    public void removeUnusualDesc(List<Integer> stockIdList) {
        baseMapper.removeUnusualDesc(stockIdList);
    }
}




