package com.seeease.flywheel.serve.storework.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.qt.request.QualityTestingCreateRequest;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.event.BillHandlerEventPublisher;
import com.seeease.flywheel.serve.douyin.entity.DouYinRefundCreated;
import com.seeease.flywheel.serve.douyin.service.DouYinRefundCreatedService;
import com.seeease.flywheel.serve.fix.entity.BillFix;
import com.seeease.flywheel.serve.fix.enums.FixStateEnum;
import com.seeease.flywheel.serve.fix.service.BillFixService;
import com.seeease.flywheel.serve.goods.entity.GoodsStockCPrice;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.mapper.GoodsMetaInfoSyncMapper;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.Store;
import com.seeease.flywheel.serve.maindata.service.StoreRelationshipSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreService;
import com.seeease.flywheel.serve.pricing.entity.LogPriceOpt;
import com.seeease.flywheel.serve.pricing.service.LogPriceOptService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.qt.service.BillQualityTestingService;
import com.seeease.flywheel.serve.recycle.service.IRecycleOrderService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.enums.SaleOrderChannelEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderModeEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.flywheel.serve.storework.convert.BillStoreWorkPreConvert;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.entity.WmsWorkCollect;
import com.seeease.flywheel.serve.storework.enums.StoreWorkLogisticsRejectStateEnum;
import com.seeease.flywheel.serve.storework.enums.StoreWorkTypeEnum;
import com.seeease.flywheel.serve.storework.enums.WmsWorkCollectWorkStateEnum;
import com.seeease.flywheel.serve.storework.event.*;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.flywheel.serve.storework.service.WmsWorkCollectService;
import com.seeease.flywheel.serve.storework.service.WmsWorkInterceptService;
import com.seeease.flywheel.storework.IStoreWorkFacade;
import com.seeease.flywheel.storework.request.*;
import com.seeease.flywheel.storework.result.*;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Auther Gilbert
 * @Date 2023/1/17 17:40
 */
@DubboService(version = "1.0.0")
@Slf4j
public class StoreWorkFacade implements IStoreWorkFacade {

    //采购类型
    List<BusinessBillTypeEnum> PURCHASE_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.TH_CG_DJ,
            BusinessBillTypeEnum.TH_CG_BH,
            BusinessBillTypeEnum.TH_CG_PL,
            BusinessBillTypeEnum.TH_CG_QK,
            BusinessBillTypeEnum.TH_CG_DJTP,
            BusinessBillTypeEnum.TH_JS,
            BusinessBillTypeEnum.GR_JS,
            BusinessBillTypeEnum.GR_HS_JHS,
            BusinessBillTypeEnum.GR_HS_ZH,
            BusinessBillTypeEnum.GR_HG_ZH,
            BusinessBillTypeEnum.GR_HG_JHS

    );

    @Resource
    protected StoreRelationshipSubjectService storeRelationshipSubjectService;
    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;
    @Resource
    private BillQualityTestingService billQualityTestingService;
    @Resource
    private BillFixService billFixService;
    @Resource
    private StockService stockService;
    @Resource
    private StoreService storeService;
    @Resource
    private BillHandlerEventPublisher billHandlerEventPublisher;
    @Resource
    private BillSaleOrderService billSaleOrderService;
    @Resource
    private BillPurchaseLineService billPurchaseLineService;
    @Resource
    private DouYinRefundCreatedService refundCreatedService;
    @Resource
    private LogPriceOptService logPriceOptService;
    @Resource
    private WmsWorkCollectService wmsWorkCollectService;
    @Resource
    private WmsWorkInterceptService wmsWorkInterceptService;

    @Resource
    private IRecycleOrderService recycleOrderService;

    @Resource
    private TransactionTemplate trx;

    /**
     * 创建作业
     *
     * @param request
     * @return
     */
    @Override
    public List<StoreWorkCreateResult> create(List<StoreWorKCreateRequest> request) {
        return billStoreWorkPreService.create(request);
    }


    /**
     * 物流收货
     * 拒收对于原单操作
     *
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StoreWorkReceivedListResult logisticsReceiving(StoreWorkReceivedRequest request) {
        //当前操作人门店id
        Integer shopId = UserContext.getUser().getStore().getId();
        //物流收货
        StoreWorkReceivedListResult result = billStoreWorkPreService.receiving(request);

        StoreWorkLogisticsRejectStateEnum stateEnum = StoreWorkLogisticsRejectStateEnum.fromCode(request.getLogisticsRejectState());
        //查询出作业单号
        List<BillStoreWorkPre> workPreList = billStoreWorkPreService.listByIds(result.getWorkIds());
        List<StoreWorkReceivedListResult.PriceMessage> list = new ArrayList<>();
        switch (stateEnum) {
            case NORMAL:
                if (request.isShopReceived()) {
                    //入库上架商品
                    this.inStoragePutOnSale(workPreList, shopId, true, Arrays.asList());
                    //每一个入库单
                    for (BillStoreWorkPre billStoreWorkPre : workPreList) {
                        //新表 同一型号的新表集合
                        list.add(selectNewStock(billStoreWorkPre.getStockId()));
                    }
                } else {
                    //更新商品位置
                    stockService.updateBatchById(workPreList.stream()
                            .sorted(Comparator.comparing(BillStoreWorkPre::getStockId))
                            .map(t -> new Stock()
                                    .setId(t.getStockId())
                                    .setLocationId(shopId))
                            .collect(Collectors.toList()));
                    //总部收货批量生成质检单
                    billQualityTestingService.create(workPreList.stream()
                            .map(t -> new QualityTestingCreateRequest()
                                    .setQtSource(t.getWorkSource().getValue())
                                    .setOriginSerialNo(t.getOriginSerialNo())
                                    .setStoreWorkSerialNo(t.getSerialNo())
                                    .setStockId(t.getStockId())
                                    .setWorkId(t.getId())
                                    .setCustomerId(t.getCustomerId())
                                    .setCustomerContactId(t.getCustomerContactId()))
                            .collect(Collectors.toList()));
                }
                break;
        }

        result.setStoreWorkCreateResultList(BillStoreWorkPreConvert.INSTANCE.convertStoreWorkWaitReceivingResult(workPreList));

        //发送物流收货事件
        billHandlerEventPublisher.publishEvent(new LogisticsReceivingEvent(workPreList,
                StoreWorkLogisticsRejectStateEnum.fromCode(request.getLogisticsRejectState()),
                request.isShopReceived()));

        result.setList(list);
        return result;
    }

    @Resource
    private GoodsMetaInfoSyncMapper goodsMetaInfoSyncMapper;

    /**
     * 仓库入库
     *
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StoreWorkInStorageListResult inStorage(StoreWorkInStorageRequest request) {
        //当前操作人门店id
        Integer shopId = UserContext.getUser().getStore().getId();
        StoreWorkInStorageListResult result = billStoreWorkPreService.inStorage(request);
        //改变商品状态为可售状态 异常入库不能可售
        List<BillStoreWorkPre> workPreList = billStoreWorkPreService.listByIds(result.getWorkIds());

        result.setStoreWorkCreateResultList(BillStoreWorkPreConvert.INSTANCE.convertStoreWorkWaitReceivingResult(workPreList));

        List<String> stockSnList = new ArrayList<String>();

        //入库上架商品
        this.inStoragePutOnSale(workPreList, shopId, false, stockSnList);
        //发送入库事件
        billHandlerEventPublisher.publishEvent(new InStorageEvent(workPreList));
        List<StoreWorkInStorageListResult.PriceMessage> list = new ArrayList<>();
        //每一个入库单
        for (BillStoreWorkPre billStoreWorkPre : workPreList) {
            //新表 同一型号的新表集合
            list.add(selectNewStock2(billStoreWorkPre.getStockId()));
        }

        result.setStockSnList(stockSnList);
        result.setList(list);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StoreWorkOutStorageSupplyStockResult outStorageSupplyStock(StoreWorkOutStorageSupplyStockRequest request) {
        Integer shopId = UserContext.getUser().getStore().getId();
        //查商品
        Map<String, Stock> stockMap = stockService.list(Wrappers.<Stock>lambdaQuery()
                        .eq(Stock::getStockStatus, StockStatusEnum.MARKETABLE) //状态可售
                        .eq(Stock::getLocationId, shopId) //商品位置确认
                        .eq(request.getScenario().equals(StoreWorkOutStorageSupplyStockRequest.SupplyScenario.ALLOCATE), Stock::getRightOfManagement, Objects.requireNonNull(storeRelationshipSubjectService.getByShopId(shopId).getSubjectId())) // 经营权确认
                        .in(Stock::getSn, request.getLineList().stream().map(StoreWorkOutStorageSupplyStockRequest.OutStorageSupplyStockDto::getStockSn).collect(Collectors.toList())) //表身号
                        .isNull(Stock::getTemp))
                .stream()
                .collect(Collectors.toMap(Stock::getSn, Function.identity()));

        List<String> stockSnList = request.getLineList()
                .stream()
                .map(StoreWorkOutStorageSupplyStockRequest.OutStorageSupplyStockDto::getStockSn)
                .collect(Collectors.toList());

        //校验商品
        Collection<String> errorStock = CollectionUtils.subtract(stockSnList, stockMap.keySet().stream().collect(Collectors.toList()));
        if (CollectionUtils.isNotEmpty(errorStock)) {
            throw new OperationRejectedException(request.getScenario().equals(StoreWorkOutStorageSupplyStockRequest.SupplyScenario.ALLOCATE)
                    ? OperationExceptionCode.GOODS_NOT_ALLOCATE : OperationExceptionCode.GOODS_NOT_SALE,
                    errorStock.stream().collect(Collectors.joining(",")));
        }

        //查出库单
        Map<Integer, BillStoreWorkPre> outWorkPreMap = billStoreWorkPreService.list(Wrappers.<BillStoreWorkPre>lambdaUpdate()
                        .in(BillStoreWorkPre::getId, request.getLineList()
                                .stream()
                                .map(StoreWorkOutStorageSupplyStockRequest.OutStorageSupplyStockDto::getId)
                                .collect(Collectors.toList()))
                        .isNull(BillStoreWorkPre::getStockId)
                        .eq(BillStoreWorkPre::getWorkType, StoreWorkTypeEnum.OUT_STORE)
                        .eq(BillStoreWorkPre::getOriginSerialNo, request.getOriginSerialNo()))
                .stream()
                .collect(Collectors.toMap(BillStoreWorkPre::getId, Function.identity()));

        //填充商品id
        List<BillStoreWorkPre> outWorkList = request.getLineList()
                .stream()
                .map(t -> {
                    BillStoreWorkPre outWork = outWorkPreMap.get(t.getId());
                    if (Objects.isNull(outWork) || Objects.nonNull(outWork.getStockId())) {
                        throw new BusinessException(ExceptionCode.OPT_NOT_SUPPORT);
                    }
                    Stock stock = stockMap.get(t.getStockSn());
                    if (Objects.isNull(stock)) {
                        throw new BusinessException(ExceptionCode.GOODS_NOT_SUPPORT);
                    }
                    if (!stock.getGoodsId().equals(outWork.getGoodsId())) {
                        throw new BusinessException(ExceptionCode.GOODS_MODEL_MISMATCHING);
                    }
                    //填充商品id
                    outWork.setStockId(stock.getId());
                    return outWork;
                })
                .sorted(Comparator.comparing(BillStoreWorkPre::getId))
                .collect(Collectors.toList());


        //更新出库单
        outWorkList.forEach(t -> {
            BillStoreWorkPre up = new BillStoreWorkPre();
            up.setId(t.getId());
            up.setStockId(t.getStockId());
            if (!billStoreWorkPreService.update(up, Wrappers.<BillStoreWorkPre>lambdaUpdate()
                    .eq(BillStoreWorkPre::getId, t.getId())
                    .eq(BillStoreWorkPre::getWorkState, t.getWorkState()))) {
                throw new BusinessException(ExceptionCode.OPT_NOT_SUPPORT);
            }
        });

        StockStatusEnum.TransitionEnum transitionEnum;
        //调拨场景同时更新入库单

        switch (request.getScenario()) {
            case ALLOCATE:
                //调拨商品下架
                transitionEnum = StockStatusEnum.TransitionEnum.ALLOCATE;

                Map<String/*mateMark*/, BillStoreWorkPre> inWorkPreMap = billStoreWorkPreService.list(Wrappers.<BillStoreWorkPre>lambdaUpdate()
                                .eq(BillStoreWorkPre::getWorkType, StoreWorkTypeEnum.INT_STORE)
                                .eq(BillStoreWorkPre::getOriginSerialNo, request.getOriginSerialNo()))
                        .stream()
                        .collect(Collectors.toMap(BillStoreWorkPre::getMateMark, Function.identity()));

                //更新入库单
                outWorkList.forEach(t -> {
                    BillStoreWorkPre inWork = Objects.requireNonNull(inWorkPreMap.get(t.getMateMark()));
                    BillStoreWorkPre upIn = new BillStoreWorkPre();
                    upIn.setId(inWork.getId());
                    upIn.setStockId(t.getStockId());
                    billStoreWorkPreService.updateById(upIn);
                });
                break;
            case SALE:
                //销售商品下架
                transitionEnum = StockStatusEnum.TransitionEnum.SALE;
                break;

            default:
                throw new BusinessException(ExceptionCode.OPT_NOT_SUPPORT);
        }

        //修改商品状态
        stockService.updateStockStatus(outWorkList.stream().map(BillStoreWorkPre::getStockId).collect(Collectors.toList()), transitionEnum);

        //出库补充表身号事件
        billHandlerEventPublisher.publishEvent(new OutStorageSupplyStockEvent(outWorkList));

        return StoreWorkOutStorageSupplyStockResult.builder()
                .workIds(outWorkPreMap.values()
                        .stream()
                        .map(BillStoreWorkPre::getId)
                        .collect(Collectors.toList()))
                .build();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public StoreWorkOutStorageResult outStorage(StoreWorkOutStorageRequest request) {
        StoreWorkOutStorageResult result = billStoreWorkPreService.outStorage(request);
        List<BillStoreWorkPre> workPreList = billStoreWorkPreService.listByIds(result.getWorkIds());
        if (WhetherEnum.YES.getValue().intValue() == result.getNeedQt()) {
            //出库质检
            billQualityTestingService.create(workPreList.stream()
                    .map(t -> new QualityTestingCreateRequest()
                            .setQtSource(t.getWorkSource().getValue())
                            .setOriginSerialNo(t.getOriginSerialNo())
                            .setStoreWorkSerialNo(t.getSerialNo())
                            .setStockId(t.getStockId())
                            .setWorkId(t.getId())
                            .setCustomerId(t.getCustomerId())
                            .setCustomerContactId(t.getCustomerContactId()))
                    .collect(Collectors.toList()));
        }
        //发送出库事件
        billHandlerEventPublisher.publishEvent(new OutStorageEvent(workPreList));
        result.setStoreWorkCreateResultList(BillStoreWorkPreConvert.INSTANCE.convertStoreWorkWaitReceivingResult(workPreList));
        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public StoreWorkDeliveryResult logisticsDelivery(StoreWorkDeliveryRequest request) {
        //物流发货
        StoreWorkDeliveryResult result = billStoreWorkPreService.logisticsDelivery(request);
        List<BillStoreWorkPre> workPreList = billStoreWorkPreService.listByIds(result.getWorkIds());
        Assert.isTrue(request.isBatchDelivery() || workPreList.stream().map(BillStoreWorkPre::getWorkSource).distinct().count() == 1, "发货来源不唯一");

        if (workPreList.stream().anyMatch(t -> Objects.isNull(t.getStockId()))) {
            throw new OperationRejectedException(OperationExceptionCode.LOGISTICS_DELIVERY_STOCK_ID_NULL);
        }

        List<String> originSerialNoList = workPreList.stream()
                .map(BillStoreWorkPre::getOriginSerialNo)
                .distinct()
                .collect(Collectors.toList());
        //发货拦截
        wmsWorkInterceptService.checkIntercept(originSerialNoList);
        //查集单数据
        List<WmsWorkCollect> collectList = wmsWorkCollectService.list(Wrappers.<WmsWorkCollect>lambdaQuery()
                .in(WmsWorkCollect::getOriginSerialNo, originSerialNoList));

        //非集单入口，无法操作集单数据
        if (!request.isBatchDelivery() && CollectionUtils.isNotEmpty(collectList)) {
            throw new OperationRejectedException(OperationExceptionCode.WORK_COLLECT_DATA_OPT_ERROR);
        }

        //更新集单状态
        wmsWorkCollectService.updateCollectWorkState(collectList, WmsWorkCollectWorkStateEnum.TransitionEnum.DELIVERY);

        //商城回购退回监听发货信息
        recycleOrderService.checkIntercept(originSerialNoList);

        List<String> serialNoList = workPreList
                .stream()
                .map(BillStoreWorkPre::getOriginSerialNo)
                .filter(s -> s.startsWith("TOCXS"))
                .collect(Collectors.toList());
        //是门店发货 并且查出来是平台 抖音的才走这逻辑
        if (request.isShopDelivery() && CollectionUtils.isNotEmpty(serialNoList)) {
            billSaleOrderService.list(new LambdaQueryWrapper<BillSaleOrder>()
                            .in(BillSaleOrder::getSerialNo, serialNoList))
                    .stream()
                    .filter(t -> SaleOrderChannelEnum.DOU_YIN.equals(t.getSaleChannel()) && SaleOrderModeEnum.ON_LINE.equals(t.getSaleMode()))
                    .forEach(t -> {
                        for (String code : t.getBizOrderCode().split(",")) {
                            if (Objects.nonNull(refundCreatedService.getOne(new LambdaQueryWrapper<DouYinRefundCreated>()
                                    .eq(DouYinRefundCreated::getOrderId, code)
                                    .eq(DouYinRefundCreated::getAftersaleStatus, 6L)
                                    .eq(DouYinRefundCreated::getAftersaleType, 2L)))) {
                                throw new OperationRejectedException(OperationExceptionCode.DOU_YIN_ORDER_EXIST_REFUND);
                            }
                        }
                    });
        }
        //发送发货事件
        billHandlerEventPublisher.publishEvent(new LogisticsDeliveryEvent(workPreList
                , request.getDeliveryExpressNumber()
                , request.isShopDelivery()
                , false));
//                , FlywheelConstant._DF3_SHOP_ID == UserContext.getUser().getStore().getId()));

        result.setStoreWorkCreateResultList(BillStoreWorkPreConvert.INSTANCE.convertStoreWorkWaitReceivingResult(workPreList));

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(StoreWorkEditRequest request) {
        BillStoreWorkPre billStoreWorkPre = BillStoreWorkPreConvert.INSTANCE.convertBillStoreWorkPre(request);
        billStoreWorkPreService.updateById(billStoreWorkPre);

        BillStoreWorkPre workPre = billStoreWorkPreService.getById(request.getWorkId());

        if (ObjectUtils.isNotEmpty(workPre)) {
            //查集单数据
            List<WmsWorkCollect> collectList = wmsWorkCollectService.list(Wrappers.<WmsWorkCollect>lambdaQuery()
                    .in(WmsWorkCollect::getOriginSerialNo, Arrays.asList(workPre.getOriginSerialNo())));

            //更新集单状态
            wmsWorkCollectService.updateCollectWorkState(collectList, WmsWorkCollectWorkStateEnum.TransitionEnum.PRINT);
        }
    }

    @Override
    public boolean validateCanDoIfMallOrder(List<Integer> workIds) {
        if (workIds.size() == 1) {
            Integer workId = workIds.get(0);
            BillStoreWorkPre workPre = billStoreWorkPreService.getById(workId);
            if (workPre.getWorkSource() == BusinessBillTypeEnum.TO_C_XS) {
                BillSaleOrder billSaleOrder = billSaleOrderService.selectBySerialNo(workPre.getOriginSerialNo());
                if (billSaleOrder.getSaleMode() == SaleOrderModeEnum.DEPOSIT &&
                        billSaleOrder.getSaleChannel() == SaleOrderChannelEnum.XI_YI_SHOP) {
                    return billSaleOrder.getMallPayed();
                }
            }
        }
        return true;
    }


    /**
     * 入库上架商品
     *
     * @param workPreList
     * @param shopId
     * @param shopReceived
     * @param stockSnList  价格不符合数据集合
     */
    private void inStoragePutOnSale(List<BillStoreWorkPre> workPreList, Integer shopId, boolean shopReceived, List<String> stockSnList) {

        Map<Integer, BigDecimal> collectPrice = new HashMap<>();

        Map<Integer, BillFix> collectFix = new HashMap<>();

        if (!shopReceived) {
            //key stockId 维修内部倒叙
            TreeMap<Integer, List<BillFix>> map = billFixService.list(Wrappers.<BillFix>lambdaQuery()
                            .in(BillFix::getStockId, workPreList.stream().map(BillStoreWorkPre::getStockId).collect(Collectors.toList()))
                            .eq(BillFix::getFixState, FixStateEnum.NORMAL)
                    )
                    .stream().sorted(Comparator.comparing(BillFix::getId).reversed())
                    .collect(Collectors.groupingBy(billFix -> billFix.getStockId(), TreeMap::new, Collectors.toList()));

            map.forEach((k, v) -> {
                //新的计算
                if (CollectionUtils.isNotEmpty(v)) {
                    collectPrice.put(k, v.stream().map(BillFix::getFixMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
                    collectFix.put(k, v.get(FlywheelConstant.INDEX));
                }
            });
        }

        Store store = storeService.selectByShopId(shopId);

        workPreList.stream()
                .sorted(Comparator.comparing(BillStoreWorkPre::getStockId))
                .forEach(t -> {
                    Stock up = new Stock()
                            .setId(t.getStockId())
                            .setLocationId(shopId) //改变商品位置
                            .setStoreId(store.getId()) //改变仓库位置
                            .setRkTime(Boolean.FALSE.equals(shopReceived) && PURCHASE_TYPE.contains(t.getWorkSource()) ? new Date() : null)//采购入库时间
                            .setFixPrice(shopReceived ? null : Optional.ofNullable(collectPrice.get(t.getStockId())).orElse(BigDecimal.valueOf(0D)))//写入维修价
                            .setDefectOrNot(shopReceived ? null : Objects.nonNull(collectFix.get(t.getStockId())) ? collectFix.get(t.getStockId()).getDefectOrNot() : 0)//是否有瑕疵
                            .setDefectDescription(shopReceived ? null : Objects.nonNull(collectFix.get(t.getStockId())) ? collectFix.get(t.getStockId()).getDefectDescription() : StringUtils.EMPTY);

                    stockService.updateById(up);
                });

        /**
         * 1、入库后的寄售价 大于 B价时候，系统自动更新B价 = 寄售价；同时记录更新的原因是对应的维修原因。
         *
         * 2、预警通知场景：提示价格预警通知品牌
         * 2.1 寄售价大于C价。直接更新为待定价重新定价
         *
         * 维修价此时才写 可能未计算 后置才能计算准确的寄售价 不能提前
         */
        selectStock(shopReceived, collectFix, stockSnList, workPreList);
    }

    /**
     * 保存日志
     *
     * @param stock
     * @param modeType 类型 1 变更 2 通知
     * @param modeMsg  内容
     */
    private void save(Stock stock, Integer modeType, String modeMsg) {
        LogPriceOpt logPriceOpt = new LogPriceOpt();
        logPriceOpt.setStockId(stock.getId());
        logPriceOpt.setModeType(modeType);
        logPriceOpt.setModeMsg(modeMsg);
        logPriceOpt.setConsignPrice(stock.getConsignmentPrice());
        logPriceOpt.setBPrice(stock.getTobPrice());
        logPriceOpt.setCPrice(stock.getTocPrice());

        logPriceOptService.save(logPriceOpt);
    }

    private void selectStock(boolean shopReceived, Map<Integer, BillFix> collectFix, List<String> stockSnList, List<BillStoreWorkPre> workPreList) {
        try {
            if (shopReceived) {
                return;
            }
            //前提已经定过价 才存在b价
            for (Stock stock : stockService.getConsignmentPrice(workPreList.stream().filter(billStoreWorkPre -> PURCHASE_TYPE.contains(billStoreWorkPre.getWorkSource())).map(BillStoreWorkPre::getStockId).collect(Collectors.toList()))) {
                if (ObjectUtils.isNotEmpty(stock.getTobPrice()) && ObjectUtils.isNotEmpty(stock.getTocPrice()) && stock.getTobPrice().compareTo(stock.getTocPrice()) < 0) {
                    Boolean b = ImmutableRangeMap.<Comparable<BigDecimal>, Boolean>builder()
                            .put(Range.open(stock.getTobPrice(), stock.getTocPrice()), true)
                            .put(Range.atLeast(stock.getTocPrice()), false)
                            .build().get(stock.getConsignmentPrice());

                    if (ObjectUtils.isNotEmpty(b) && b) {
                        //变更
                        //记录
                        save(stock, 1, ObjectUtils.isEmpty(collectFix.get(stock.getId())) ? "未知" : collectFix.get(stock.getId()).getRemark());
                    } else if (ObjectUtils.isNotEmpty(b) && !b) {
                        //通知
                        stockSnList.add(stock.getSn());
                        //记录 警告
                        save(stock, 2, "警告");
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 同一型号的新表校验
     *
     * @param stockId
     */
    private StoreWorkReceivedListResult.PriceMessage selectNewStock(Integer stockId) {
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
//                .map(stockCPrice -> stockCPrice.getStockSn() + "(2c价:" +
//                        (ObjectUtils.isEmpty(stockCPrice.getTocPrice()) ? "未定价" : (stockCPrice.getTocPrice() + "元,吊牌价:")) +
//                        (ObjectUtils.isEmpty(stockCPrice.getTagPrice()) ? "未定价" : stockCPrice.getTagPrice() + "元)"))
//                .collect(Collectors.toList());
//        List<String> localColl = stockCPriceList.stream().filter(stockCPrice -> stockId.equals(stockCPrice.getStockId()))
//                .map(stockCPrice -> stockCPrice.getStockSn() + "(2c价:" +
//                        (ObjectUtils.isEmpty(stockCPrice.getTocPrice()) ? "未定价" : (stockCPrice.getTocPrice() + "元,吊牌价:")) +
//                        (ObjectUtils.isEmpty(stockCPrice.getTagPrice()) ? "未定价" : stockCPrice.getTagPrice() + "元)"))
//                .collect(Collectors.toList());
//        localColl.addAll(otherColl);

        return StoreWorkReceivedListResult.PriceMessage.builder()
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

    private StoreWorkInStorageListResult.PriceMessage selectNewStock2(Integer stockId) {
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
//                .map(stockCPrice -> stockCPrice.getStockSn() + "(2c价:" +
//                        (ObjectUtils.isEmpty(stockCPrice.getTocPrice()) ? "未定价" : (stockCPrice.getTocPrice() + "元,吊牌价:")) +
//                        (ObjectUtils.isEmpty(stockCPrice.getTagPrice()) ? "未定价" : stockCPrice.getTagPrice() + "元)"))
//                .collect(Collectors.toList());
//        List<String> localColl = stockCPriceList.stream().filter(stockCPrice -> stockId.equals(stockCPrice.getStockId()))
//                .map(stockCPrice -> stockCPrice.getStockSn() + "(2c价:" +
//                        (ObjectUtils.isEmpty(stockCPrice.getTocPrice()) ? "未定价" : (stockCPrice.getTocPrice() + "元,吊牌价:")) +
//                        (ObjectUtils.isEmpty(stockCPrice.getTagPrice()) ? "未定价" : stockCPrice.getTagPrice() + "元)"))
//                .collect(Collectors.toList());
//        localColl.addAll(otherColl);

        return StoreWorkInStorageListResult.PriceMessage.builder()
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

}
