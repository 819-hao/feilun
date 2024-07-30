package com.seeease.flywheel.serve.goods.rpc;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.*;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.common.biz.buyBackPolicy.BuyBackPolicyBO;
import com.seeease.flywheel.goods.IStockFacade;
import com.seeease.flywheel.goods.entity.GoodsBaseInfo;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.goods.entity.StockInfo;
import com.seeease.flywheel.goods.entity.StockMarketsInfo;
import com.seeease.flywheel.goods.request.*;
import com.seeease.flywheel.goods.request.StockExt1;
import com.seeease.flywheel.goods.result.*;
import com.seeease.flywheel.pricing.request.ModelPriceChangeImportRequest;
import com.seeease.flywheel.pricing.result.ModelPriceChangeImportResult;
import com.seeease.flywheel.purchase.request.PurchaseByNameRequest;
import com.seeease.flywheel.purchase.result.PurchaseByNameResult;
import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import com.seeease.flywheel.serve.base.*;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.entity.AuditLoggingDetail;
import com.seeease.flywheel.serve.financial.entity.FinancialDocuments;
import com.seeease.flywheel.serve.financial.entity.FinancialDocumentsDetail;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.financial.service.AuditLoggingDetailService;
import com.seeease.flywheel.serve.financial.service.FinancialDocumentsDetailService;
import com.seeease.flywheel.serve.financial.service.FinancialDocumentsService;
import com.seeease.flywheel.serve.fix.enums.FlowGradeEnum;
import com.seeease.flywheel.serve.goods.convert.StockConverter;
import com.seeease.flywheel.serve.goods.entity.*;
import com.seeease.flywheel.serve.goods.enums.SeriesTypeEnum;
import com.seeease.flywheel.serve.goods.enums.StockPromotionEnum;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.mapper.GoodsMetaInfoSyncMapper;
import com.seeease.flywheel.serve.goods.service.*;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.entity.StoreManagementInfo;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.StoreRelationshipSubjectService;
import com.seeease.flywheel.serve.pricing.entity.BillPricing;
import com.seeease.flywheel.serve.pricing.service.BillPricingService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.utils.BigDecimalUtil;
import com.seeease.springframework.utils.StrFormatterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Slf4j
@DubboService(version = "1.0.0")
public class StockFacade implements IStockFacade {

    private static final Set<StockListRequest.UseScenario> SCOPE_BUSINESS = ImmutableSet.of(StockListRequest.UseScenario.PURCHASE_RETURN, StockListRequest.UseScenario.PURCHASE_RETURN_STOCK);
    private static final Set<StockListRequest.UseScenario> SALE = ImmutableSet.of(StockListRequest.UseScenario.SALE);
    private static final List<Integer> SALE_APPLY = ImmutableList.of(
            StockStatusEnum.MARKETABLE.getValue(),
            StockStatusEnum.CONSIGNMENT.getValue(),
            StockStatusEnum.WAIT_PRICING.getValue(),
            StockStatusEnum.ALLOCATE_IN_TRANSIT.getValue(),
            StockStatusEnum.PURCHASE_IN_TRANSIT.getValue(),
            StockStatusEnum.WAIT_RECEIVED.getValue(),
            StockStatusEnum.ON_LOAN.getValue()
    );
    private static List<Integer> PURCHASE_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.TH_CG_DJ.getValue(),
            BusinessBillTypeEnum.TH_CG_BH.getValue(),
            BusinessBillTypeEnum.TH_CG_PL.getValue(),
            BusinessBillTypeEnum.TH_JS.getValue(),
            BusinessBillTypeEnum.GR_JS.getValue(),
            BusinessBillTypeEnum.GR_HS_JHS.getValue(),
            BusinessBillTypeEnum.GR_HS_ZH.getValue(),
            BusinessBillTypeEnum.GR_HG_ZH.getValue(),
            BusinessBillTypeEnum.GR_HG_JHS.getValue(),
            BusinessBillTypeEnum.TH_CG_QK.getValue(),
            BusinessBillTypeEnum.TH_CG_DJTP.getValue()
    );

    private static final ImmutableRangeMap<Comparable<BigDecimal>, BigDecimal> MAP = ImmutableRangeMap.<Comparable<BigDecimal>, BigDecimal>builder()
            .put(Range.lessThan(BigDecimal.valueOf(10000L)), BigDecimal.valueOf(300L))
            .put(Range.closedOpen(BigDecimal.valueOf(10000L), BigDecimal.valueOf(30000L)), BigDecimal.valueOf(800L))
            .put(Range.closedOpen(BigDecimal.valueOf(30000L), BigDecimal.valueOf(60000L)), BigDecimal.valueOf(1200L))
            .put(Range.closedOpen(BigDecimal.valueOf(60000L), BigDecimal.valueOf(100000L)), BigDecimal.valueOf(2000L))
            .put(Range.atLeast(BigDecimal.valueOf(100000L)), BigDecimal.valueOf(3000L))
            .build();

    @Resource
    private StoreManagementService storeManagementService;

    @Resource
    private PurchaseSubjectService purchaseSubjectService;

    @Resource
    private StoreRelationshipSubjectService storeRelationshipSubjectService;

    @Resource
    private StockService stockService;

    @Resource
    private GoodsWatchService goodsWatchService;

    @Resource
    private BillPurchaseService billPurchaseService;
    @Resource
    private BillPurchaseLineService billPurchaseLineService;
    @Resource
    private BillSaleOrderLineService billSaleOrderLineService;
    @Resource
    private FinancialDocumentsDetailService documentsDetailService;
    @Resource
    private FinancialDocumentsService documentsService;
    @Resource
    private AccountsPayableAccountingService accountingService;
    @Resource
    private AuditLoggingDetailService loggingDetailService;
    @Resource
    private BuyBackPolicyService buyBackPolicyService;

    @Resource
    private StockPromotionService stockPromotionService;

    @Resource
    private PhotoGalleryService photoGalleryService;

    @Resource
    private GoodsMetaInfoSyncMapper goodsMetaInfoSyncMapper;

    @Resource
    private StockGuaranteeCardManageService cardManageService;
    @Resource
    private ModelLiveScriptService modelLiveScriptService;

    @Resource
    private LogStockOptService logStockOptService;

    @Override
    public PageResult<StockInfo> selectStockList(StockInfoListRequest request) {

        Page<StockInfo> page = stockService.listByRequest(request);
        return PageResult.<StockInfo>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public List<StockExt1> selectByStockIdList(List<Integer> stockIdList) {
       return stockService.selectByStockIdList(stockIdList).stream()
               .map(StockConverter.INSTANCE::to)
               .collect(Collectors.toList());

    }

    @Override
    public PageResult<StockBaseInfo> listStock(StockListRequest request) {

        StockListRequest.UseScenario requestUseScenario = request.getUseScenario();

        Page<StockBaseInfo> stockInfoPage = new Page<>();
        switch (requestUseScenario) {

            case PURCHASE_RETURN:
                request.setStockIdList(null);
                Assert.notNull(request.getCustomerId(), "供应商不能为空");
                stockInfoPage = billPurchaseService.listByReturn(request);
                break;
            case PURCHASE_RETURN_STOCK:
                Assert.isTrue(CollectionUtils.isNotEmpty(request.getStockIdList()), "表身号id不能为空");
                request.setPage(1);
                request.setLimit(99999);
                request.setPurchaseType(null);
                request.setSerialNo(null);
                request.setCustomerId(null);
                request.setStockSn(null);
                stockInfoPage = billPurchaseService.listByReturn(request);
                break;
            case ALLOCATE:
                //调拨只能选择位置在当前门店
                request.setLocationId(UserContext.getUser().getStore().getId());
                //自动选择
                boolean autoSelect = CollectionUtils.isNotEmpty(request.getAutoSelect());
                if (autoSelect) {
                    List<StockBaseInfo> res = request.getAutoSelect().stream()
                            .map(t -> {
                                request.setGoodsIdList(Lists.newArrayList(t.getGoodsId()));
                                request.setLimit(t.getQuantity());
                                request.setSingle(t.getSingle());
                                request.setColour(StringUtils.defaultString(t.getColour()));
                                request.setSize(StringUtils.defaultString(t.getSize()));
                                request.setMaterial(StringUtils.defaultString(t.getMaterial()));
                                request.setGwModel(StringUtils.defaultString(t.getGwModel()));

                                Page<StockBaseInfo> resData = stockService.listStock(request);

                                if (CollectionUtils.isEmpty(resData.getRecords())
                                        || resData.getRecords().size() < t.getQuantity()) {
                                    throw new OperationRejectedException(OperationExceptionCode.MODEL_QUANTITY_INSUFFICIENT, t.getModel());
                                }
                                return resData.getRecords();
                            })
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());

                    stockInfoPage.setRecords(res);
                    stockInfoPage.setTotal(res.size());
                    stockInfoPage.setPages(NumberUtils.INTEGER_ONE);

                } else {
                    stockInfoPage = stockService.listStock(request);
                }

                if (CollectionUtils.isNotEmpty(stockInfoPage.getRecords())) {
                    Map<Integer, StockGuaranteeCardManage> manageMap = cardManageService.list(Wrappers.<StockGuaranteeCardManage>lambdaQuery()
                                    .eq(StockGuaranteeCardManage::getAllocateState, WhetherEnum.NO.getValue())
                                    .in(StockGuaranteeCardManage::getStockId, stockInfoPage.getRecords().stream()
                                            .map(StockBaseInfo::getStockId).collect(Collectors.toList())))
                            .stream()
                            .collect(Collectors.toMap(StockGuaranteeCardManage::getStockId, Function.identity()));
                    stockInfoPage.getRecords().forEach(t -> {
                        t.setGuaranteeCardManage(manageMap.containsKey(t.getStockId()) ? WhetherEnum.YES.getValue() : WhetherEnum.NO.getValue());
                    });
                }

                break;

            case ALLOCATE_TASK:
                //只显示总库龄大于60天且渠道库龄大于45天的商品
                if (UserContext.getUser().getStore().getId() != FlywheelConstant._ZB_ID) {
//                    request.setStartStorageAge(45);
//                    request.setEndStorageAge(99999);
//                    request.setStartTotalStorageAge(60);
//                    request.setEndTotalStorageAge(99999);
                }

                //调拨任务排除总部商品
                request.setExcludedLocationId(FlywheelConstant._ZB_ID);
                stockInfoPage = stockService.listStock(request);
                break;
            case SALE:
                request.setSalesPriority(null);
                //过滤三号楼天梭，美度
                request.setFilterSpecificBrand(StringUtils.isBlank(request.getStockSn()) && StringUtils.isBlank(request.getWno()));
                stockInfoPage = stockService.listStock(request);
                break;
            case MONEY_SALE:

                if (ObjectUtils.isEmpty(request.getStockStatus()) || request.getStockStatus().intValue() == -1) {
                    request.setStockStatusList(SALE_APPLY);
                } else {
                    com.baomidou.mybatisplus.core.toolkit.Assert.isTrue(SALE_APPLY.contains(request.getStockStatus()), "类型参数不符合");
                    request.setStockStatusList(Arrays.asList(request.getStockStatus()));
                }

                stockInfoPage = stockService.listStockByApply(request);
                break;
            //别再删别人代码了 合并的时候注意点
            case BATCH_CONSIGNMENT_SETTLEMENT:
                Assert.notNull(request.getCustomerId(), "供应商不能为空");
                request.setStockStatus(null);
                request.setStockSrc(BusinessBillTypeEnum.TH_JS.getValue());
                stockInfoPage = stockService.listStockBySettlement(request);
                break;
            case APPLY_FINANCIAL_INVOICE:
                //销售单中 行状态是 寄售中 的 商品状态是已寄售
                Assert.notNull(request.getCustomerId(), "供应商不能为空");
                request.setStockStatus(StockStatusEnum.SOLD_OUT.getValue());
                stockInfoPage = stockService.listStockByInvoice(request);
                break;
            case GROUP_CONSIGNMENT_SETTLEMENT:
                request.setStockStatus(null);
                request.setStockSrc(BusinessBillTypeEnum.TH_CG_PL.getValue());
                stockInfoPage = stockService.listStockBySettlement(request);
                break;
        }


        List<StockBaseInfo> result = stockInfoPage.getRecords();
        System.out.println("=============11111111" + JSONObject.toJSONString(result));
        listStockResult(result, requestUseScenario);


        return PageResult.<StockBaseInfo>builder()
                .result(result)
                .totalCount(stockInfoPage.getTotal())
                .totalPage(stockInfoPage.getPages())
                .build();
    }

    public void listStockResult(List<StockBaseInfo> result, StockListRequest.UseScenario requestUseScenario) {
        if (CollectionUtils.isNotEmpty(result)) {

            List<Integer> collect = result
                    .stream()
                    .map(StockBaseInfo::getLocationId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Integer, String> shopMap = new HashMap<>();
            //商品位置
            if (CollectionUtils.isNotEmpty(collect)) {
                shopMap = storeManagementService.selectInfoByIds(collect)
                        .stream()
                        .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName));
            }


            //经营权 商品归属
            Map<Integer, String> purchaseSubjectMap = purchaseSubjectService.list()
                    .stream()
                    .collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));


            Map<Integer, WatchDataFusion> watchDataFusionMap = goodsWatchService.getWatchDataFusionListByStockIds(
                            result.stream().map(StockBaseInfo::getStockId).collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(WatchDataFusion::getStockId, watchDataFusion -> watchDataFusion));

            Map<Integer, StockPromotion> promotionMap = new HashMap<>();
            if (SALE.contains(requestUseScenario)) {
                Date nowDate = DateUtils.getNowDate();
                promotionMap.putAll(stockPromotionService.list(new LambdaQueryWrapper<StockPromotion>()
                                .in(StockPromotion::getStockId, result.stream().map(StockBaseInfo::getStockId).collect(Collectors.toList()))
                                .eq(StockPromotion::getStatus, StockPromotionEnum.ITEM_UP_SHELF)
                                .le(StockPromotion::getStartTime, nowDate)
                                .ge(StockPromotion::getEndTime, nowDate)
                                .eq(StockPromotion::getDeleted, WhetherEnum.NO.getValue()))
                        .stream().collect(Collectors.toMap(StockPromotion::getStockId, Function.identity())));
            }

            Map<Integer, String> finalShopMap = shopMap;
            result.forEach(t -> {
                t.setLocationName(finalShopMap.get(t.getLocationId()));
                t.setRightOfManagementName(purchaseSubjectMap.get(t.getRightOfManagement()));
                t.setBelongName(purchaseSubjectMap.get(t.getBelongId()));


                WatchDataFusion watchDataFusion = watchDataFusionMap.get(t.getStockId());
                if (Objects.nonNull(watchDataFusion)) {
                    t.setBrandName(watchDataFusion.getBrandName());
                    t.setSeriesName(watchDataFusion.getSeriesName());
                    t.setModel(watchDataFusion.getModel());
                    t.setPricePub(watchDataFusion.getPricePub());
                    t.setSex(watchDataFusion.getSex());
                }
                //采购查询特有的参数
                if (Objects.nonNull(requestUseScenario) && SCOPE_BUSINESS.contains(requestUseScenario)) {
                    Map<Integer, String> map = purchaseSubjectService.list(Wrappers.<PurchaseSubject>lambdaQuery().in(PurchaseSubject::getId, result.stream().
                                    collect(Collectors.groupingBy(StockBaseInfo::getPurchaseSubjectId)).keySet().stream().collect(Collectors.toList()))).stream().
                            collect(Collectors.toMap(PurchaseSubject::getId, purchaseSubject -> purchaseSubject.getName()));

                    t.setPurchaseSubjectName(map.get(t.getPurchaseSubjectId()));
                }
                if (SALE.contains(requestUseScenario)) {

                    // 补充回购政策
                    this.supplyBuyBackPolicy(t);
                    StockPromotion stockPromotion = promotionMap.getOrDefault(t.getStockId(), null);
                    //填充 活动价格
                    if (Objects.nonNull(stockPromotion)) {
                        t.setPromotionConsignmentPrice(stockPromotion.getPromotionConsignmentPrice());
                    }
                } else if (StockListRequest.UseScenario.APPLY_FINANCIAL_INVOICE.equals(requestUseScenario)) {
                    Map<Integer, String> subjectUrlMap = FlywheelConstant.SUBJECT_URL_MAP;
                    if (subjectUrlMap.containsKey(t.getBelongId())) {
                        t.setSubjectUrl(subjectUrlMap.get(t.getBelongId()));
                    }
                }
            });
        }
    }

    @Override
    public List<StockBaseInfo> queryByStockSn(StockQueryRequest request) {
        return stockService.list(Wrappers.<Stock>lambdaQuery()
                        .in(null != request.getStockIds(),Stock::getId,request.getStockIds())
                        .in(null != request.getStockSnList(),Stock::getSn, request.getStockSnList())
                        .eq(request.isSaleable(), Stock::getStockStatus, StockStatusEnum.MARKETABLE))
                .stream()
                .map(StockConverter.INSTANCE::convertStockBaseInfo)
                .collect(Collectors.toList());
    }

    @Override
    public StockBaseInfo getById(Integer stockId) {
        Stock stock = stockService.getById(stockId);
        if (Objects.isNull(stock)) {
            throw new BusinessException(ExceptionCode.GOODS_NOT_SUPPORT);
        }
        StockBaseInfo result = StockConverter.INSTANCE.convertStockBaseInfo(stock);

        Optional.ofNullable(goodsWatchService.getWatchDataFusionListByStockIds(Lists.newArrayList(stockId)))
                .map(t -> t.stream().findFirst().orElse(null))
                .ifPresent(t -> {
                    result.setBrandName(t.getBrandName());
                    result.setSeriesName(t.getSeriesName());
                    result.setModel(t.getModel());
                    result.setPricePub(t.getPricePub());
                    result.setBrandId(t.getBrandId());
                    result.setSeriesType(t.getSeriesType());
                    result.setSex(t.getSex());
                });
        // 补充回购政策
        this.supplyBuyBackPolicy(result);

        return result;
    }

    @Override
    public StockBaseInfo getByWno(String wno) {
        if (StringUtils.isEmpty(wno)) {
            return null;
        }
        Stock stock = stockService.getOne(Wrappers.<Stock>lambdaQuery()
                .eq(Stock::getWno, wno)
        );
        if (Objects.isNull(stock)) {
            throw new BusinessException(ExceptionCode.GOODS_NOT_SUPPORT);
        }
        StockBaseInfo result = StockConverter.INSTANCE.convertStockBaseInfo(stock);

        Optional.ofNullable(goodsWatchService.getWatchDataFusionListByStockIds(Lists.newArrayList(stock.getId())))
                .map(t -> t.stream().findFirst().orElse(null))
                .ifPresent(t -> {
                    result.setBrandName(t.getBrandName());
                    result.setSeriesName(t.getSeriesName());
                    result.setModel(t.getModel());
                    result.setPricePub(t.getPricePub());
                    result.setImage(t.getImage());
                });

        return result;
    }

    @Override
    public StockBaseInfo getByStockSn(String stockSn) {
        if (StringUtils.isEmpty(stockSn)) {
            return null;
        }
        Stock stock = stockService.getOne(Wrappers.<Stock>lambdaQuery()
                //排出情况
                .notIn(Stock::getStockStatus, Arrays.asList(StockStatusEnum.SOLD_OUT, StockStatusEnum.CONSIGNMENT, StockStatusEnum.PURCHASE_RETURNED, StockStatusEnum.ON_LOAN))
                .eq(Stock::getSn, stockSn)
        );
        if (Objects.isNull(stock)) {
            throw new BusinessException(ExceptionCode.GOODS_NOT_SUPPORT);
        }
        StockBaseInfo result = StockConverter.INSTANCE.convertStockBaseInfo(stock);

        Optional.ofNullable(goodsWatchService.getWatchDataFusionListByStockIds(Lists.newArrayList(stock.getId())))
                .map(t -> t.stream().findFirst().orElse(null))
                .ifPresent(t -> {
                    result.setBrandName(t.getBrandName());
                    result.setSeriesName(t.getSeriesName());
                    result.setModel(t.getModel());
                    result.setPricePub(t.getPricePub());
                    result.setImage(t.getImage());
                });
        return result;
    }

    @Override
    public StockAttachmentResult attachment(StockAttachmentRequest request) {
        stockService.attachment(request);

        return StockAttachmentResult.builder().success(Boolean.TRUE).build();
    }

    @Override
    public List<StockPrintResult> print(StockPrintRequest request) {

        if (CollectionUtils.isNotEmpty(request.getStockIdList())) {
            List<StockExt> select = stockService.selectByStockIdList(request.getStockIdList());

            List<PurchaseByNameResult> purchaseByNameResultList = billPurchaseService.getByPurchaseName(PurchaseByNameRequest.builder().stockIdList(request.getStockIdList()).build());

            if (CollectionUtils.isNotEmpty(select)) {

                List<StockPrintResult> collect = select.stream().map(stockExt -> StockConverter.INSTANCE.convertStockPrintResult(stockExt)).collect(Collectors.toList());

                for (StockPrintResult stockPrintResult : collect) {
                    FlowGradeEnum flowGrade = FlowGradeEnum.UNDEFINED;

                    switch (BusinessBillTypeEnum.fromValue(stockPrintResult.getPurchaseSource())) {
                        case TH_CG_QK:
                        case TH_CG_DJTP:
                        case TH_CG_DJ:
                            flowGrade = FlowGradeEnum.RECEIVE;
                            break;
                        case GR_JS:
                            flowGrade = FlowGradeEnum.CREATE;
                            break;
                        case TH_JS:
                            flowGrade = FlowGradeEnum.ANOMALY2;
                            break;
                        case TH_CG_BH:
                            flowGrade = FlowGradeEnum.NORMAL;
                            break;
                        case TH_CG_PL:
                            flowGrade = FlowGradeEnum.ANOMALY;
                            break;
                        case GR_HS_ZH:
                        case GR_HS_JHS:
                            flowGrade = FlowGradeEnum.EXTERNAL;
                            break;
                        case GR_HG_ZH:
                        case GR_HG_JHS:
                            flowGrade = FlowGradeEnum.EXTERNAL2;
                            break;
                        default:
                            flowGrade = FlowGradeEnum.UNDEFINED;
                    }
                    stockPrintResult.setFlowGrade(flowGrade.getValue());

                    List<PurchaseByNameResult> purchaseByNameResults = purchaseByNameResultList.stream().filter(purchaseByNameResult -> stockPrintResult.getId().equals(purchaseByNameResult.getStockId())).collect(Collectors.toList());

                    if (CollectionUtils.isNotEmpty(purchaseByNameResults) && StringUtils.isNotBlank(purchaseByNameResults.get(FlywheelConstant.INDEX).getPurchaseName())) {
                        stockPrintResult.setCreatedBy(purchaseByNameResults.get(FlywheelConstant.INDEX).getPurchaseName());
                    }
                }

                return collect;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public StockForNoLoginResult getByWnoNoLogin(String wno) {
        if (StringUtils.isEmpty(wno)) {
            return null;
        }
        Stock stock = stockService.getOne(Wrappers.<Stock>lambdaQuery()
                .eq(Stock::getWno, wno)
        );
        if (Objects.isNull(stock)) {
            throw new BusinessException(ExceptionCode.GOODS_NOT_SUPPORT);
        }
        StockForNoLoginResult result = StockConverter.INSTANCE.convertStockForNoLoginResult(stock);

        Optional.ofNullable(goodsWatchService.getWatchDataFusionListByStockIds(Lists.newArrayList(stock.getId())))
                .map(t -> t.stream().findFirst().orElse(null))
                .ifPresent(t -> {
                    result.setGoodsId(t.getGoodsId());
                    result.setBrandName(t.getBrandName());
                    result.setSeriesName(t.getSeriesName());
                    result.setModel(t.getModel());
                    result.setPricePub(t.getPricePub());
                    result.setImage(t.getImage());
                    result.setSex(t.getSex());
                    result.setShape(t.getShape());
                    result.setBraceletColor(t.getBraceletColor());
                    result.setBuckleType(t.getBuckleType());
                    result.setDepth(t.getDepth());
                    result.setWatchcaseMaterial(t.getWatchcaseMaterial());
                    result.setWatchSize(t.getWatchSize());
                    result.setStrapMaterial(t.getStrapMaterial());
                });

        //todo 要是新表 二手表的值变了 会有问题
        List<String> imgUrlList = new ArrayList<>();
        imgUrlList.add(result.getImage());
        //stockSn 二手表 stockId  7
        //stockModel 新表 goodsWatchId 8
        String modelUrl = photoGalleryService.queryPhotoGalleryImgUrlByGoodsId(result.getGoodsId(), FlywheelConstant.newWatch);

        String stockUrl = photoGalleryService.queryPhotoGalleryImgUrlByStockId(result.getStockId(), FlywheelConstant.oldWatch);

        if (StringUtils.isNotBlank(modelUrl)) {
            imgUrlList.addAll(Arrays.asList(modelUrl.split(",")));
        }
        if (StringUtils.isNotBlank(stockUrl)) {
            imgUrlList.addAll(Arrays.asList(stockUrl.split(",")));
        }
        result.setImageList(imgUrlList);


        //toc && 回购政策
        StockBaseInfo stockBaseInfo = this.getById(result.getStockId());
        result.setTocPrice(stockBaseInfo.getTocPrice());
        result.setIsRepurchasePolicy(stockBaseInfo.getIsRepurchasePolicy());
        result.setBuyBackPolicy(stockBaseInfo.getBuyBackPolicy());

        //话术
        LambdaQueryWrapper<ModelLiveScript> qw = Wrappers.<ModelLiveScript>lambdaQuery().eq(ModelLiveScript::getGoodsWatchId, result.getGoodsId()).last("limit 1");
        ModelLiveScript one = modelLiveScriptService.getOne(qw);
        result.setModelLiveScript(one == null ? null : one.getLiveScript());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockSn(StockSnUpdateRequest request) {
        //2。查询表身号
        Map<String, Stock> collectByStock = stockService.list(Wrappers.<Stock>lambdaQuery().in(Stock::getSn, Collections.singletonList(request.getStockSn()))
                .notIn(Stock::getStockStatus, Arrays.asList(
                        StockStatusEnum.PURCHASE_RETURNED_ING,
                        StockStatusEnum.SOLD_OUT,
                        StockStatusEnum.PURCHASE_RETURNED
                ))).stream().collect(Collectors.toMap(Stock::getSn, Function.identity()));
        Stock oldStock = stockService.getById(request.getStockId());

        if (CollectionUtils.isNotEmpty(collectByStock.keySet()) && !oldStock.getSn().equals(request.getStockSn())) {
            throw new OperationRejectedException(OperationExceptionCode.PURCHASE_PARAMETER);
        }
        //原始信息

//        if (!PURCHASE_TYPE.contains(request.getWorkSource()))
//            return;
        //Stock 需要更改表身号
        Stock temp = new Stock();
        temp.setId(request.getStockId());
        temp.setSn(request.getStockSn());
        if (Objects.nonNull(request.getGoodsId())) {
            temp.setGoodsId(request.getGoodsId());
        }
        if (Objects.nonNull(request.getModel())) {
            temp.setGoodsModel(request.getModel());
        }
        stockService.updateById(temp);
        //采购单要修改表身号
        List<BillPurchaseLine> purchaseLineList = billPurchaseLineService.list(new LambdaQueryWrapper<BillPurchaseLine>()
                        .eq(BillPurchaseLine::getStockId, request.getStockId()))
                .stream()
                .map(line -> {
                    BillPurchaseLine purchaseLine = new BillPurchaseLine();
                    purchaseLine.setId(line.getId());
                    if (Objects.isNull(line.getOldStockSn())) {
                        purchaseLine.setOldStockSn(line.getStockSn());
                    }
                    purchaseLine.setStockSn(request.getStockSn());
                    if (Objects.nonNull(request.getGoodsId())) {
                        purchaseLine.setGoodsId(request.getGoodsId());
                        GoodsWatch watch = goodsWatchService.getById(line.getGoodsId());
                        if (Objects.nonNull(watch)) {
                            purchaseLine.setOldModel(watch.getModel());
                        }
                    }
                    return purchaseLine;
                }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(purchaseLineList)) {
            billPurchaseLineService.updateBatchById(purchaseLineList);
        }
        //财务需要改表身号
        List<FinancialDocumentsDetail> detailList = documentsDetailService.list(new LambdaQueryWrapper<FinancialDocumentsDetail>()
                        .eq(FinancialDocumentsDetail::getStockId, request.getStockId()))
                .stream()
                .map(documentsDetail -> {
                    FinancialDocumentsDetail detail = new FinancialDocumentsDetail();
                    detail.setId(documentsDetail.getId());
                    detail.setStockSn(request.getStockSn());
                    if (Objects.nonNull(request.getModel())) {
                        detail.setModelName(request.getModel());
                    }
                    return detail;
                }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(detailList)) {
            documentsDetailService.updateBatchById(detailList);
        }

        //应收应付需要改表身号
        List<AccountsPayableAccounting> accountingList = accountingService.list(new LambdaQueryWrapper<AccountsPayableAccounting>()
                        .eq(AccountsPayableAccounting::getStockId, request.getStockId()))
                .stream()
                .map(payableAccounting -> {
                    AccountsPayableAccounting accounting = new AccountsPayableAccounting();
                    accounting.setId(payableAccounting.getId());
                    accounting.setStockSn(request.getStockSn());
                    if (Objects.nonNull(request.getModel()))
                        accounting.setModel(request.getModel());
                    return accounting;
                }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(accountingList)) {
            accountingService.updateBatchById(accountingList);
        }

        //审核记录
        List<AuditLoggingDetail> loggingDetailList = loggingDetailService.list(new LambdaQueryWrapper<AuditLoggingDetail>()
                        .eq(AuditLoggingDetail::getStockId, request.getStockId()))
                .stream()
                .map(loggingDetail -> {
                    AuditLoggingDetail detail = new AuditLoggingDetail();
                    detail.setId(loggingDetail.getId());
                    detail.setStockSn(request.getStockSn());
                    if (Objects.nonNull(request.getModel()))
                        detail.setModel(request.getModel());
                    return detail;
                }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(loggingDetailList)) {
            loggingDetailService.updateBatchById(loggingDetailList);
        }

        List<WatchDataFusion> oldWatchDataFusionList = goodsWatchService.getWatchDataFusionListByGoodsIds(Arrays.asList(oldStock.getGoodsId()));

        if (CollectionUtils.isEmpty(oldWatchDataFusionList)) {
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
        }

        WatchDataFusion oldWatchDataFusion = oldWatchDataFusionList.get(FlywheelConstant.INDEX);

        //插入日志
        LogStockOpt logStockOpt = new LogStockOpt();
        //插入日志
        logStockOpt.setOptMode(1);
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
                oldWatchDataFusion.getBrandName(), oldWatchDataFusion.getSeriesName(), oldWatchDataFusion.getModel(),
                request.getStockSn(), oldStock.getAttachment(), oldStock.getFiness(), oldStock.getLevel(), oldStock.getStrap(),
                oldStock.getWeek(), oldStock.getWatchSection(), oldStock.getUnusualDesc()));

        logStockOptService.save(logStockOpt);
    }

    @Override
    public void updateStockLimitedCode(UpdateStockLimitedCodeRequest request) {
        Stock stock = new Stock();
        stock.setId(Objects.requireNonNull(request.getStockId()));
        stock.setLimitedCode(Objects.requireNonNull(request.getLimitedCode()));
        stockService.updateById(stock);
    }

    @Override
    public boolean unLockDemand(StockUnLockDemandRequest request) {
        Stock stock = new Stock();
        stock.setId(Objects.requireNonNull(request.getStockId()));
        stock.setLockDemand(NumberUtils.INTEGER_ZERO);
        return stockService.updateById(stock);
    }

    @Override
    public PageResult<StockGoodQueryResult> modelStockFold(StockGoodQueryRequest request) {
        Page<StockGoodQueryResult> stockGoodQueryResultPage = stockService.modelStockFoldList(request);
        return PageResult.<StockGoodQueryResult>builder()
                .result(stockGoodQueryResultPage.getRecords())
                .totalCount(stockGoodQueryResultPage.getTotal())
                .totalPage(stockGoodQueryResultPage.getPages())
                .build();
    }

    @Resource
    private BillPricingService billPricingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResult<ModelPriceChangeImportResult> modelPriceChange(ModelPriceChangeImportRequest request) {

        List<ModelPriceChangeImportRequest.ImportDto> importDtoList = request.getDataList();

        Set<String> errorList = new HashSet<>();

        //查询型号列表
        Map<String, Map<String, List<GoodsBaseInfo>>> goodsMap = goodsWatchService.listGoodsBaseInfo(importDtoList.stream()
                        .map(ModelPriceChangeImportRequest.ImportDto::getBrandName)
                        .collect(Collectors.toList()), importDtoList.stream()
                        .map(ModelPriceChangeImportRequest.ImportDto::getModel)
                        .map(StringTools::purification)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.groupingBy(GoodsBaseInfo::getBrandName, Collectors.groupingBy(GoodsBaseInfo::getModel)));

        List<ModelPriceChangeImportResult> collect = importDtoList.stream().map(importDto -> {
                    GoodsBaseInfo goodsBaseInfo = Optional.ofNullable(goodsMap.get(importDto.getBrandName()))
                            .map(t -> Optional.ofNullable(t.get(importDto.getModel())) // 优先匹配型号相同
                                    .orElse(t.entrySet()
                                            .stream()
                                            .filter(et -> StringTools.purification(et.getKey()).equals(StringTools.purification(importDto.getModel()))) //过滤型号简码相同的
                                            .map(Map.Entry::getValue)
                                            .findFirst()
                                            .orElse(null)))
                            .map(t -> t.stream().findFirst().orElse(null))
                            .orElse(null);

                    if (Objects.isNull(goodsBaseInfo)) {
                        errorList.add(StringUtils.join(Arrays.asList(importDto.getBrandName(), importDto.getSeriesName(), importDto.getModel()), "/"));
                        return null;
                    }

                    List<GoodsStockCPrice> goodsStockCPriceList = goodsMetaInfoSyncMapper.checkCPrice(goodsBaseInfo.getGoodsId(), false);
                    if (CollectionUtils.isNotEmpty(goodsStockCPriceList)) {
                        goodsStockCPriceList.stream().filter(goodsStockCPrice -> {
                            if (Objects.nonNull(goodsStockCPrice.getTocPrice()) && goodsStockCPrice.getTocPrice().compareTo(BigDecimal.ZERO) > 0) {
                                return true;
                            }
                            return false;
                        }).forEach(goodsStockCPrice -> {
                            //修改价格
                            Stock stock = new Stock();
                            stock.setId(goodsStockCPrice.getStockId());
                            stock.setTobPrice(importDto.getTobPrice());
                            stock.setTocPrice(importDto.getTocPrice());
                            stock.setTagPrice(importDto.getTocPrice().add(MAP.get(importDto.getTocPrice())));
                            stockService.updateById(stock);

                            preProcessing(goodsStockCPrice, importDto);
                        });
                    }

                    return ModelPriceChangeImportResult.builder()
                            .goodsId(goodsBaseInfo.getGoodsId())
                            .build();

                }).filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ImportResult.<ModelPriceChangeImportResult>builder()
                .successList(collect)
                .errList(Lists.newArrayList(errorList))
                .build();
    }

    /**
     * 同行寄售结算导入
     *
     * @param request
     * @return
     */
    @Override
    public ImportResult<SettleStockQueryImportResult> settleStockQueryImport(SettleStockQueryImportRequest request) {
        Map<String, BigDecimal> stockSnMap = request.getDataList().stream()
                .collect(Collectors.toMap(SettleStockQueryImportRequest.ImportDto::getStockSn,
                        SettleStockQueryImportRequest.ImportDto::getSettlePrice));
        StockListRequest stockListRequest = StockListRequest.builder()
                .customerId(request.getCustomerId())
                .useScenario(StockListRequest.UseScenario.BATCH_CONSIGNMENT_SETTLEMENT)
                .build();
        stockListRequest.setPage(1);
        stockListRequest.setLimit(9999);
        stockListRequest.setStockSnList(request.getDataList().stream().map(SettleStockQueryImportRequest.ImportDto::getStockSn).collect(Collectors.toList()));

        PageResult<StockBaseInfo> pageResult = listStock(stockListRequest);


        Map<String, Integer> map = pageResult.getResult().stream()
                .collect(Collectors.toMap(StockBaseInfo::getStockSn, StockBaseInfo::getStockId));

        return ImportResult.<SettleStockQueryImportResult>builder()
                .successList(pageResult.getResult().stream()
                        .filter(t -> stockSnMap.containsKey(t.getStockSn()))
                        .map(t -> {
                            SettleStockQueryImportResult r = StockConverter.INSTANCE.convertSettleStockQueryImportResult(t);
                            r.setSettlePrice(stockSnMap.get(t.getStockSn()));
                            return r;
                        })
                        .collect(Collectors.toList()))
                .errList(stockSnMap.keySet().stream().filter(t -> !map.containsKey(t)).collect(Collectors.toList()))
                .build();
    }

    /**
     * 补充回购政策
     *
     * @param baseInfo
     * @return
     */
    private StockBaseInfo supplyBuyBackPolicy(StockBaseInfo baseInfo) {
        if (Objects.isNull(baseInfo)) {
            return null;
        }
        if (!SeriesTypeEnum.WRISTWATCH.getValue().equals(baseInfo.getSeriesType())) {
            return null;
        }

        List<BuyBackPolicyInfo> buyBackPolicy = buyBackPolicyService.getStockBuyBackPolicy(BuyBackPolicyBO.builder()
                .finess(baseInfo.getFiness())
                .sex(baseInfo.getSex())
                .brandId(baseInfo.getBrandId())
                .clinchPrice(baseInfo.getTocPrice())
                .build());

        baseInfo.setIsRepurchasePolicy(CollectionUtils.isNotEmpty(buyBackPolicy) ? WhetherEnum.YES.getValue() : WhetherEnum.NO.getValue());
        baseInfo.setBuyBackPolicy(buyBackPolicy);

        return baseInfo;
    }

    /**
     * 更改采购单
     *
     * @param goodsStockCPrice
     * @param importDto
     */
    private void preProcessing(GoodsStockCPrice goodsStockCPrice, ModelPriceChangeImportRequest.ImportDto importDto) {

        List<BillPricing> list = billPricingService.list(Wrappers.<BillPricing>lambdaQuery().eq(BillPricing::getStockId, goodsStockCPrice.getStockId()));

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        BillPricing pricing = list.get(0);
        //定价单变更
        BillPricing billPricing = new BillPricing();

        //特殊处理
        billPricing.setBMargin(importDto.getTobPrice().subtract(pricing.getAllPrice()).divide(importDto.getTobPrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)));
        billPricing.setCMargin(importDto.getTocPrice().subtract(pricing.getAllPrice()).divide(importDto.getTocPrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)));

        //前闭后开
        billPricing.setTPrice(importDto.getTocPrice().add(MAP.get(importDto.getTocPrice())));

        billPricing.setBPrice(importDto.getTobPrice());
        billPricing.setCPrice(importDto.getTocPrice());
        billPricingService.update(billPricing, Wrappers.<BillPricing>lambdaQuery().eq(BillPricing::getStockId, goodsStockCPrice.getStockId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResult<StockPromotionImportResult> stockPurchaseUpdate(StockPurchaseUpdateImportRequest request) {
        if (UserContext.getUser().getRoles().stream().noneMatch(a -> "admin".equals(a.getRoleName()))) {
            return ImportResult.<StockPromotionImportResult>builder()
                    .successList(null)
                    .errList(Lists.newArrayList("你不是admin，不能操作!!!!!"))
                    .build();
        }
        log.info("开始进行采购价修改，并且相对应财务业务修改 {}", request);
        List<String> errorList = new ArrayList<>();

        Map<String, StockPurchaseUpdateImportRequest.ImportDto> stockMap = request.getDataList()
                .stream()
                .collect(Collectors.
                        toMap(StockPurchaseUpdateImportRequest.ImportDto::getStockSn, Function.identity()));
        //查询的表身号条件
        List<String> stockSnList = new ArrayList<>(stockMap.keySet());

        List<Stock> stocks = stockService.list(new LambdaQueryWrapper<Stock>()
                        .in(Stock::getSn, stockSnList)
                        .gt(Stock::getCreatedTime, DateUtils.parseStrToDate("2023-09-19 00:00:00")))
                .stream()
                .map(stock -> {
                    StockPurchaseUpdateImportRequest.ImportDto dto = stockMap.get(stock.getSn());
                    if (BigDecimalUtil.eq(stock.getPurchasePrice(), dto.getPurchasePrice())) {
                        errorList.add(stock.getSn());
                        return null;
                    }
                    Stock s = new Stock();
                    s.setId(stock.getId());
                    s.setPurchasePrice(stockMap.get(stock.getSn()).getPurchasePrice());
                    return s;
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(stocks)) {
            stockService.updateBatchById(stocks);
            stockService.recalculateConsignmentPrice(stocks.stream().map(Stock::getId).collect(Collectors.toList()));
        } else {
            return ImportResult.<StockPromotionImportResult>builder()
                    .successList(null)
                    .errList(Lists.newArrayList("没有符合的表身号可以改！！！！"))
                    .build();
        }

        Map<Integer, BigDecimal> bpMap = new HashMap<>();

        List<Stock> stockList = stockService.list(new LambdaQueryWrapper<Stock>()
                .in(Stock::getSn, stockSnList)
                .gt(Stock::getCreatedTime, DateUtils.parseStrToDate("2023-09-19 00:00:00")));

        Map<Integer, List<BillPurchaseLine>> purchaseLineMap = billPurchaseLineService.list(new LambdaQueryWrapper<BillPurchaseLine>()
                        .in(BillPurchaseLine::getStockId, stockList.stream().map(Stock::getId).collect(Collectors.toList())))
                .stream()
                .collect(Collectors.groupingBy(BillPurchaseLine::getStockId));

        Map<Integer, List<BillSaleOrderLine>> orderLineMap = billSaleOrderLineService.list(new LambdaQueryWrapper<BillSaleOrderLine>()
                        .in(BillSaleOrderLine::getStockId, stockList.stream().map(Stock::getId).collect(Collectors.toList())))
                .stream()
                .collect(Collectors.groupingBy(BillSaleOrderLine::getStockId));

        List<FinancialDocumentsDetail> detailList = documentsDetailService.list(new LambdaQueryWrapper<FinancialDocumentsDetail>()
                .in(FinancialDocumentsDetail::getStockId, stockList.stream().map(Stock::getId).collect(Collectors.toList())));

        Map<Integer, List<FinancialDocumentsDetail>> documentsDetailMap = detailList
                .stream()
                .collect(Collectors.groupingBy(FinancialDocumentsDetail::getStockId));

        Map<Integer, FinancialDocuments> documentsMap = documentsService.list(new LambdaQueryWrapper<FinancialDocuments>()
                        .in(FinancialDocuments::getId, detailList.stream().map(FinancialDocumentsDetail::getFinancialDocumentsId).collect(Collectors.toList())))
                .stream()
                .collect(Collectors.toMap(FinancialDocuments::getId, Function.identity()));

        stockList
                .stream().collect(Collectors.groupingBy(Stock::getSn))
                .forEach((sn, list) -> {
                    StockPurchaseUpdateImportRequest.ImportDto dto = stockMap.get(sn);
                    list.forEach(stock -> {
                        if (Objects.nonNull(dto)) {
                            //改采购详情
                            if (purchaseLineMap.containsKey(stock.getId())) {
                                purchaseLineMap.get(stock.getId()).forEach(billPurchaseLine -> {
                                    if (bpMap.containsKey(billPurchaseLine.getPurchaseId())) {
                                        bpMap.put(billPurchaseLine.getPurchaseId(),
                                                bpMap.get(billPurchaseLine.getPurchaseId())
                                                        .add(stock.getPurchasePrice()
                                                                .subtract(billPurchaseLine.getPurchasePrice())));
                                    } else {
                                        bpMap.put(billPurchaseLine.getPurchaseId(),
                                                stock.getPurchasePrice().subtract(billPurchaseLine.getPurchasePrice()));
                                    }
                                    BillPurchaseLine line = new BillPurchaseLine();
                                    line.setId(billPurchaseLine.getId());
                                    line.setConsignmentPrice(stock.getConsignmentPrice());
                                    line.setPurchasePrice(dto.getPurchasePrice());
                                    billPurchaseLineService.updateById(line);
                                });
                            }

                            //改销售详情
                            if (orderLineMap.containsKey(stock.getId())) {
                                orderLineMap.get(stock.getId()).forEach(orderLine -> {
                                    BillSaleOrderLine line = new BillSaleOrderLine();
                                    line.setId(orderLine.getId());
                                    line.setConsignmentPrice(stock.getConsignmentPrice());
                                    billSaleOrderLineService.updateById(line);
                                });
                            }

                            //改财务
                            if (documentsDetailMap.containsKey(stock.getId())) {
                                List<FinancialDocumentsDetail> details = documentsDetailMap.get(stock.getId())
                                        .stream()
                                        .map(detail -> {
                                            if (documentsMap.containsKey(detail.getFinancialDocumentsId())) {
                                                FinancialDocuments documents = documentsMap.get(detail.getFinancialDocumentsId());
                                                FinancialDocumentsDetail financialDocumentsDetail = new FinancialDocumentsDetail();
                                                financialDocumentsDetail.setId(detail.getId());
                                                switch (documents.getOrderType()) {
                                                    case 100:
                                                    case 200:
                                                        //采购
                                                        //采购退货
                                                        financialDocumentsDetail.setPurchasePrice(dto.getPurchasePrice());
                                                        detail.setPurchasePrice(dto.getPurchasePrice());
                                                        break;
                                                    case 300:
                                                        //销售
                                                    case 400:
                                                        //销售退货
                                                        financialDocumentsDetail.setConsignSalePrice(stock.getConsignmentPrice());
                                                        detail.setConsignSalePrice(stock.getConsignmentPrice());
                                                        break;
                                                    case 500:
                                                        //商品调拨
                                                        financialDocumentsDetail.setConsignSalePrice(stock.getConsignmentPrice());
                                                        financialDocumentsDetail.setPurchasePrice(dto.getPurchasePrice());
                                                        detail.setPurchasePrice(dto.getPurchasePrice());
                                                        detail.setConsignSalePrice(stock.getConsignmentPrice());
                                                        break;
                                                    case 600:
                                                        //服务费
                                                        if (!BigDecimalUtil.eq(dto.getClinchPrice(), BigDecimal.ZERO)) {
                                                            financialDocumentsDetail.setServiceFee(dto.getClinchPrice()
                                                                    .subtract(stock.getConsignmentPrice())
                                                                    .multiply(new BigDecimal("0.5"))
                                                                    .setScale(2, RoundingMode.HALF_UP));
                                                            financialDocumentsDetail.setConsignSalePrice(stock.getConsignmentPrice());
                                                            detail.setServiceFee(financialDocumentsDetail.getServiceFee());
                                                            detail.setConsignSalePrice(stock.getConsignmentPrice());
                                                        } else {
                                                            financialDocumentsDetail.setServiceFee(detail.getServiceFee());
                                                        }
                                                        break;
                                                }
                                                return financialDocumentsDetail;
                                            } else {
                                                return null;
                                            }
                                        })
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList());
                                if (CollectionUtils.isNotEmpty(details))
                                    documentsDetailService.updateBatchById(details);
                            }
                        } else {
                            errorList.add(sn);
                        }
                    });
                });

        bpMap.forEach((bpId, totalPrice) -> {
            billPurchaseService.updateTotalPrice(bpId, totalPrice);
        });

        List<FinancialDocuments> fdList = new ArrayList<>();
        detailList
                .stream()
                .collect(Collectors.groupingBy(FinancialDocumentsDetail::getFinancialDocumentsId))
                .forEach((fdId, list) -> {
                    if (documentsMap.containsKey(fdId)) {
                        FinancialDocuments documents = documentsMap.get(fdId);
                        switch (documents.getOrderType()) {
                            case 100:
                            case 200:
                                //采购
                                //采购退货
                                FinancialDocuments d = new FinancialDocuments();
                                d.setId(fdId);
                                d.setOrderMoney(list.stream().map(FinancialDocumentsDetail::getPurchasePrice).reduce(BigDecimal.ZERO, BigDecimal::add));
                                fdList.add(d);
                                break;
                            case 300:
                                //销售
                            case 400:
                                //销售退货
                                break;
                            case 500:
                                //商品调拨
                                FinancialDocuments dd = new FinancialDocuments();
                                dd.setId(fdId);
                                dd.setOrderMoney(list.stream().map(FinancialDocumentsDetail::getConsignSalePrice).reduce(BigDecimal.ZERO, BigDecimal::add));
                                fdList.add(dd);
                                break;
                            case 600:
                                //服务费
                                FinancialDocuments ddd = new FinancialDocuments();
                                ddd.setId(fdId);
                                ddd.setOrderMoney(list.stream().map(FinancialDocumentsDetail::getServiceFee).reduce(BigDecimal.ZERO, BigDecimal::add));
                                fdList.add(ddd);
                                break;
                        }
                    }
                });
        if (CollectionUtils.isNotEmpty(fdList))
            documentsService.updateBatchById(fdList);
        return ImportResult.<StockPromotionImportResult>builder()
                .successList(null)
                .errList(errorList)
                .build();
    }

    @Override
    public ImportResult<FinancialInvoiceStockQueryImportResult> invoiceStockQueryImport(FinancialInvoiceStockQueryImportRequest request) {
        List<String> list = request.getDataList().stream()
                .map(FinancialInvoiceStockQueryImportRequest.ImportDto::getStockSn)
                .collect(Collectors.toList());
        StockListRequest stockListRequest = StockListRequest.builder()
                .customerId(request.getCustomerId())
                .useScenario(StockListRequest.UseScenario.APPLY_FINANCIAL_INVOICE)
                .stockSnList(list)
                .build();
        stockListRequest.setPage(1);
        stockListRequest.setLimit(list.size());
        PageResult<StockBaseInfo> pageResult = listStock(stockListRequest);

        Map<String, Integer> map = pageResult.getResult().stream()
                .collect(Collectors.toMap(StockBaseInfo::getStockSn, StockBaseInfo::getStockId));

        Map<Integer, String> purchaseSubjectMap = purchaseSubjectService.list()
                .stream()
                .collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));

        List<String> errList = new ArrayList<>();
        //查找相对应的销售单
        for (String t : list) {
            if (!map.containsKey(t)) {
                errList.add(t);
            }
        }
        return ImportResult.<FinancialInvoiceStockQueryImportResult>builder()
                .successList(pageResult.getResult().stream()
                        .map(t -> {
                            FinancialInvoiceStockQueryImportResult r = StockConverter.INSTANCE.convertInvoiceStockQueryImportResult(t);
                            r.setBelongName(purchaseSubjectMap.get(t.getBelongId()));
                            return r;
                        })
                        .collect(Collectors.toList()))
                .errList(errList)
                .build();
    }

    @Override
    public PageResult<StockGoodQueryResult> queryStockPage(StockGoodQueryRequest request) {
        request.setGroupingGoodsId(Boolean.FALSE);
        if (StockGoodQueryRequest.UseScenario.INVENTORY_LIST.equals(request.getUseScenario())) {
            request.setStockStatus(StockStatusEnum.MARKETABLE.getValue());
            request.setQueryBatchBorrowing(Boolean.TRUE);
        }
        Page<StockGoodQueryResult> stockGoodQueryResultPage = stockService.queryStockPage(request);
        return PageResult.<StockGoodQueryResult>builder()
                .result(stockGoodQueryResultPage.getRecords())
                .totalCount(stockGoodQueryResultPage.getTotal())
                .totalPage(stockGoodQueryResultPage.getPages())
                .build();
    }

    @Resource
    private StockMarketsService marketsService;

    @Override
    public void saveStockMarketsInfo(StockMarketsInfo info) {
        StockMarkets markets = new StockMarkets();
        markets.setStockId(info.getStockId());
        markets.setImages(info.getImages());
        markets.setMarketsPrice(info.getMarketsPrice());
        marketsService.saveOrUpdate(markets, Wrappers.<StockMarkets>lambdaUpdate()
                .eq(StockMarkets::getStockId, info.getStockId()));
    }

    @Override
    public ImportResult<GroupSettleStockQueryImportResult> settleStockQueryImport(GroupSettleStockQueryImportRequest request) {

        /**
         * 入参
         */
        Map<String, BigDecimal> stockSnMap = request.getDataList().stream()
                .collect(Collectors.toMap(t -> t.getOriginSerialNo() + "#" + t.getStockSn(),
                        GroupSettleStockQueryImportRequest.ImportDto::getSettlePrice));

        StockListRequest stockListRequest = StockListRequest.builder()
                .useScenario(StockListRequest.UseScenario.GROUP_CONSIGNMENT_SETTLEMENT)
                .build();
        stockListRequest.setPage(1);
        stockListRequest.setLimit(9999);
        stockListRequest.setStockSnList(request.getDataList().stream().map(GroupSettleStockQueryImportRequest.ImportDto::getStockSn).collect(Collectors.toList()));

        PageResult<StockBaseInfo> pageResult = listStock(stockListRequest);

        Map<String, StockBaseInfo> map = pageResult.getResult().stream()
                .collect(Collectors.toMap(t -> t.getOriginSerialNo() + "#" + t.getStockSn(), v -> v));

        List<String> collect = new ArrayList<>(CollectionUtils.intersection(stockSnMap.keySet(), map.keySet()));

        List<String> list = new ArrayList<>(CollectionUtils.subtract(stockSnMap.keySet(), map.keySet()));

        return ImportResult.<GroupSettleStockQueryImportResult>builder()
                .successList(
                        map.entrySet().stream().filter(r -> collect.contains(r.getKey())).map(g -> {
                            StockBaseInfo t = g.getValue();
                            GroupSettleStockQueryImportResult r = StockConverter.INSTANCE.convertGroupSettleStockQueryImportResult(t);
                            r.setSettlePrice(stockSnMap.get(t.getOriginSerialNo() + "#" + t.getStockSn()));
                            return r;
                        }).collect(Collectors.toList())
                )
                .errList(stockSnMap.keySet().stream().filter(list::contains).collect(Collectors.toList()))
                .build();
    }

    @Override
    public int inExceptionStock(List<Integer> ids) {
        return stockService.inExceptionStock(ids);
    }

    @Override
    public List<StockBaseInfo> listStockByStockSnList(StockListRequest request) {
        //调拨任务排除总部商品
        request.setExcludedLocationId(FlywheelConstant._ZB_ID);
        //商品要是可售的
        request.setStockStatus(StockStatusEnum.MARKETABLE.getValue());
        List<StockBaseInfo> stockBaseInfos = stockService.listStockByStockSnList(request);
        listStockResult(stockBaseInfos, StockListRequest.UseScenario.ALLOCATE_TASK);
        return stockBaseInfos;
    }

    @Override
    public List<StockBaseInfo> listStockByStockSnList2(StockListRequest request) {

        //调拨任务排除总部商品
//        request.setExcludedLocationId(FlywheelConstant._ZB_ID);
        //商品要是可售的
        request.setStockStatus(StockStatusEnum.MARKETABLE.getValue());
        List<StockBaseInfo> stockBaseInfos = stockService.listStockByStockSnList(request);
        listStockResult(stockBaseInfos, StockListRequest.UseScenario.ALLOCATE_TASK);
        return stockBaseInfos;
    }

}
