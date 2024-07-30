package com.seeease.flywheel.serve.purchase.strategy;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentCreateRequest;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentCreateResult;
import com.seeease.flywheel.purchase.request.PurchaseCreateRequest;
import com.seeease.flywheel.purchase.result.PurchaseCreateListResult;
import com.seeease.flywheel.purchase.result.PurchaseDetailsResult;
import com.seeease.flywheel.serve.base.*;
import com.seeease.flywheel.serve.base.template.Bill;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.dict.entity.DictData;
import com.seeease.flywheel.serve.dict.service.DictDataService;
import com.seeease.flywheel.serve.financial.convert.ApplyFinancialPaymentConvert;
import com.seeease.flywheel.serve.financial.entity.AccountStockRelation;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPayment;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentStateEnum;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.service.AccountStockRelationService;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.financial.service.ApplyFinancialPaymentService;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.flywheel.serve.sale.convert.SaleOrderConverter;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.entity.BuyBackPolicyMapper;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderTypeEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.type.TransactionalUtil;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.exception.e.SeeeaseBaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/3/2
 */
@Slf4j
public abstract class PurchaseStrategy implements Bill<PurchaseCreateRequest, PurchaseCreateListResult> {

    private static final ImmutableRangeMap<Comparable<BigDecimal>, BigDecimal> MAP = ImmutableRangeMap.<Comparable<BigDecimal>, BigDecimal>builder()
            .put(Range.lessThan(BigDecimal.valueOf(20000L)), BigDecimal.valueOf(600L))
            .put(Range.atLeast(BigDecimal.valueOf(20000L)), BigDecimal.valueOf(1200L))
            .build();

    @Resource
    private BillPurchaseService billPurchaseService;
    @Resource
    private AccountStockRelationService stockRelationService;
    @Resource
    private BillPurchaseLineService billPurchaseLineService;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private BillSaleOrderLineService billSaleOrderLineService;

    @Resource
    private ApplyFinancialPaymentService applyFinancialPaymentService;

    @Resource
    private AccountsPayableAccountingService accountingService;

    @Resource
    private CustomerService customerService;

    @Resource
    private CustomerContactsService contactsService;

    @Resource
    private TransactionalUtil transactionalUtil;

    @Resource
    private BillSaleOrderService billSaleOrderService;

    @Resource
    private StockService stockService;

//    @Resource
//    private BillApplyPurchaseService billApplyPurchaseService;

    /**
     * 前置处理
     * 1、参数转换
     * 2、参数填充
     *
     * @param request
     */
    abstract void preRequestProcessing(PurchaseCreateRequest request);

    /**
     * 业务校验
     * 1、必要参数校验
     * 2、金额校验
     * 3、业务可行性校验
     *
     * @param request
     * @throws BusinessException
     */
    abstract void checkRequest(PurchaseCreateRequest request) throws BusinessException;

    @Override
    public void preProcessing(PurchaseCreateRequest request) {

        //采购来源
        request.setPurchaseSource(this.getType().getValue());
        //前置申请打款单 必须
        if (Optional.of(FlywheelConstant.FORCE_PRE_PAYMENTS).get().contains(this.getType().getValue())
                && ObjectUtils.isEmpty(request.getApplyPaymentSerialNo())
                && request.getMallUser()) {
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER_FINANCE_RETURN_APPLY);
        }

//        if (Arrays.asList(BusinessBillTypeEnum.TH_CG_DJ, BusinessBillTypeEnum.TH_CG_BH).contains(this.getType())) {
//        } else {
//            if (request.getDetails().stream().anyMatch(billPurchaseLineDto -> Objects.nonNull(billPurchaseLineDto.getOriginApplyPurchaseId()))) {
//                throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER_FINANCE_RETURN_APPLY);
//            }
//        }

//        log.info("purchaseType:{}, prePayment:{}", request.getPurchaseType(), request.getPrePayment());

        List<String> collect = request.getDetails().stream().map(PurchaseCreateRequest.BillPurchaseLineDto::getStockSn).collect(Collectors.toList());

        for (List<String> list : Lists.partition(collect, 500)) {

            //1.是否允许建单
            Map<String, BillPurchaseLine> collectByLine = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery().in(BillPurchaseLine::getStockSn, list)
                    .notIn(BillPurchaseLine::getPurchaseLineState, Arrays.asList(
                            PurchaseLineStateEnum.RETURNED,
                            PurchaseLineStateEnum.ORDER_CANCEL_WHOLE,
                            PurchaseLineStateEnum.WAREHOUSED,
                            PurchaseLineStateEnum.IN_SETTLED
                    ))).stream().collect(Collectors.toMap(BillPurchaseLine::getStockSn, Function.identity()));

            if (CollectionUtils.isNotEmpty(collectByLine.keySet())) {
                throw new OperationRejectedException(OperationExceptionCode.PURCHASE_PARAMETER);
            }
            //2。查询表身号
            Map<String, Stock> collectByStock = stockService.list(Wrappers.<Stock>lambdaQuery().in(Stock::getSn, list)
                    .notIn(Stock::getStockStatus, Arrays.asList(
                            StockStatusEnum.PURCHASE_RETURNED_ING,
                            StockStatusEnum.SOLD_OUT,
                            StockStatusEnum.PURCHASE_RETURNED
                    ))).stream().collect(Collectors.toMap(Stock::getSn, Function.identity()));

            if (CollectionUtils.isNotEmpty(collectByStock.keySet())) {
                throw new OperationRejectedException(OperationExceptionCode.PURCHASE_PARAMETER);
            }
        }

        //生成采购单号
        request.setSerialNo(SerialNoGenerator.generatePurchaseSerialNo());

        //字典处理
        List<DictData> dataList = dictDataService.list(Wrappers.<DictData>lambdaQuery().likeRight(DictData::getDictType, "stock_"));
        //封装附件id
        request.getDetails().forEach(billPurchaseLineDto -> {

            List<Integer> convert = convert(dataList, billPurchaseLineDto.getAttachmentMap());

            billPurchaseLineDto.setAttachmentList(convert);
        });

        //处理入参每行数值的四舍五入的值
        this.preRequestProcessingPurchaseLine(request);

        //查询打款单 预处理
        this.preRequestProcessingApplyFinancialPayment(request);

        /**
         * 本次销售单号
         */
        this.preProcessingSale(request);

        /**
         * 关联销售单号
         */
        this.preProcessingOriginSale(request);


        this.preRequestProcessing(request);

        //特殊 处理累加
        this.preRequestProcessingReducePurchasePrice(request);
    }


    @Override
    public void bizCheck(PurchaseCreateRequest request) throws SeeeaseBaseException {
        Assert.notNull(request.getStoreId(), "门店不能为空");

        Assert.notNull(request.getTotalPurchasePrice(), "采购总价不能为空");
        Assert.isTrue(request.getDetails().stream().allMatch(t -> Objects.nonNull(t.getPurchasePrice())), "采购价不能为空");
        request.getDetails()
                .forEach(t -> {
                    Assert.isTrue(CollectionUtils.isNotEmpty(t.getAttachmentList()) || t.getIsCard() != 0, "附件信息不能为空");
                });

        this.checkRequest(request);
    }

    @Override
    public PurchaseCreateListResult save(PurchaseCreateRequest request) {

        return transactionalUtil.transactional(() -> {
            PurchaseCreateListResult result = billPurchaseService.create(request);
            List<Integer> stockIds = result.getLine().stream()
                    .map(PurchaseDetailsResult.PurchaseLineVO::getStockId).collect(Collectors.toList());
            switch (getType()) {
                case TH_CG_DJ:
                case TH_CG_BH:
                case TH_CG_QK:
                case TH_CG_DJTP:
//                    if (ObjectUtils.isNotEmpty(request.getPrePayment()) && request.getPrePayment().intValue() == 1) {
//                        return result;
//                    }
//                    Customer customer = customerService.getById(request.getCustomerId());
//                    //同行采购（订金/备货） 新建采购单后根据采购单自动创建
//                    createAfp(request, result, ApplyFinancialPaymentTypeEnum.PEER_PROCUREMENT,
//                            request.getTotalPurchasePrice(), Objects.nonNull(customer) ? customer.getCustomerName() : null);
                case GR_HS_JHS:
                    //todo
                    log.info("不做什么操作");
                    //个人回收 在创建采购单的时候 生成预付单
//                    accountingService.createApa(result.getSerialNo(), ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT,
//                            FinancialStatusEnum.PENDING_REVIEW, stockIds, null, false);
//                    editApplyFinancialPayment(request);

                    //申请打款单 和 商品关联关系

                    break;
                case GR_HS_ZH:
//                    BigDecimal reduce = request.getDetails().stream().map(PurchaseCreateRequest.BillPurchaseLineDto::getRecyclePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
//                    //（当付款方式为全款时）
//                    if (PurchasePaymentMethodEnum.FK_QK.getValue().equals(request.getPaymentMethod())) {
//                        //回收置换，在订单创建时生成预付单，
//                        accountingService.createApa(result.getSerialNo(), ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT,
//                                FinancialStatusEnum.PENDING_REVIEW, stockIds, reduce, false);
//                        CustomerContacts contacts = contactsService.getById(request.getCustomerContactId());
//                        //生成申请打款单及确认收款单，均为销售单/采购单的全款金额
//                        //申请打款单：回收置换-新建采购单提交后创建
//                        createAfp(request, result, ApplyFinancialPaymentTypeEnum.PERSONAL_RECYCLING,
//                                reduce, Objects.nonNull(contacts) ? contacts.getName() : null, stockIds);
//                    } else if (PurchasePaymentMethodEnum.FK_CE.getValue().equals(request.getPaymentMethod())) {
//                        //当付款方式为差额时）差额=回收价/置换价-销售价
//                        //累计回收价
//                        BigDecimal subtract = reduce.subtract(request.getSalePrice());//差额
//
//                        if (subtract.signum() > 0) {
//                            //若差额为正数，代表我们需要付钱
//                            //回收置换，在订单创建时生成预付单，入库后自动核销，核销说明：入库自动核销
//                            accountingService.createApa(result.getSerialNo(), ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT,
//                                    FinancialStatusEnum.PENDING_REVIEW, stockIds, reduce, false);
//                            //生成申请打款单金额为差额 申请打款单：回收置换-新建采购单提交后创建
//                            CustomerContacts contacts = contactsService.getById(request.getCustomerContactId());
//                            createAfp(request, result, ApplyFinancialPaymentTypeEnum.PERSONAL_RECYCLING,
//                                    subtract, Objects.nonNull(contacts) ? contacts.getName() : null, stockIds);
//                        } else {
//                            //若差额为负数，代表我们需要收钱
//                            //回收置换，在订单创建时生成预付单，入库后自动核销，核销说明：入库自动核销
//                            accountingService.createApa(result.getSerialNo(), ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT,
//                                    FinancialStatusEnum.PENDING_REVIEW, stockIds, reduce, false);
//                        }
//                    }

                    //累计回收价
//                    BigDecimal reduce = request.getDetails().stream().map(PurchaseCreateRequest.BillPurchaseLineDto::getRecyclePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
//                    BigDecimal subtract = request.getSalePrice().subtract(reduce);
//
//                    if (subtract.signum() < 0) {
//                        CustomerContacts contacts = contactsService.getById(request.getCustomerContactId());
//                        //新建个人置换单后（成交价-回收价＜0）提交后 申请打款单生成
//                        createAfp(request, result, getType().equals(BusinessBillTypeEnum.GR_HS_ZH) ?
//                                        ApplyFinancialPaymentTypeEnum.PERSONAL_RECYCLING : ApplyFinancialPaymentTypeEnum.BUY_BACK,
//                                subtract.abs(), Objects.nonNull(contacts) ? contacts.getName() : null, stockIds);
//                    } else {
//                        accountingService.createApa(result.getSerialNo(), ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE,
//                                FinancialStatusEnum.PENDING_REVIEW, stockIds, subtract, false);
//                    }
                    break;
                case GR_HG_ZH:
//                    if (request.getSalePrice().compareTo(request.getTotalPurchasePrice()) > 0) {
//                        //新建个人置换单后 成交价-回收价＞0提交后 应收单生成
//                        accountingService.createApa(result.getSerialNo(), ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE,
//                                FinancialStatusEnum.PENDING_REVIEW, stockIds, request.getSalePrice().subtract(request.getTotalPurchasePrice()), false);
//                    }
                    break;
            }
            return result;
        });
    }

    private void editApplyFinancialPayment(PurchaseCreateRequest request) {

        ApplyFinancialPayment applyFinancialPayment = new ApplyFinancialPayment();
        applyFinancialPayment.setWhetherUse(WhetherEnum.YES);

        applyFinancialPaymentService.update(applyFinancialPayment, Wrappers.<ApplyFinancialPayment>lambdaQuery().eq(ApplyFinancialPayment::getSerialNo, request.getApplyPaymentSerialNo()));
    }


    private String createAfp(PurchaseCreateRequest request, PurchaseCreateListResult result, ApplyFinancialPaymentTypeEnum typePayment,
                             BigDecimal pricePayment, String name, List<Integer> stockIds) {
        ApplyFinancialPaymentCreateRequest createRequest = new ApplyFinancialPaymentCreateRequest();
        createRequest.setPricePayment(pricePayment);
        createRequest.setTypePayment(typePayment.getValue());
        createRequest.setCustomerContactsId(request.getCustomerContactId());
        createRequest.setSubjectPayment(request.getPurchaseSubjectId());
        createRequest.setShopId(request.getStoreId());
        createRequest.setWhetherUse(1);
        createRequest.setCustomerName(name);
        createRequest.setBankAccount(request.getBank());
        createRequest.setBankCard(request.getBankAccount());
        createRequest.setBankName(request.getAccountName());
        createRequest.setBankCustomerName(request.getBankCustomerName());
        createRequest.setDemanderStoreId(request.getDemanderStoreId());
        createRequest.setSalesMethod(PurchaseModeEnum.convert(PurchaseModeEnum.fromCode(request.getPurchaseMode())).getValue());
        createRequest.setBatchPictureUrl(request.getBatchPictureUrl());

        ApplyFinancialPaymentCreateResult createResult = applyFinancialPaymentService.create(createRequest);

        //申请打款单 和 商品关联关系
        List<AccountStockRelation> relationList = stockIds.stream()
                .map(a -> AccountStockRelation.builder()
                        .afpId(createResult.getId())
                        .stockId(a)
                        .originSerialNo(result.getSerialNo())
                        .originPrice(createResult.getPricePayment())
                        .build())
                .collect(Collectors.toList());
        stockRelationService.saveBatch(relationList);

        BillPurchase purchase = new BillPurchase();
        purchase.setId(result.getId());
        purchase.setApplyPaymentSerialNo(createResult.getSerialNo());
        billPurchaseService.updateById(purchase);
        return createResult.getSerialNo();
    }

    /**
     * 字典list
     *
     * @param dataList
     * @param map
     * @return
     */
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

    private void preProcessingSale(PurchaseCreateRequest request) {

        if (ObjectUtils.isNotEmpty(request.getSaleSerialNo()) && request.getMallUser()) {

            //本次销售单号
            BillSaleOrder billSaleOrder = billSaleOrderService.getOne(Wrappers.<BillSaleOrder>lambdaQuery().
                    eq(BillSaleOrder::getSerialNo, request.getSaleSerialNo())
                    .eq(BillSaleOrder::getSaleType, SaleOrderTypeEnum.TO_C_XS)
                    //已销售的
                    .eq(BillSaleOrder::getSaleState, BusinessBillStateEnum.COMPLETE));
            Optional.ofNullable(billSaleOrder).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.SALE_PURCHASE));

            request.setSaleOrderDetailsResult(SaleOrderConverter.INSTANCE.convertSaleOrderDetailsResult(billSaleOrder));
        }
    }

    private void preProcessingOriginSale(PurchaseCreateRequest request) {

        if (ObjectUtils.isNotEmpty(request.getOriginSaleSerialNo()) && request.getMallUser()) {

            //关联销售单号
            BillSaleOrder billSaleOrder = billSaleOrderService.getOne(Wrappers.<BillSaleOrder>lambdaQuery().
                    eq(BillSaleOrder::getSerialNo, request.getOriginSaleSerialNo())
                    .eq(BillSaleOrder::getSaleType, SaleOrderTypeEnum.TO_C_XS)
                    //已销售的
                    .eq(BillSaleOrder::getSaleState, BusinessBillStateEnum.COMPLETE));
            Optional.ofNullable(billSaleOrder).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.ORIGIN_SALE_PURCHASE));

            request.setOriginSaleOrderDetailsResult(SaleOrderConverter.INSTANCE.convertSaleOrderDetailsResult(billSaleOrder));
        }
    }

    private void preRequestProcessingReducePurchasePrice(PurchaseCreateRequest request) {
        //只处理累加价格 注意四舍五入
        BigDecimal totalPurchasePrice = request.getDetails().stream()
                .map(PurchaseCreateRequest.BillPurchaseLineDto::getPurchasePrice)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        request.setTotalPurchasePrice(totalPurchasePrice);
    }

    private void preRequestProcessingPurchaseLine(PurchaseCreateRequest request) {

        request.getDetails().forEach(billPurchaseLineDto -> {

            /**
             * 采购价
             */
            if (ObjectUtils.isNotEmpty(billPurchaseLineDto.getPurchasePrice())) {
                billPurchaseLineDto.setPurchasePrice(BigDecimalUtil.roundHalfUp(billPurchaseLineDto.getPurchasePrice()));
            }

            /**
             * 寄售协议价
             */
            if (ObjectUtils.isNotEmpty(billPurchaseLineDto.getDealPrice())) {
                billPurchaseLineDto.setDealPrice(BigDecimalUtil.roundHalfUp(billPurchaseLineDto.getDealPrice()));
            }

            /**
             * 回收价
             */
            if (ObjectUtils.isNotEmpty(billPurchaseLineDto.getRecyclePrice())) {
                billPurchaseLineDto.setRecycleServePrice(BigDecimalUtil.roundHalfUp(billPurchaseLineDto.getRecyclePrice()));
            }
        });
    }

    private void preRequestProcessingApplyFinancialPayment(PurchaseCreateRequest request) {

        if (request.getMallUser() && ObjectUtils.isNotEmpty(request.getApplyPaymentSerialNo())) {
            ApplyFinancialPayment applyFinancialPayment = applyFinancialPaymentService.getOne(Wrappers.<ApplyFinancialPayment>lambdaQuery()
                    .eq(ApplyFinancialPayment::getSerialNo, request.getApplyPaymentSerialNo())
                    .eq(ApplyFinancialPayment::getState, ApplyFinancialPaymentStateEnum.PAID)
                    .eq(ApplyFinancialPayment::getWhetherUse, WhetherEnum.NO));

            Optional.ofNullable(applyFinancialPayment).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.APPLY_FINANCIAL_PAYMENT));

            request.setApplyFinancialPaymentDetailResult(ApplyFinancialPaymentConvert.INSTANCE.convert(applyFinancialPayment));
        }
    }

    public void preProcessingBuyBack(PurchaseCreateRequest request, Boolean again) {

        //查询表是否存在回购
        List<BillPurchaseLine> purchaseLineList = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery()
                .eq(BillPurchaseLine::getOriginStockId, request.getDetails().get(FlywheelConstant.INDEX).getOriginStockId()));

        //是否存在相同的回购单
        if (CollectionUtils.isNotEmpty(purchaseLineList)) {
            List<BillPurchase> collect = billPurchaseService.listByIds(purchaseLineList.stream().map(BillPurchaseLine::getPurchaseId).collect(Collectors.toList()))
                    .stream().filter(billPurchase -> request.getOriginSaleSerialNo().equals(billPurchase.getOriginSaleSerialNo()) && !billPurchase.getPurchaseState().equals(BusinessBillStateEnum.CANCEL_WHOLE)).collect(Collectors.toList());
            //
            if (!collect.isEmpty()) {
                throw new OperationRejectedException(OperationExceptionCode.BUY_BACK_EXITS);
            }
        }

        BillSaleOrderLine billSaleOrderLine;
        //已销售的
        if (!request.getMallUser()) {
            billSaleOrderLine = billSaleOrderLineService.getOne(Wrappers.<BillSaleOrderLine>lambdaQuery().
                    eq(BillSaleOrderLine::getSaleId, request.getOriginSaleOrderDetailsResult().getId()).
                    eq(BillSaleOrderLine::getIsCounterPurchase, WhetherEnum.YES.getValue()).
                    eq(BillSaleOrderLine::getIsRepurchasePolicy, WhetherEnum.YES.getValue()).
                    eq(BillSaleOrderLine::getStockId, request.getDetails().get(FlywheelConstant.INDEX).getOriginStockId()));
        } else {
            billSaleOrderLine = billSaleOrderLineService.getOne(Wrappers.<BillSaleOrderLine>lambdaQuery().
                    eq(BillSaleOrderLine::getSaleId, request.getOriginSaleOrderDetailsResult().getId()).
                    eq(BillSaleOrderLine::getSaleLineState, SaleOrderLineStateEnum.DELIVERED).
                    eq(BillSaleOrderLine::getIsCounterPurchase, WhetherEnum.YES.getValue()).
                    eq(BillSaleOrderLine::getIsRepurchasePolicy, WhetherEnum.YES.getValue()).
                    eq(BillSaleOrderLine::getStockId, request.getDetails().get(FlywheelConstant.INDEX).getOriginStockId()));
        }

        if (ObjectUtils.isEmpty(billSaleOrderLine)) {
            throw new OperationRejectedException(OperationExceptionCode.ORIGIN_SALE_STOCK);
        }

        //寄售价=门店采购价
        BigDecimal consignmentPrice = billSaleOrderLine.getConsignmentPrice();
        //关联销售成交价
        BigDecimal clinchPrice = billSaleOrderLine.getClinchPrice();

        /**
         * 销售单里面的值
         */
        BigDecimal watchbandReplacePrice = billSaleOrderLine.getStrapReplacementPrice();

        /**
         * 判断表带更换费
         */
        if (ObjectUtils.isEmpty(watchbandReplacePrice) || BigDecimal.ZERO.compareTo(watchbandReplacePrice) == 0) {
            // 判断抛出
            for (PurchaseCreateRequest.BillPurchaseLineDto detail : request.getDetails()) {
                if (ObjectUtils.isEmpty(detail.getStrapMaterial())) {
                    throw new OperationRejectedException(OperationExceptionCode.ORIGIN_SALE_STOCK);
                }
                if (Arrays.asList("皮", "针织", "绢丝").contains(detail.getStrapMaterial())) {
                    if (ObjectUtils.isEmpty(detail.getWhetherFix())) {
                        throw new OperationRejectedException(OperationExceptionCode.ORIGIN_SALE_STOCK);
                    }
                    if (WhetherEnum.YES == (WhetherEnum.fromValue(detail.getWhetherFix()))) {
                        watchbandReplacePrice = MAP.get(clinchPrice);
                    } else {
                        watchbandReplacePrice = BigDecimal.ZERO;
                    }

                } else {
                    watchbandReplacePrice = BigDecimal.ZERO;
                }
            }
        }

        //*********价格计算开始***********

        //入参对象
        PurchaseCreateRequest.BillPurchaseLineDto billPurchaseLineDto = request.getDetails().get(FlywheelConstant.INDEX);

        billPurchaseLineDto.setWatchbandReplacePrice(Objects.nonNull(billPurchaseLineDto.getWatchbandReplacePrice())? billPurchaseLineDto.getWatchbandReplacePrice(): watchbandReplacePrice);

        billPurchaseLineDto.setIsSettlement(WhetherEnum.NO.getValue());
        //寄售价
        billPurchaseLineDto.setConsignmentPrice(consignmentPrice);
        //成交价
        billPurchaseLineDto.setClinchPrice(clinchPrice);
        //预计维修价
        billPurchaseLineDto.setPlanFixPrice(Objects.nonNull(billPurchaseLineDto.getPlanFixPrice()) ? billPurchaseLineDto.getPlanFixPrice() : BigDecimal.ZERO);
        //实际维修价
        billPurchaseLineDto.setFixPrice(BigDecimal.ZERO);

        //参考回购价规范->排序 正序
        List<BuyBackPolicyMapper> mappers = billSaleOrderLine.getBuyBackPolicy().stream().sorted(Comparator.comparing(BuyBackPolicyMapper::getBuyBackTime)).collect(Collectors.toList());
        ImmutableRangeMap.Builder<Comparable<Date>, BigDecimal> builder = ImmutableRangeMap.<Comparable<Date>, BigDecimal>builder();
        //差值 初始者
        AtomicReference<Integer> difference = new AtomicReference<>(0);
        //回购加点值
        mappers.forEach(buyBackPolicyMapper -> {
            //前开后闭
            //YYYY_MM_DD_HH_MM_SS ？？？？ 建单时间 初始时间
            Date date = DateUtils.stepMonth(DateUtil.parseDate(request.getOriginSaleOrderDetailsResult().getCreatedTime()), difference.get());
            //递增值
            Date month = DateUtils.stepMonth(DateUtil.parseDate(request.getOriginSaleOrderDetailsResult().getCreatedTime()), buyBackPolicyMapper.getBuyBackTime());
            //回收
            if (again) {
                builder.put(Range.openClosed(date, month),
                        buyBackPolicyMapper.getDiscount().add(buyBackPolicyMapper.getReplacementDiscounts()));
            } else {
                builder.put(Range.openClosed(date, month),
                        buyBackPolicyMapper.getDiscount());
            }
            difference.set(buyBackPolicyMapper.getBuyBackTime());
        });

        //建单时间 参考回购价
        BigDecimal referenceBuyBackPrice = BigDecimalUtil.multiplyRoundHalfUp(clinchPrice,
                //加点
                Optional.ofNullable(builder.build().get(DateUtil.parse(DateUtil.today()).toJdkDate())).orElse(BigDecimal.ZERO)
                        //值点
                        .divide(BigDecimal.valueOf(FlywheelConstant.MULTIPLIER_10), 4, BigDecimal.ROUND_HALF_UP));

        //选择的回收置换折扣
        if (StringUtils.isNotEmpty(billPurchaseLineDto.getSelectedDiscount())) {
            BigDecimal referenceBuyBackPriceBySelected = BigDecimalUtil.multiplyRoundHalfUp(clinchPrice,
                    //值点
                    new BigDecimal(billPurchaseLineDto.getSelectedDiscount()).divide(BigDecimal.valueOf(FlywheelConstant.MULTIPLIER_10), 4, BigDecimal.ROUND_HALF_UP));

            Assert.isTrue(com.seeease.springframework.utils.BigDecimalUtil.le(referenceBuyBackPriceBySelected, referenceBuyBackPrice), "目标折扣回收价不合法");
            //覆盖回收价
            referenceBuyBackPrice = referenceBuyBackPriceBySelected;
        }

        if (ObjectUtils.isEmpty(referenceBuyBackPrice)
                || com.seeease.springframework.utils.BigDecimalUtil.eqZero(referenceBuyBackPrice)) {
            throw new OperationRejectedException(OperationExceptionCode.REFERENCE_BUY_BACK_PRICE);
        }

        //会不会导致引用
        billPurchaseLineDto.setReferenceBuyBackPrice(referenceBuyBackPrice);
//        billPurchaseLineDto.setWatchbandReplacePrice(watchbandReplacePrice);

        //实际回购价 = 参考回购价 - 预计维修价 - 表带更换费
        BigDecimal buyBackPrice = referenceBuyBackPrice.subtract(Objects.nonNull(billPurchaseLineDto.getPlanFixPrice()) ? billPurchaseLineDto.getPlanFixPrice() : BigDecimal.ZERO).subtract(Objects.nonNull(billPurchaseLineDto.getWatchbandReplacePrice())? billPurchaseLineDto.getWatchbandReplacePrice(): watchbandReplacePrice);

        //采购价
        //回购服务费
        if (buyBackPrice.compareTo(consignmentPrice) > 0) {
            billPurchaseLineDto.setPurchasePrice(consignmentPrice);
            billPurchaseLineDto.setRecycleServePrice(buyBackPrice.subtract(consignmentPrice));
        } else {
            billPurchaseLineDto.setPurchasePrice(buyBackPrice);
            billPurchaseLineDto.setRecycleServePrice(BigDecimal.ZERO);
        }

        //实际回购价
        billPurchaseLineDto.setBuyBackPrice(buyBackPrice);

        //*********价格计算结束***********
    }

    /**
     * 校验与打款单相同
     *
     * @param request
     */
    public void bizCheckApplyPayment(PurchaseCreateRequest request) {

        if (!request.getPurchaseSubjectId().equals(request.getApplyFinancialPaymentDetailResult().getSubjectPayment())) {
            throw new OperationRejectedException(OperationExceptionCode.APPLY_FINANCIAL_PURCHASE);
        }

        if (request.getMallUser() && request.getTotalPurchasePrice().compareTo(request.getApplyFinancialPaymentDetailResult().getPricePayment()) != 0) {
            throw new OperationRejectedException(OperationExceptionCode.APPLY_FINANCIAL_MONEY_PURCHASE);
        }
    }
}
