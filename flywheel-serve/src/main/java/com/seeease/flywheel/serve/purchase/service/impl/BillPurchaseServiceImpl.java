package com.seeease.flywheel.serve.purchase.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableSet;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentCancelRequest;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentCheckoutStockSnRequest;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.goods.request.SelectInsertPurchaseLineRequest;
import com.seeease.flywheel.goods.request.SelectInsertPurchaseRequest;
import com.seeease.flywheel.goods.request.StockListRequest;
import com.seeease.flywheel.purchase.request.*;
import com.seeease.flywheel.purchase.result.*;
import com.seeease.flywheel.serve.base.*;
import com.seeease.flywheel.serve.customer.mapper.CustomerContactsMapper;
import com.seeease.flywheel.serve.dict.entity.DictData;
import com.seeease.flywheel.serve.dict.entity.StockDict;
import com.seeease.flywheel.serve.dict.mapper.DictDataMapper;
import com.seeease.flywheel.serve.dict.mapper.StockDictMapper;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.enums.ReceiptPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.mapper.AccountsPayableAccountingMapper;
import com.seeease.flywheel.serve.financial.service.ApplyFinancialPaymentService;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.enums.SeriesTypeEnum;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.enums.StockUndersellingEnum;
import com.seeease.flywheel.serve.goods.mapper.SeriesMapper;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.maindata.mapper.PurchaseSubjectMapper;
import com.seeease.flywheel.serve.purchase.convert.PurchaseConverter;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseTask;
import com.seeease.flywheel.serve.purchase.enums.*;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseLineMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseMapper;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseTaskService;
import com.seeease.flywheel.serve.recycle.domain.RecycleDomain;
import com.seeease.flywheel.serve.recycle.entity.MallRecyclingOrder;
import com.seeease.flywheel.serve.recycle.enums.RecycleStateEnum;
import com.seeease.flywheel.serve.recycle.mapper.RecycleOrderMapper;
import com.seeease.flywheel.serve.recycle.service.IRecycleOrderService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderStateEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.exception.e.BusinessException;
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
 * @author Tiro
 * @description 针对表【bill_purchase】的数据库操作Service实现
 * @createDate 2023-01-07 17:25:43
 */
@Service
@Slf4j
public class BillPurchaseServiceImpl extends ServiceImpl<BillPurchaseMapper, BillPurchase>
        implements BillPurchaseService {

    private static final Set<BusinessBillTypeEnum> SCOPE_BUSINESS = ImmutableSet.of(BusinessBillTypeEnum.GR_JS, BusinessBillTypeEnum.GR_HG_JHS, BusinessBillTypeEnum.GR_HG_ZH);

    private static final Set<BusinessBillTypeEnum> SCOPE_BUSINESS_BY_PAYMENT = ImmutableSet.of(BusinessBillTypeEnum.TH_CG_DJ, BusinessBillTypeEnum.TH_CG_BH, BusinessBillTypeEnum.GR_HS_ZH);

    @Resource
    private BillPurchaseLineMapper billPurchaseLineMapper;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private DictDataMapper dictDataMapper;

    @Resource
    private StockDictMapper stockDictMapper;

    @Resource
    private CustomerContactsMapper customerContactsMapper;

    @Resource
    private PurchaseSubjectMapper purchaseSubjectMapper;

    @Resource
    private ApplyFinancialPaymentService applyFinancialPaymentService;
    @Resource
    private RecycleDomain recycleDomain;
    @Resource
    private BillSaleOrderService billSaleOrderService;
    @Resource
    private BillSaleOrderLineService billSaleOrderLineService;
    @Resource
    private IRecycleOrderService recycleOrderService;
    @Resource
    private RecycleOrderMapper recycleOrderMapper;

    @Resource
    private BillPurchaseTaskService billPurchaseTaskService;
    @Resource
    private SeriesMapper seriesMapper;
    @Resource
    private GoodsWatchService goodsWatchService;

    @Resource
    private AccountsPayableAccountingMapper accountsPayableAccountingMapper;

    @Override
    public Page<BillPurchase> listByRequest(PurchaseListRequest request) {
        return baseMapper.listByRequest(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseCreateListResult create(PurchaseCreateRequest dto) {
        BillPurchase billPurchase = PurchaseConverter.INSTANCE.convert(dto);
        if (StringUtils.isNotEmpty(billPurchase.getConsignmentTime()) && StringUtils.isEmpty(billPurchase.getDealBeginTime())) {
            billPurchase.setDealBeginTime(DateUtil.format(new Date(), DatePattern.NORM_DATE_PATTERN));
        }
        //采购状态
        billPurchase.setPurchaseState(BusinessBillStateEnum.UNCONFIRMED);
        //采购数量
        billPurchase.setPurchaseNumber(dto.getDetails().size());
        billPurchase.setStoreTag(dto.getStoreId().intValue() == 1 ? WhetherEnum.NO : WhetherEnum.YES);
        //新增采购单
        baseMapper.insert(billPurchase);

        //采购行集合
        List<PurchaseDetailsResult.PurchaseLineVO> line = new ArrayList<>();

        Map<Integer, WatchDataFusion> fusionMap = goodsWatchService.getWatchDataFusionListByGoodsIds(dto.getDetails()
                        .stream()
                        .map(PurchaseCreateRequest.BillPurchaseLineDto::getGoodsId)
                        .collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));

        List<BillPurchaseLine> billPurchaseLines = dto.getDetails().stream().map(PurchaseConverter.INSTANCE::convert)
                .peek(t -> {

                    t.setPurchaseId(billPurchase.getId());//采购单id
                    t.setPurchaseLineState(PurchaseLineStateEnum.TO_BE_CONFIRMED);//采购行状态
                    t.setStockSn(t.getStockSn().trim());
                    if (fusionMap.containsKey(t.getGoodsId())) {
                        WatchDataFusion fusion = fusionMap.get(t.getGoodsId());
                        if (SeriesTypeEnum.BAGS.getValue().equals(fusion.getSeriesType())) {
                            t.setWno(WNOUtil.generateWNOB());
                        } else if (SeriesTypeEnum.ORNAMENT.getValue().equals(fusion.getSeriesType())) {
                            t.setWno(WNOUtil.generateWNOJ());
                        } else {
                            t.setWno(WNOUtil.generateWNO());
                        }
                    } else {
                        t.setWno(WNOUtil.generateWNO());
                    }

                    line.add(PurchaseConverter.INSTANCE.convertPurchaseLineVO(t));
                })
                .collect(Collectors.toList());
        //新增采购详情
        billPurchaseLineMapper.insertBatchSomeColumn(billPurchaseLines);

        if (ObjectUtils.isNotEmpty(dto.getApplyPaymentSerialNo())) {
            applyFinancialPaymentService.cancel(ApplyFinancialPaymentCancelRequest.builder()
                    .serialNo(dto.getApplyPaymentSerialNo())
                    .useScenario(ApplyFinancialPaymentCancelRequest.UseScenario.PURCHASE_BINDING)
                    .build());
        }
        PurchaseCreateListResult purchaseCreateResult = PurchaseConverter.INSTANCE.convertPurchaseCreateResult(billPurchase);
        if (ObjectUtils.isNotEmpty(dto.getPurchaseTaskId())) {

            BillPurchaseTask billPurchaseTask = new BillPurchaseTask();
            billPurchaseTask.setPurchaseId(billPurchase.getId());
            billPurchaseTask.setId(dto.getPurchaseTaskId());
            billPurchaseTask.setTransitionStateEnum(TaskStateEnum.TransitionEnum.IN_STORAGE);
            billPurchaseTaskService.updateByState(billPurchaseTask);

            BillPurchaseTask purchaseTask = billPurchaseTaskService.getById(dto.getPurchaseTaskId());

            if (ObjectUtils.isNotEmpty(purchaseTask)) {
                purchaseCreateResult.setPurchaseTaskVO(PurchaseCreateListResult.PurchaseTaskVO.builder().serialNo(purchaseTask.getSerialNo()).build());
            }
        }

        purchaseCreateResult.setLine(line);

        return purchaseCreateResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseExpressNumberUploadListResult uploadExpressNumber(PurchaseExpressNumberUploadRequest dto) {
        //修改采购单状态
        BillPurchase upBillPurchase = new BillPurchase();
        upBillPurchase.setId(dto.getPurchaseId());
        upBillPurchase.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.UNCONFIRMED_TO_UNDER_WAY);
        upBillPurchase.setExpressNumber(dto.getExpressNumber());
        UpdateByIdCheckState.update(baseMapper, upBillPurchase);

        BillPurchase purchase = baseMapper.selectById(dto.getPurchaseId());

        //查采购详情
        List<BillPurchaseLine> lines = billPurchaseLineMapper.selectList(Wrappers.<BillPurchaseLine>lambdaQuery()
                .eq(BillPurchaseLine::getPurchaseId, dto.getPurchaseId()));

        List<DictData> dataList = dictDataMapper.selectList(Wrappers.<DictData>lambdaQuery().likeRight(DictData::getDictType, "stock_"));

        //创建库存
        List<Stock> stockList = lines.stream()
                .map(t -> {
                    Stock stock = PurchaseConverter.INSTANCE.convertStock(t);
                    stock.setStockStatus(StockStatusEnum.WAIT_RECEIVED);
//                    stock.setStockStatus(StockStatusEnum.PURCHASE_IN_TRANSIT);
                    stock.setSourceSubjectId(purchase.getPurchaseSubjectId());
                    stock.setBelongId(purchase.getPurchaseSubjectId());
                    stock.setStockSrc(purchase.getPurchaseSource().getValue());
//                    stock.setConsignmentPrice(t.getConsignmentPrice());
                    //客户id
                    stock.setCcId(purchase.getCustomerId());
                    //是否允许低于b价销售
                    stock.setIsUnderselling(StockUndersellingEnum.NOT_ALLOW);
                    stock.setTotalPrice(t.getPurchasePrice()); //初始总成本为采购成本
                    stock.setUseConfig(0);
                    stock.setLockDemand(0);
                    stock.setIsRecycling(0);
                    stock.setDefectOrNot(0);
                    if (Objects.isNull(stock.getSalesPriority())) {
                        stock.setSalesPriority(SalesPriorityEnum.TOB_C.getValue()); //采购去除必填后默认bc可销
                    }

                    if (purchase.getPurchaseSource() == BusinessBillTypeEnum.GR_JS) {
                        stock.setTagPrice(t.getDealPrice());
                        stock.setTobPrice(t.getDealPrice());
                        stock.setTocPrice(t.getDealPrice());
                    }
                    stock.setRightOfManagement(FlywheelConstant._ZB_RIGHT_OF_MANAGEMENT);
                    stock.setWno(t.getWno());

                    stock.setDemandId(ObjectUtils.isEmpty(purchase.getDemanderStoreId()) ? null : purchase.getDemanderStoreId());
                    //5.25需求 锁门店库存
                    if (purchase.getPurchaseSource() == BusinessBillTypeEnum.TH_CG_DJ) {
                        stock.setLockDemand(ObjectUtils.isEmpty(purchase.getDemanderStoreId()) ? null : purchase.getDemanderStoreId());
                    }

                    stock.setAttachment(convert(dataList, t.getAttachmentList(), t.getIsCard(), t.getWarrantyDate(), stock.getGoodsId()));
                    System.out.println("22222222222222222222222222");
                    stock.setNewSettlePrice(stock.getConsignmentPrice());
                    return stock;
                })
                .peek(t -> t.setId(null))
                .collect(Collectors.toList());

        //新增库存信息
        stockMapper.insertBatchSomeColumn(stockList);

        Map<String, Integer> stockMap = stockList.stream().collect(Collectors.toMap(Stock::getSn, Stock::getId));

        List<StockDict> stockDictList = lines.stream()
                .filter(t -> CollectionUtils.isNotEmpty(t.getAttachmentList()))
                .map(t -> t.getAttachmentList()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(a -> {
                            StockDict stockDict = new StockDict();
                            stockDict.setStockId(Objects.requireNonNull(stockMap.get(t.getStockSn())));
                            stockDict.setDictId(a.longValue());
                            return stockDict;
                        })
                        .collect(Collectors.toList()))

                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(stockDictList)) {
            //新增库存附件
            stockDictMapper.insertBatchSomeColumn(stockDictList);
        }

        //更新采购行
        lines.forEach(t -> {
            BillPurchaseLine upBillPurchaseLine = new BillPurchaseLine();
            upBillPurchaseLine.setStockId(Objects.requireNonNull(stockMap.get(t.getStockSn()))); //设置库存id
            upBillPurchaseLine.setId(t.getId());
            upBillPurchaseLine.setPurchaseLineState(PurchaseLineStateEnum.CUSTOMER_HAS_SHIPPED);
            billPurchaseLineMapper.updateById(upBillPurchaseLine);
        });

        return PurchaseConverter.INSTANCE.convertPurchaseExpressNumberUploadResult(purchase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseCancelResult cancel(PurchaseCancelRequest dto) {

        BillPurchase billPurchase = baseMapper.selectById(dto.getPurchaseId());

        Assert.isFalse(ObjectUtils.isEmpty(billPurchase) || billPurchase.getPurchaseState() != BusinessBillStateEnum.UNCONFIRMED, "不符合采购取消条件");

        //取消还是变成未使用 false 取消 true 未使用
        Boolean b = false;

        if (ObjectUtils.isNotEmpty(billPurchase.getApplyPaymentSerialNo())) {
            switch (billPurchase.getPurchaseSource()) {
                case TH_CG_DJ:
                case TH_CG_BH:
                case TH_CG_QK:
                case TH_CG_DJTP:
//                case TH_CG_PL:
                    //前置
                    if (Objects.nonNull(billPurchase.getPrePayment()) && billPurchase.getPrePayment().intValue() == 1) {
                        applyFinancialPaymentService.cancel(ApplyFinancialPaymentCancelRequest.builder()
                                .serialNo(billPurchase.getApplyPaymentSerialNo())
                                .useScenario(ApplyFinancialPaymentCancelRequest.UseScenario.PURCHASE_UNBIND)
                                .build());
                        b = true;
                        break;
                    } else {
                        applyFinancialPaymentService.cancel(ApplyFinancialPaymentCancelRequest.builder()
                                .serialNo(billPurchase.getApplyPaymentSerialNo())
                                .useScenario(ApplyFinancialPaymentCancelRequest.UseScenario.PURCHASE_PEER_CANCEL)
                                .build());

                        break;
                    }
                case GR_HS_JHS:
                case GR_HS_ZH:
                    applyFinancialPaymentService.cancel(ApplyFinancialPaymentCancelRequest.builder()
                            .serialNo(billPurchase.getApplyPaymentSerialNo())
                            .useScenario(ApplyFinancialPaymentCancelRequest.UseScenario.RECYCLE_PERSON_CANCEL)
                            .build());
                    b = true;
                    break;
            }
        }

        //修改采购单状态
        BillPurchase upBillPurchase = new BillPurchase();
        upBillPurchase.setId(dto.getPurchaseId());
        upBillPurchase.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.UNCONFIRMED_TO_CANCEL_WHOLE);
        if (b) {
            upBillPurchase.setApplyPaymentSerialNo("");
        }
        UpdateByIdCheckState.update(baseMapper, upBillPurchase);
        //查采购详情
        List<BillPurchaseLine> lines = billPurchaseLineMapper.selectList(Wrappers.<BillPurchaseLine>lambdaQuery()
                .eq(BillPurchaseLine::getPurchaseId, dto.getPurchaseId()));
        //更新采购行
        lines.forEach(t -> {
            BillPurchaseLine upBillPurchaseLine = new BillPurchaseLine();
            upBillPurchaseLine.setId(t.getId());
            upBillPurchaseLine.setPurchaseLineState(PurchaseLineStateEnum.ORDER_CANCEL_WHOLE);
            billPurchaseLineMapper.updateById(upBillPurchaseLine);

        });

        PurchaseCancelResult build = PurchaseCancelResult.builder()
                .serialNo(billPurchase.getSerialNo())
                .line(lines.stream().map(billPurchaseLine -> {
                    PurchaseDetailsResult.PurchaseLineVO purchaseLineVO = new PurchaseDetailsResult.PurchaseLineVO();
                    purchaseLineVO.setWno(billPurchaseLine.getWno());

                    return purchaseLineVO;
                }).collect(Collectors.toList()))
                .build();
        //采购订单取消 通知采购需求取消 todo
        //修改对应申请打款单为取消 2023-06-08
        //采购单取消判断是否需要取消销售单
        MallRecyclingOrder recyclingOrder = recycleDomain.asserter.assertRecycleByPurchaseId(dto.getPurchaseId());
        //如果有销售单就取消销售单并且回购记录变成已取消
        if (Objects.nonNull(recyclingOrder)) {
            if (recyclingOrder.getSaleId() != null) {
                BillSaleOrder billSaleOrder = new BillSaleOrder();
                billSaleOrder.setId(recyclingOrder.getSaleId());
                billSaleOrder.setSaleState(SaleOrderStateEnum.CANCEL_WHOLE);
                billSaleOrderService.updateById(billSaleOrder);
                log.info("修改销售单状态:{}", billSaleOrder);

                //需要改变商品状态,先查询销售行信息在进行取消
                LambdaQueryWrapper<BillSaleOrderLine> billSaleOrderLineLambdaQueryWrapper = new LambdaQueryWrapper<>();
                billSaleOrderLineLambdaQueryWrapper.eq(BillSaleOrderLine::getSaleId, recyclingOrder.getSaleId());
                List<BillSaleOrderLine> list = billSaleOrderLineService.list(billSaleOrderLineLambdaQueryWrapper);
                if (CollectionUtil.isNotEmpty(list)) {
                    list.forEach(line -> {
                        //取消行状态
                        BillSaleOrderLine billSaleOrderLineUpdate = new BillSaleOrderLine();
                        billSaleOrderLineUpdate.setId(line.getId());
                        billSaleOrderLineUpdate.setTransitionStateEnum(SaleOrderLineStateEnum.TransitionEnum.WAIT_OUT_STORAGE_TO_CANCEL_WHOLE);
                        billSaleOrderLineService.updateState(billSaleOrderLineUpdate);
                        //取消商品状态
                        Stock stockUpdate = new Stock();
                        stockUpdate.setId(line.getStockId());
                        stockUpdate.setTransitionStateEnum(StockStatusEnum.TransitionEnum.SOLD_OUT_MARKETABLE);
                        UpdateByIdCheckState.update(stockMapper, stockUpdate);
                    });
                }

            }
            //去取消回购记录
            if (recyclingOrder.getState() == RecycleStateEnum.WAY_OFFER) {
                recycleOrderService.updateRecycleStatus(new MallRecyclingOrder().setId(recyclingOrder.getId()).setTransitionStateEnum(RecycleStateEnum.TransitionEnum.WAY_OFFER_PURCEASE_CANCEL));
            } else if (recyclingOrder.getState() == RecycleStateEnum.COMPLETE) {
                recycleOrderService.updateRecycleStatus(new MallRecyclingOrder().setId(recyclingOrder.getId()).setTransitionStateEnum(RecycleStateEnum.TransitionEnum.PURCEASE_CANCEL));
            } else if (recyclingOrder.getState() == RecycleStateEnum.WAIT_UPLOAD_CUSTOMER) {
                recycleOrderService.updateRecycleStatus(new MallRecyclingOrder().setId(recyclingOrder.getId()).setTransitionStateEnum(RecycleStateEnum.TransitionEnum.WAIT_UPLOAD_CUSTOMER_CANCEL));
            } else if (recyclingOrder.getState() == RecycleStateEnum.MAKE_ORDER) {
                recycleOrderService.updateRecycleStatus(new MallRecyclingOrder().setId(recyclingOrder.getId()).setTransitionStateEnum(RecycleStateEnum.TransitionEnum.MAKE_ORDER_CANCEL));
            }

        }

        return build;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseEditResult edit(PurchaseEditRequest request) {
        BillPurchase upBillPurchase = PurchaseConverter.INSTANCE.convert(request);

        if (1 != baseMapper.updateById(upBillPurchase)) {
            throw new BusinessException(ExceptionCode.PURCHASE_EDIT_FAIL);
        }

        request.getDetails().forEach(t -> {
            BillPurchaseLine upBillPurchaseLine = PurchaseConverter.INSTANCE.convert(t);
            if (1 != billPurchaseLineMapper.updateById(upBillPurchaseLine)) {
                throw new BusinessException(ExceptionCode.PURCHASE_EDIT_FAIL);
            }
        });

        return PurchaseEditResult.builder().id(request.getId()).build();
    }

    @Override
    public PurchaseExpressNumberUploadListResult shopReceiving(PurchaseExpressNumberUploadRequest request) {

        BillPurchase purchase = baseMapper.selectById(request.getPurchaseId());

        PurchaseExpressNumberUploadListResult uploadResult = PurchaseConverter.INSTANCE.convertPurchaseExpressNumberUploadResult(purchase);

        uploadResult.setSerialNo(purchase.getSerialNo());
        StoreWorkCreateResult storeWorkCreateResult = new StoreWorkCreateResult();
        storeWorkCreateResult.setStockId(billPurchaseLineMapper.selectList(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getPurchaseId, request.getPurchaseId())).get(0).getStockId());
        uploadResult.setStoreWorkList(Arrays.asList(storeWorkCreateResult));

        return uploadResult;
    }

    @Override
    public PurchaseExpressNumberUploadListResult confirmReturn(PurchaseExpressNumberUploadRequest request) {

        BillPurchase purchase = baseMapper.selectById(request.getPurchaseId());

        PurchaseExpressNumberUploadListResult uploadResult = PurchaseConverter.INSTANCE.convertPurchaseExpressNumberUploadResult(purchase);
        uploadResult.setSerialNo(purchase.getSerialNo());
        StoreWorkCreateResult storeWorkCreateResult = new StoreWorkCreateResult();
        storeWorkCreateResult.setStockId(billPurchaseLineMapper.selectList(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getPurchaseId, request.getPurchaseId())).get(0).getStockId());
        uploadResult.setStoreWorkList(Arrays.asList(storeWorkCreateResult));

        return uploadResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseApplySettlementResult applySettlement(PurchaseApplySettlementRequest request) {

        BillPurchase billPurchase = baseMapper.selectById(request.getPurchaseId());

        MallRecyclingOrder mallRecyclingOrder = recycleOrderService.list(Wrappers.<MallRecyclingOrder>lambdaQuery().eq(MallRecyclingOrder::getPurchaseId, request.getPurchaseId())).stream().findAny().orElse(null);
        if (ObjectUtils.isNotEmpty(mallRecyclingOrder) && mallRecyclingOrder.getState() == RecycleStateEnum.COMPLETE && mallRecyclingOrder.getBalance().signum() < 0) {
            request.setAccountName(billPurchase.getAccountName());
            request.setBankAccount(billPurchase.getBankAccount());
            request.setBankCustomerName(billPurchase.getBankCustomerName());
            request.setBank(billPurchase.getBank());
        } else if (ObjectUtils.isNotEmpty(mallRecyclingOrder) && mallRecyclingOrder.getState() == RecycleStateEnum.COMPLETE && mallRecyclingOrder.getBalance().signum() >= 0) {
//            throw new OperationRejectedException(OperationExceptionCode.MALL_CLIENT_CLIENT_PAY);
            recycleOrderService.checkIntercept(billPurchase.getSerialNo());
        }

//        else if (ObjectUtils.isNotEmpty(mallRecyclingOrder) && mallRecyclingOrder.getState() != RecycleStateEnum.COMPLETE) {
//            throw new OperationRejectedException(OperationExceptionCode.MALL_CLIENT_NO_UPLOAD);
//        }

        //可以结算有哪些流程
        Assert.isTrue(ObjectUtils.isNotEmpty(billPurchase) && SCOPE_BUSINESS.contains(billPurchase.getPurchaseSource()) && billPurchase.getIsSettlement() == WhetherEnum.YES, "非个人寄售,回购不能结算");

        BillPurchaseLine billPurchaseLine = billPurchaseLineMapper.selectOne(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getPurchaseId, request.getPurchaseId()));

        Assert.notNull(billPurchaseLine, "采购单行不存在");

        PurchaseApplySettlementResult purchaseApplySettlementResult = new PurchaseApplySettlementResult();
        Stock stock = stockMapper.selectById(billPurchaseLine.getStockId());

        //打款金额 个人寄售
        if (billPurchase.getPurchaseType() == PurchaseTypeEnum.GR_JS) {

            Assert.isTrue(stock.getStockStatus() == StockStatusEnum.SOLD_OUT, "表未售出");
            //断言是否生成的应收应付了没有
            List<AccountsPayableAccounting> payableAccountingList = accountsPayableAccountingMapper.selectList(Wrappers.<AccountsPayableAccounting>lambdaQuery()
                    .eq(AccountsPayableAccounting::getType, ReceiptPaymentTypeEnum.AMOUNT_PAYABLE)
                    .eq(AccountsPayableAccounting::getStockId, billPurchaseLine.getStockId())
                    .eq(AccountsPayableAccounting::getStockSn, billPurchaseLine.getStockSn())
            );

            if (DateUtil.between(DateUtil.parse("2024-03-01 00:00:00"), billPurchase.getCreatedTime(), DateUnit.SECOND, false) >= 0) {
                Assert.isTrue(Objects.nonNull(payableAccountingList) && CollectionUtils.isNotEmpty(payableAccountingList), "未创建应收应付");
            }


            purchaseApplySettlementResult.setTypePayment(ApplyFinancialPaymentTypeEnum.SEND_PERSON.getValue());
            if (request.getSettlementPrice() != null && request.getSettlementPrice().compareTo(BigDecimal.ZERO) != 0) {
                purchaseApplySettlementResult.setPricePayment(request.getSettlementPrice());
            } else {
                purchaseApplySettlementResult.setPricePayment(billPurchaseLine.getPurchasePrice().subtract(ObjectUtils.isEmpty(billPurchaseLine.getFixPrice()) ? BigDecimal.ZERO : billPurchaseLine.getFixPrice()));
            }
            if (CollectionUtil.isNotEmpty(request.getOtherPicture())) {
                String collect = request.getOtherPicture().stream().collect(Collectors.joining(","));
                purchaseApplySettlementResult.setRecoveryPricingRecord(collect);
            }
            purchaseApplySettlementResult.setDemanderStoreId(billPurchase.getDemanderStoreId());
            purchaseApplySettlementResult.setFrontIdentityCard(CollectionUtils.isEmpty(request.getFrontIdentityCard()) ? StringUtils.EMPTY : request.getFrontIdentityCard().get(0));
            purchaseApplySettlementResult.setReverseIdentityCard(CollectionUtils.isEmpty(request.getReverseIdentityCard()) ? StringUtils.EMPTY : request.getReverseIdentityCard().get(0));
            purchaseApplySettlementResult.setRecoveryPricingRecord(CollectionUtils.isEmpty(request.getRecoveryPricingRecord()) ? CollectionUtils.isEmpty(request.getRecoveryPricingRecord()) ? StringUtils.EMPTY : request.getRecoveryPricingRecord().get(0) : request.getRecoveryPricingRecord().get(0));
            purchaseApplySettlementResult.setBatchPictureUrl(CollectionUtils.isEmpty(request.getOtherPicture()) ? null : request.getOtherPicture());
        } else if (billPurchase.getPurchaseType() == PurchaseTypeEnum.GR_HG) {
            purchaseApplySettlementResult.setTypePayment(ApplyFinancialPaymentTypeEnum.BUY_BACK.getValue());
            //针对于单 还是表
//            purchaseApplySettlementResult.setPricePayment(billPurchase.getPurchaseSource() == BusinessBillTypeEnum.GR_HG_JHS ? billPurchaseLine.getReferenceBuyBackPrice().subtract(billPurchaseLine.getPlanFixPrice()).subtract(billPurchaseLine.getWatchbandReplacePrice()) : billPurchaseLine.getReferenceBuyBackPrice().subtract(billPurchaseLine.getPlanFixPrice()).subtract(billPurchaseLine.getWatchbandReplacePrice()).subtract(billPurchase.getSalePrice()));
            purchaseApplySettlementResult.setPricePayment(billPurchase.getPurchaseSource() == BusinessBillTypeEnum.GR_HG_JHS ? billPurchaseLine.getReferenceBuyBackPrice().subtract(request.getPlanFixPrice()).subtract(billPurchaseLine.getWatchbandReplacePrice()) : billPurchaseLine.getReferenceBuyBackPrice().subtract(request.getPlanFixPrice()).subtract(billPurchaseLine.getWatchbandReplacePrice()).subtract(billPurchase.getSalePrice()));
            //结算金额
            purchaseApplySettlementResult.setSettlementPrice(billPurchaseLine.getReferenceBuyBackPrice().subtract(request.getPlanFixPrice()).subtract(request.getWatchbandReplacePrice()));
            purchaseApplySettlementResult.setFrontIdentityCard(CollectionUtils.isEmpty(request.getFrontIdentityCard()) ? StringUtils.EMPTY : request.getFrontIdentityCard().get(0));
            purchaseApplySettlementResult.setReverseIdentityCard(CollectionUtils.isEmpty(request.getReverseIdentityCard()) ? StringUtils.EMPTY : request.getReverseIdentityCard().get(0));
            purchaseApplySettlementResult.setRecoveryPricingRecord(CollectionUtils.isEmpty(request.getRecoveryPricingRecord()) ? CollectionUtils.isEmpty(request.getBuyBackTransfer()) ? StringUtils.EMPTY : request.getBuyBackTransfer().get(0) : request.getRecoveryPricingRecord().get(0));
            purchaseApplySettlementResult.setAgreementTransfer(CollectionUtils.isEmpty(request.getAgreementTransfer()) ? StringUtils.EMPTY : request.getAgreementTransfer().get(0));
            purchaseApplySettlementResult.setBuyBackTransfer(CollectionUtils.isEmpty(request.getBuyBackTransfer()) ? StringUtils.EMPTY : request.getBuyBackTransfer().get(0));
        }

        purchaseApplySettlementResult.setSubjectPayment(billPurchase.getPurchaseSubjectId());

        purchaseApplySettlementResult.setCustomerName(customerContactsMapper.selectById(billPurchase.getCustomerContactId()).getName());

        purchaseApplySettlementResult.setStockId(billPurchaseLine.getStockId());
        purchaseApplySettlementResult.setPurchaseSource(billPurchase.getPurchaseSource().getValue());
        purchaseApplySettlementResult.setPurchaseId(billPurchase.getId());
        purchaseApplySettlementResult.setSerialNo(billPurchase.getSerialNo());
        purchaseApplySettlementResult.setSalePrice(billPurchase.getSalePrice());
        purchaseApplySettlementResult.setTotalPurchasePrice(billPurchase.getTotalPurchasePrice());
        purchaseApplySettlementResult.setStockSn(stock.getSn());
        purchaseApplySettlementResult.setPurchaseMode(billPurchase.getPurchaseMode().getValue());
        purchaseApplySettlementResult.setLineId(billPurchaseLine.getId());
        purchaseApplySettlementResult.setTime(billPurchase.getCreatedTime());

        if (ObjectUtils.isNotEmpty(mallRecyclingOrder) && mallRecyclingOrder.getState() == RecycleStateEnum.WAIT_UPLOAD_CUSTOMER && mallRecyclingOrder.getBalance().signum() < 0) {
            MallRecyclingOrder recyclingOrder = new MallRecyclingOrder();
            recyclingOrder.setId(mallRecyclingOrder.getId());
            recyclingOrder.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.WAIT_UPLOAD_CUSTOMER_WAIT_DELIVER_LOGISTICS);
            UpdateByIdCheckState.update(recycleOrderMapper, recyclingOrder);

            //之前的 mq
            recycleOrderService.checkIntercept(billPurchase.getSerialNo());
        }

        return purchaseApplySettlementResult;

    }

    @Override
    public Page<StockBaseInfo> listByReturn(StockListRequest request) {

        return this.baseMapper.listByReturn(new Page<>(request.getPage(), request.getLimit()), request);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillPurchase changeRecycle(PurchaseChangeRecycleRequest request) {

        //1。判定
        BillPurchase billPurchase = this.baseMapper.selectById(request.getPurchaseId());

        Assert.isTrue(ObjectUtils.isNotEmpty(billPurchase) && billPurchase.getPurchaseState() == BusinessBillStateEnum.UNDER_WAY, "采购单不存在");

        BillPurchaseLine billPurchaseLine = billPurchaseLineMapper.selectOne(Wrappers.<BillPurchaseLine>lambdaQuery()
                .eq(BillPurchaseLine::getPurchaseId, request.getPurchaseId())
                .eq(BillPurchaseLine::getStockId, request.getStockId())
                .eq(BillPurchaseLine::getPurchaseLineState, PurchaseLineStateEnum.ON_CONSIGNMENT)

        );

        Assert.notNull(billPurchaseLine, "采购单行不存在");

        Integer consignmentPoint = Optional.ofNullable(purchaseSubjectMapper.selectById(billPurchase.getPurchaseSubjectId()).getConsignmentPoint()).orElse(0);

        BigDecimal consignmentPrice = request.getRecyclePrice().add(request.getRecyclePrice().multiply(BigDecimal.valueOf(consignmentPoint).divide(BigDecimal.valueOf(100L))));

        BillPurchase purchase = PurchaseConverter.INSTANCE.convertBillPurchase(billPurchase);

        //2。新建新的商品
        purchase.setSerialNo(SerialNoGenerator.generatePurchaseSerialNo());
        purchase.setApplyPaymentSerialNo(billPurchase.getApplyPaymentSerialNo());
        purchase.setPurchaseType(PurchaseTypeEnum.GR_HS);
        purchase.setPurchaseMode(PurchaseModeEnum.RECYCLE);
        purchase.setPurchaseSource(BusinessBillTypeEnum.GR_HS_JHS);
        //前端传入
        purchase.setTotalPurchasePrice(request.getRecyclePrice());

        purchase.setPurchaseState(BusinessBillStateEnum.COMPLETE);
        purchase.setRecycleModel(RecycleModeEnum.RECYCLE);
        purchase.setRemarks("个人寄售转回收");

        this.baseMapper.insert(purchase);

        //行状态
        BillPurchaseLine purchaseLine = PurchaseConverter.INSTANCE.convertBillPurchaseLine(billPurchaseLine);
        purchaseLine.setPurchaseId(purchase.getId());
        purchaseLine.setConsignmentPrice(consignmentPrice);
        purchaseLine.setPurchasePrice(request.getRecyclePrice());
        purchaseLine.setPurchaseLineState(PurchaseLineStateEnum.WAREHOUSED);
        purchaseLine.setRecyclePrice(request.getRecyclePrice());

        billPurchaseLineMapper.insert(purchaseLine);

        //3.更新 todo
        Stock stock = new Stock();
        stock.setId(billPurchaseLine.getStockId());
        stock.setConsignmentPrice(consignmentPrice);
        stock.setPurchasePrice(request.getRecyclePrice());
        //表的状态
        stockMapper.updateById(stock);

        BillPurchase billPurchaseEdit = new BillPurchase();
        billPurchaseEdit.setId(billPurchase.getId());
        //进行中
        billPurchaseEdit.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.UNDER_WAY_TO_CANCEL_WHOLE);
        UpdateByIdCheckState.update(baseMapper, billPurchaseEdit);

        BillPurchaseLine billPurchaseLineEdit = new BillPurchaseLine();
        billPurchaseLineEdit.setId(billPurchaseLine.getId());
        billPurchaseLineEdit.setPurchaseLineState(PurchaseLineStateEnum.ORDER_CANCEL_WHOLE);
        this.billPurchaseLineMapper.updateById(billPurchaseLineEdit);

        return purchase;
    }

    @Override
    public void extendTime(PurchaseExtendTimeRequest request) {
        //1。判定
        BillPurchase billPurchase = this.baseMapper.selectById(request.getPurchaseId());

        Assert.isTrue(ObjectUtils.isNotEmpty(billPurchase) && billPurchase.getPurchaseState() == BusinessBillStateEnum.UNDER_WAY && billPurchase.getPurchaseSource() == BusinessBillTypeEnum.GR_JS, "采购单不存在");

        //2.
        BillPurchase purchase = new BillPurchase();
        purchase.setId(request.getPurchaseId());
        purchase.setDealEndTime(request.getDealEndTime());

        this.baseMapper.updateById(purchase);
    }

    @Override
    public List<PurchaseByNameResult> getByPurchaseName(PurchaseByNameRequest request) {

        return billPurchaseLineMapper.getByPurchaseName(request);
    }

    @Override
    public BillPurchase billPurchaseQuery(String originSerialNo) {
        LambdaQueryWrapper<BillPurchase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillPurchase::getSerialNo, originSerialNo);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public BillPurchase selectOneByStockId(Integer stockId) {
        return this.baseMapper.selectOneByStockId(stockId);
    }

    @Override
    public void updateTotalPrice(Integer bpId, BigDecimal totalPrice) {
        this.baseMapper.updateTotalPrice(bpId, totalPrice);
    }

    @Override
    public Boolean editByStock(Integer stockId, String serialNo, String returnNewStockSn, String returnFixRemarks) {
        BillPurchase billPurchase = baseMapper.selectOne(Wrappers.<BillPurchase>lambdaQuery().eq(BillPurchase::getSerialNo, serialNo));

        if (Objects.nonNull(billPurchase)) {
            BillPurchaseLine purchaseLine = billPurchaseLineMapper.selectOne(Wrappers.<BillPurchaseLine>lambdaQuery()
                    .eq(BillPurchaseLine::getPurchaseId, billPurchase.getId())
                    .in(BillPurchaseLine::getStockId, stockId)
            );
            if (Objects.nonNull(purchaseLine)) {
                BillPurchaseLine billPurchaseLine = new BillPurchaseLine();
                billPurchaseLine.setStockSn(returnNewStockSn);
                billPurchaseLine.setOldStockSn(purchaseLine.getStockSn());
                billPurchaseLine.setId(purchaseLine.getId());
                billPurchaseLine.setReturnFixRemarks(returnFixRemarks);
                billPurchaseLineMapper.updateById(billPurchaseLine);

                return true;
            }
        }

        return false;
    }

    @Override
    public void autoPurchaseCreate(SelectInsertPurchaseRequest selectInsertPurchaseRequest, SelectInsertPurchaseLineRequest selectInsertPurchaseLineRequest) {
        baseMapper.selectInsert(selectInsertPurchaseRequest);

        selectInsertPurchaseLineRequest.setPurchaseId(selectInsertPurchaseRequest.getId());

        billPurchaseLineMapper.selectInsert(selectInsertPurchaseLineRequest);
    }


    @Override
    public List<String> checkoutStockSn(ApplyFinancialPaymentCheckoutStockSnRequest request) {
        if (Objects.isNull(request) || CollectionUtils.isEmpty(request.getStockSnList()))
            return Collections.EMPTY_LIST;
        return this.baseMapper.checkoutStockSn(request);
    }

    private String convert(List<DictData> dataList, List<Integer> itemList, Integer isCard, String warrantyDate, Integer goodsId) {
        String card = "空白保卡";
        String cardDate = "保卡(date)";
        if (Objects.nonNull(goodsId)) {
            Integer seriesType = seriesMapper.getSeriesTypeByGoodsId(goodsId);
            if (Objects.nonNull(seriesType) && SeriesTypeEnum.BAGS.getValue().equals(seriesType)) {
                card = "空白身份卡";
                cardDate = "身份卡(date)";
            }
        }
        String join = ObjectUtils.isEmpty(isCard) ? "" : (isCard.equals(1) ? StringUtils.replace(cardDate, "date", warrantyDate) : isCard.equals(0) ? "" : card);

        String attachment = "";

        if (ObjectUtils.isNotEmpty(dataList) && CollectionUtils.isNotEmpty(itemList)) {

            List<String> collect = itemList.stream().flatMap(item -> dataList.stream().filter(dictData -> item.intValue() == (dictData.getDictCode().intValue()))).map(dictData -> dictData.getDictLabel()).collect(Collectors.toList());

            attachment = StringUtils.join(collect, "/") + (ObjectUtils.isEmpty(join) ? "" : "/" + join);

        } else {
            return join;
        }

        return attachment;

    }

}




