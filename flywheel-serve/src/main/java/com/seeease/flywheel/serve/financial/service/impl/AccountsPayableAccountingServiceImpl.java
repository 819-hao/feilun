package com.seeease.flywheel.serve.financial.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.request.AccountsPayableAccountingQueryRequest;
import com.seeease.flywheel.financial.result.AccountsPayableAccountingPageResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.mapper.CustomerMapper;
import com.seeease.flywheel.serve.financial.convert.AuditLoggingConvert;
import com.seeease.flywheel.serve.financial.entity.*;
import com.seeease.flywheel.serve.financial.enums.FinancialClassificationEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.enums.OriginTypeEnum;
import com.seeease.flywheel.serve.financial.enums.ReceiptPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.mapper.*;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturn;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturnLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseLineMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseReturnLineMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseReturnMapper;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrder;
import com.seeease.flywheel.serve.sale.enums.SaleOrderModeEnum;
import com.seeease.flywheel.serve.sale.enums.SaleReturnOrderTypeEnum;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderLineMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleReturnOrderLineMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleReturnOrderMapper;
import com.seeease.springframework.context.UserContext;
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
 * @author edy
 * @description 针对表【accounts_payable_accounting】的数据库操作Service实现
 * @createDate 2023-05-10 10:13:56
 */
@Slf4j
@Service
public class AccountsPayableAccountingServiceImpl extends ServiceImpl<AccountsPayableAccountingMapper, AccountsPayableAccounting>
        implements AccountsPayableAccountingService {

    @Resource
    private AuditLoggingMapper auditLoggingMapper;
    @Resource
    private AuditLoggingDetailMapper auditLoggingDetailMapper;
    @Resource
    private BillSaleOrderMapper saleOrderMapper;
    @Resource
    private BillSaleOrderLineMapper saleOrderLineMapper;
    @Resource
    private BillSaleReturnOrderMapper returnOrderMapper;
    @Resource
    private BillSaleReturnOrderLineMapper returnOrderLineMapper;
    @Resource
    private BillPurchaseMapper purchaseMapper;
    @Resource
    private BillPurchaseReturnLineMapper purchaseReturnLineMapper;
    @Resource
    private BillPurchaseReturnMapper purchaseReturnMapper;
    @Resource
    private BillPurchaseLineMapper purchaseLineMapper;
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private ApplyFinancialPaymentMapper paymentMapper;
    @Resource
    private AccountStockRelationMapper stockRelationMapper;
    @Resource
    private AccountReceiptConfirmMapper receiptConfirmMapper;

    @Override
    public Page<AccountsPayableAccountingPageResult> page(AccountsPayableAccountingQueryRequest request) {
        return this.baseMapper.getPage(new Page(request.getPage(), request.getLimit()), request);
    }

    @Override
    public List<AccountsPayableAccounting> selectListByOriginSerialNoAndStatusAndType(String originSerialNo, List<FinancialStatusEnum> statusList, List<ReceiptPaymentTypeEnum> typeList) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<AccountsPayableAccounting>()
                .eq(Objects.nonNull(originSerialNo), AccountsPayableAccounting::getOriginSerialNo, originSerialNo)
                .in(AccountsPayableAccounting::getType, typeList)
                .in(AccountsPayableAccounting::getStatus, statusList));
    }

    @Override
    public List<AccountsPayableAccounting> selectListByAfpSerialNoAndStatusAndType(String afpSerialNo, List<FinancialStatusEnum> statusList, List<ReceiptPaymentTypeEnum> typeList) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<AccountsPayableAccounting>()
                .eq(AccountsPayableAccounting::getAfpSerialNo, afpSerialNo)
                .in(AccountsPayableAccounting::getType, typeList)
                .in(AccountsPayableAccounting::getStatus, statusList));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAudit(List<Integer> ids, String auditDescription, String userName) {
        if (CollectionUtils.isEmpty(ids))
            return;
        log.info("batchAudit ids: {} ,audit: {}", ids, userName);
        ids.forEach(id -> {
            AccountsPayableAccounting accounting = new AccountsPayableAccounting();
            accounting.setId(id);
            accounting.setStatus(FinancialStatusEnum.AUDITED);
            accounting.setAuditTime(new Date());
            accounting.setAuditor(userName);
            accounting.setAuditDescription(auditDescription);
            accounting.setWaitAuditPrice(BigDecimal.ZERO);
            this.baseMapper.updateById(accounting);
        });
        this.baseMapper.selectBatchIds(ids).stream()
                .filter(t -> StringUtils.isNotBlank(t.getAfpSerialNo()))
                .collect(Collectors.groupingBy(AccountsPayableAccounting::getAfpSerialNo))
                .forEach((afpSerialNo, list) -> {
                    list.stream().collect(Collectors.groupingBy(AccountsPayableAccounting::getCustomerContactId))
                            .forEach((contactId, accountingList) -> {
                                AuditLogging auditLogging = AuditLoggingConvert.INSTANCE.convertAccountsPayableAccounting(list.stream().findFirst().get());
                                auditLogging.setId(null);
                                auditLogging.setAuditTime(new Date());
                                auditLogging.setAuditName(userName);
                                auditLogging.setNumber(accountingList.size());
                                auditLogging.setTotalPrice(accountingList.stream()
                                        .map(AccountsPayableAccounting::getTotalPrice)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                                auditLoggingMapper.insert(auditLogging);
                                List<AuditLoggingDetail> auditLoggingDetailList = new ArrayList<>();
                                accountingList.forEach(accountsPayableAccounting -> {
                                    AuditLoggingDetail detail = AuditLoggingConvert.INSTANCE.convertAuditLoggingDetail(accountsPayableAccounting);
                                    detail.setId(null);
                                    detail.setAuditLoggingId(auditLogging.getId());
                                    detail.setApaId(accountsPayableAccounting.getId());
                                    detail.setSerialNo(accountsPayableAccounting.getSerialNo());
                                    auditLoggingDetailList.add(detail);
                                    switch (auditLogging.getType()) {
                                        case PRE_PAID_AMOUNT:
                                            detail.setPrePaidAmount(accountsPayableAccounting.getTotalPrice());
                                            break;
                                        case AMOUNT_PAYABLE:
                                            detail.setAmountPayable(accountsPayableAccounting.getTotalPrice());
                                            break;
                                        case AMOUNT_RECEIVABLE:
                                            detail.setAmountReceivable(accountsPayableAccounting.getTotalPrice());
                                            break;
                                        case PRE_RECEIVE_AMOUNT:
                                            detail.setPreReceiveAmount(accountsPayableAccounting.getTotalPrice());
                                            break;
                                    }
                                });
                                auditLoggingDetailMapper.insertBatchSomeColumn(auditLoggingDetailList);
                            });
                });
    }

    /**
     * 采购应收应付单
     *
     * @param originSerialNo
     * @param typeEnum
     * @param pendingReview
     * @param stockIds
     * @param totalPrice
     * @param b
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Integer> createApa(String originSerialNo, ReceiptPaymentTypeEnum typeEnum, FinancialStatusEnum pendingReview,
                          List<Integer> stockIds, BigDecimal totalPrice, boolean b) {
        BillPurchase purchase = purchaseMapper.selectOne(new LambdaQueryWrapper<BillPurchase>()
                .eq(BillPurchase::getSerialNo, originSerialNo));
        if (ObjectUtils.isEmpty(purchase))
            return null;
        log.info("log for createApa originSerialNo:{} type:{} status:{}", originSerialNo, typeEnum.getDesc(), pendingReview.getDesc());
        ApplyFinancialPayment payment = paymentMapper.selectOne(new LambdaQueryWrapper<ApplyFinancialPayment>()
                .eq(ApplyFinancialPayment::getSerialNo, purchase.getApplyPaymentSerialNo()));
        Customer customer = customerMapper.selectById(purchase.getCustomerId());
        List<AccountsPayableAccounting> accountingList = purchaseLineMapper.selectByPurchaseId(purchase.getId())
                .stream()
                .filter(t -> stockIds.contains(t.getStockId()))
                .map(line -> {
                    AccountsPayableAccounting accounting = new AccountsPayableAccounting();
                    switch (typeEnum) {
                        case PRE_RECEIVE_AMOUNT:
                            accounting.setSerialNo(SerialNoGenerator.generatePreReceiveAmountSerialNo());
                            break;
                        case AMOUNT_RECEIVABLE:
                            accounting.setSerialNo(SerialNoGenerator.generateAmountReceivableSerialNo());
                            break;
                        case AMOUNT_PAYABLE:
                            accounting.setSerialNo(SerialNoGenerator.generateAmountPayableSerialNo());
                            break;
                        case PRE_PAID_AMOUNT:
                            accounting.setSerialNo(SerialNoGenerator.generatePrePaidAmountSerialNo());
                            break;
                    }
                    if (ObjectUtils.isNotEmpty(payment)) {
                        accounting.setAfpId(payment.getId());
                        accounting.setAfpSerialNo(payment.getSerialNo());
                    }
                    accounting.setPurchaseId(purchase.getPurchaseId());
                    accounting.setDemanderStoreId(purchase.getDemanderStoreId());
                    accounting.setOriginSerialNo(originSerialNo);
                    if (Objects.nonNull(line.getStockId())) {
                        accounting.setStockId(line.getStockId());
                    }
                    accounting.setStockSn(line.getStockSn());
                    accounting.setBrandName(line.getBrandName());
                    accounting.setSeriesName(line.getSeriesName());
                    accounting.setModel(line.getModel());
                    accounting.setTotalPrice(ObjectUtils.isEmpty(totalPrice) ? line.getPurchasePrice() : totalPrice);
                    accounting.setCustomerId(purchase.getCustomerId());
                    accounting.setApplicant(purchase.getCreatedBy());
                    accounting.setShopId(purchase.getStoreId());
                    accounting.setBelongId(purchase.getPurchaseSubjectId());
                    accounting.setCustomerContactId(purchase.getCustomerContactId());
                    accounting.setSalesMethod(PurchaseModeEnum.convert(purchase.getPurchaseMode()));
                    accounting.setCustomerType(Objects.nonNull(customer) ? customer.getType().getValue() : null);
                    accounting.setType(typeEnum);
                    accounting.setClassification(BusinessBillTypeEnum.convertClassification(purchase.getPurchaseSource()));
                    accounting.setStatus(pendingReview);
                    accounting.setOriginType(b ? OriginTypeEnum.CG_TH : OriginTypeEnum.CG);
                    return accounting;
                })
                .collect(Collectors.toList());
        this.baseMapper.insertBatchSomeColumn(accountingList);

        return accountingList.stream().map(AccountsPayableAccounting::getId).collect(Collectors.toList());
    }

    @Override
    public void updateStatusByAfpSerialNo(String afpSerialNo, Integer currStatus, Integer toStatus) {
        this.baseMapper.updateStatusByAfpSerialNo(afpSerialNo, currStatus, toStatus);
    }

    /**
     * 采购退货应收应付单
     *
     * @param arcSerialNo
     * @param returnId
     * @param stockIds
     * @param typeEnum
     * @param returnPendingReview
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createApaByReturn(String arcSerialNo, Integer returnId, List<Integer> stockIds, ReceiptPaymentTypeEnum typeEnum, FinancialStatusEnum returnPendingReview) {
        List<BillPurchaseReturnLine> returnLineList = purchaseReturnLineMapper.selectList(new LambdaQueryWrapper<BillPurchaseReturnLine>()
                .eq(BillPurchaseReturnLine::getPurchaseReturnId, returnId)
                .in(BillPurchaseReturnLine::getStockId, stockIds));
        if (CollectionUtils.isEmpty(returnLineList)) {
            return;
        }
        log.info("log for createApa originSerialNo:{} type:{} status:{}", arcSerialNo, typeEnum.getDesc(), returnPendingReview.getDesc());
        returnLineList.stream()
                .collect(Collectors.groupingBy(BillPurchaseReturnLine::getOriginSerialNo))
                .forEach((serialNo, list) -> {
                    BillPurchaseReturn purchaseReturn = purchaseReturnMapper.selectById(returnId);
                    BillPurchase purchase = purchaseMapper.selectOne(new LambdaQueryWrapper<BillPurchase>()
                            .eq(BillPurchase::getSerialNo, serialNo));

                    if (ObjectUtils.isEmpty(purchase) || ObjectUtils.isEmpty(purchaseReturn))
                        return;
                    Customer customer = customerMapper.selectById(purchaseReturn.getCustomerId());
                    List<AccountsPayableAccounting> accountingList = purchaseLineMapper.selectByPurchaseId(purchase.getId())
                            .stream()
                            .filter(t -> list.stream().map(BillPurchaseReturnLine::getStockId).collect(Collectors.toList()).contains(t.getStockId()))
                            .map(line -> {
                                AccountsPayableAccounting accounting = new AccountsPayableAccounting();
                                switch (typeEnum) {
                                    case PRE_RECEIVE_AMOUNT:
                                        accounting.setSerialNo(SerialNoGenerator.generatePreReceiveAmountSerialNo());
                                        break;
                                    case AMOUNT_RECEIVABLE:
                                        accounting.setSerialNo(SerialNoGenerator.generateAmountReceivableSerialNo());
                                        break;
                                    case AMOUNT_PAYABLE:
                                        accounting.setSerialNo(SerialNoGenerator.generateAmountPayableSerialNo());
                                        break;
                                    case PRE_PAID_AMOUNT:
                                        accounting.setSerialNo(SerialNoGenerator.generatePrePaidAmountSerialNo());
                                        break;
                                }
                                accounting.setOriginSerialNo(purchaseReturn.getSerialNo());
                                accounting.setAfpSerialNo("-");
                                accounting.setArcSerialNo(arcSerialNo);
                                accounting.setStockId(line.getStockId());
                                accounting.setStockSn(line.getStockSn());
                                accounting.setBrandName(line.getBrandName());
                                accounting.setSeriesName(line.getSeriesName());
                                accounting.setModel(line.getModel());
                                accounting.setTotalPrice(line.getPurchasePrice());
                                accounting.setCustomerId(purchaseReturn.getCustomerId());
                                accounting.setApplicant(purchaseReturn.getCreatedBy());
                                accounting.setShopId(purchaseReturn.getStoreId());
                                accounting.setBelongId(purchase.getPurchaseSubjectId());
                                accounting.setCustomerContactId(purchaseReturn.getCustomerContactId());
                                accounting.setSalesMethod(PurchaseModeEnum.convert(purchase.getPurchaseMode()));
                                accounting.setCustomerType(customer.getType().getValue());
                                accounting.setType(typeEnum);
                                accounting.setClassification(FinancialClassificationEnum.CG_TH);
                                accounting.setStatus(returnPendingReview);
                                accounting.setOriginType(OriginTypeEnum.CG_TH);
                                return accounting;
                            })
                            .collect(Collectors.toList());
                    this.baseMapper.insertBatchSomeColumn(accountingList);
                });
    }

    /**
     * 销售应收应付单
     *
     * @param map
     * @param typeEnum
     * @param pendingReview
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Integer> createSaleApa(Map<String, List<Integer>> map, ReceiptPaymentTypeEnum typeEnum, FinancialStatusEnum pendingReview) {

        List<Integer> result = new ArrayList<>();

        Map<String, AccountStockRelation> relationMap = stockRelationMapper.selectList(new LambdaQueryWrapper<AccountStockRelation>()
                        .in(AccountStockRelation::getOriginSerialNo, map.keySet()))
                .stream()
                .collect(Collectors.toMap(AccountStockRelation::getOriginSerialNo, Function.identity(), (d1, d2) -> d1));
        log.info("log for createApa map:{} type:{} status:{}", map, typeEnum.getDesc(), pendingReview.getDesc());

        map.forEach((serialNo, stockIds) -> {
            BillSaleOrder saleOrder = saleOrderMapper.selectOne(new LambdaQueryWrapper<BillSaleOrder>()
                    .eq(BillSaleOrder::getSerialNo, serialNo));
            if (ObjectUtils.isNotEmpty(saleOrder)) {
                Customer customer = customerMapper.selectById(saleOrder.getCustomerId());
                List<AccountsPayableAccounting> accountingList = saleOrderLineMapper.selectBySaleId(saleOrder.getId())
                        .stream()
                        .filter(t -> stockIds.contains(t.getStockId()))
                        .map(line -> {
                            AccountsPayableAccounting accounting = new AccountsPayableAccounting();
                            switch (typeEnum) {
                                case PRE_RECEIVE_AMOUNT:
                                    accounting.setSerialNo(SerialNoGenerator.generatePreReceiveAmountSerialNo());
                                    break;
                                case AMOUNT_RECEIVABLE:
                                    accounting.setSerialNo(SerialNoGenerator.generateAmountReceivableSerialNo());
                                    break;
                                case AMOUNT_PAYABLE:
                                    accounting.setSerialNo(SerialNoGenerator.generateAmountPayableSerialNo());
                                    break;
                                case PRE_PAID_AMOUNT:
                                    accounting.setSerialNo(SerialNoGenerator.generatePrePaidAmountSerialNo());
                                    break;
                            }
                            if (relationMap.containsKey(serialNo)) {
                                accounting.setArcSerialNo(Optional.of(receiptConfirmMapper.selectById(
                                                relationMap.get(serialNo).getArcId()))
                                        .orElseGet(AccountReceiptConfirm::new).getSerialNo());
                            }
                            accounting.setOriginSerialNo(serialNo);
                            accounting.setStockId(line.getStockId());
                            accounting.setStockSn(line.getStockSn());
                            accounting.setBrandName(line.getBrandName());
                            accounting.setSeriesName(line.getSeriesName());
                            accounting.setModel(line.getModel());
                            accounting.setTotalPrice(SaleOrderModeEnum.CONSIGN_FOR_SALE.equals(saleOrder.getSaleMode()) ? line.getPreClinchPrice() : line.getClinchPrice());
                            accounting.setCustomerId(saleOrder.getCustomerId());
                            accounting.setApplicant(saleOrder.getCreatedBy());
                            accounting.setShopId(saleOrder.getShopId());
                            accounting.setCustomerContactId(saleOrder.getCustomerContactId());
                            accounting.setSalesMethod(SaleOrderModeEnum.convert(saleOrder.getSaleMode()));
                            accounting.setCustomerType(Objects.nonNull(customer) ? customer.getType().getValue() : null);
                            accounting.setType(typeEnum);
                            accounting.setClassification(BusinessBillTypeEnum.convertClassification(saleOrder.getSaleSource()));
                            accounting.setStatus(pendingReview);
                            accounting.setOriginType(OriginTypeEnum.XS);
                            accounting.setWaitAuditPrice(FinancialStatusEnum.AUDITED.equals(pendingReview) ? BigDecimal.ZERO : accounting.getTotalPrice());
                            return accounting;
                        })
                        .collect(Collectors.toList());
                this.baseMapper.insertBatchSomeColumn(accountingList);

                if (CollectionUtils.isNotEmpty(accountingList) && accountingList.stream().allMatch(Objects::nonNull)) {
                    result.addAll(accountingList.stream().map(AccountsPayableAccounting::getId).collect(Collectors.toList()));
                }

            }
        });

        return result;
    }

    /**
     * 销售退货应收应付单
     *
     * @param map
     * @param typeEnum
     * @param returnPendingReview
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSaleApaByReturn(Map<Integer, List<Integer>> map, ReceiptPaymentTypeEnum typeEnum, FinancialStatusEnum returnPendingReview) {
        log.info("log for createApa map:{} type:{} status:{}", map, typeEnum.getDesc(), returnPendingReview.getDesc());

        map.forEach((returnId, stockIds) -> {
            BillSaleReturnOrder saleReturnOrder = returnOrderMapper.selectById(returnId);

            Customer customer = customerMapper.selectById(saleReturnOrder.getCustomerId());
            List<AccountsPayableAccounting> accountingList = returnOrderLineMapper.selectBySaleReturnId(saleReturnOrder.getId())
                    .stream()
                    .filter(t -> stockIds.contains(t.getStockId()))
                    .map(line -> {
                        AccountsPayableAccounting accounting = new AccountsPayableAccounting();
                        switch (typeEnum) {
                            case PRE_RECEIVE_AMOUNT:
                                accounting.setSerialNo(SerialNoGenerator.generatePreReceiveAmountSerialNo());
                                break;
                            case AMOUNT_RECEIVABLE:
                                accounting.setSerialNo(SerialNoGenerator.generateAmountReceivableSerialNo());
                                break;
                            case AMOUNT_PAYABLE:
                                accounting.setSerialNo(SerialNoGenerator.generateAmountPayableSerialNo());
                                break;
                            case PRE_PAID_AMOUNT:
                                accounting.setSerialNo(SerialNoGenerator.generatePrePaidAmountSerialNo());
                                break;
                        }
                        accounting.setOriginSerialNo(saleReturnOrder.getSerialNo());
                        accounting.setAfpSerialNo("-");
                        accounting.setStockId(line.getStockId());
                        accounting.setStockSn(line.getStockSn());
                        accounting.setBrandName(line.getBrandName());
                        accounting.setSeriesName(line.getSeriesName());
                        accounting.setModel(line.getModel());
                        accounting.setTotalPrice(line.getReturnPrice().negate());
                        accounting.setCustomerId(saleReturnOrder.getCustomerId());
                        accounting.setApplicant(saleReturnOrder.getCreatedBy());
                        accounting.setShopId(saleReturnOrder.getShopId());
                        accounting.setCustomerContactId(saleReturnOrder.getCustomerContactId());
                        accounting.setSalesMethod(SaleOrderModeEnum.convert(SaleOrderModeEnum
                                .fromCode(saleOrderLineMapper.selectSaleModeById(line.getSaleLineId()))));
                        accounting.setCustomerType(customer.getType().getValue());
                        accounting.setType(typeEnum);
                        accounting.setClassification(saleReturnOrder.getSaleReturnType().equals(SaleReturnOrderTypeEnum.TO_B_JS_TH) ? FinancialClassificationEnum.TH_XS : FinancialClassificationEnum.GR_XS);
                        accounting.setStatus(returnPendingReview);
                        accounting.setOriginType(OriginTypeEnum.XS);
                        accounting.setWaitAuditPrice(FinancialStatusEnum.AUDITED.equals(returnPendingReview) ? BigDecimal.ZERO : accounting.getTotalPrice());
                        return accounting;
                    })
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(accountingList))
                this.baseMapper.insertBatchSomeColumn(accountingList);
        });
    }

    @Override
    public List<AccountsPayableAccounting> selectListByStockSnAndType(List<Integer> stockSnList, ReceiptPaymentTypeEnum amountPayable) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<AccountsPayableAccounting>()
                .in(AccountsPayableAccounting::getStockId, stockSnList)
                .eq(AccountsPayableAccounting::getType, amountPayable));
    }

    @Override
    public void createSpecialApa(BillSaleOrder saleOrder, ReceiptPaymentTypeEnum typeEnum, FinancialStatusEnum pendingReview, List<Integer> stockIds, BigDecimal price, boolean b) {
        if (ObjectUtils.isNotEmpty(saleOrder)) {
            Customer customer = customerMapper.selectById(saleOrder.getCustomerId());
            List<AccountsPayableAccounting> accountingList = saleOrderLineMapper.selectBySaleId(saleOrder.getId())
                    .stream()
                    .filter(t -> stockIds.contains(t.getStockId()))
                    .map(line -> {
                        AccountsPayableAccounting accounting = new AccountsPayableAccounting();
                        switch (typeEnum) {
                            case PRE_RECEIVE_AMOUNT:
                                accounting.setSerialNo(SerialNoGenerator.generatePreReceiveAmountSerialNo());
                                break;
                            case AMOUNT_RECEIVABLE:
                                accounting.setSerialNo(SerialNoGenerator.generateAmountReceivableSerialNo());
                                break;
                            case AMOUNT_PAYABLE:
                                accounting.setSerialNo(SerialNoGenerator.generateAmountPayableSerialNo());
                                break;
                            case PRE_PAID_AMOUNT:
                                accounting.setSerialNo(SerialNoGenerator.generatePrePaidAmountSerialNo());
                                break;
                        }
                        accounting.setOriginSerialNo(saleOrder.getSerialNo());
                        accounting.setStockId(line.getStockId());
                        accounting.setStockSn(line.getStockSn());
                        accounting.setBrandName(line.getBrandName());
                        accounting.setSeriesName(line.getSeriesName());
                        accounting.setModel(line.getModel());
                        accounting.setTotalPrice(price);
                        accounting.setCustomerId(saleOrder.getCustomerId());
                        accounting.setApplicant(saleOrder.getCreatedBy());
                        accounting.setShopId(saleOrder.getShopId());
                        accounting.setCustomerContactId(saleOrder.getCustomerContactId());
                        accounting.setSalesMethod(SaleOrderModeEnum.convert(saleOrder.getSaleMode()));
                        accounting.setCustomerType(Objects.nonNull(customer) ? customer.getType().getValue() : null);
                        accounting.setType(typeEnum);
                        accounting.setClassification(BusinessBillTypeEnum.convertClassification(saleOrder.getSaleSource()));
                        accounting.setStatus(pendingReview);
                        accounting.setOriginType(OriginTypeEnum.XS);
                        accounting.setWaitAuditPrice(BigDecimal.ZERO);
                        accounting.setAuditTime(new Date());
                        accounting.setAuditor(UserContext.getUser().getUserName());
                        accounting.setAuditDescription(FlywheelConstant.SETTLEMENT_AUDIT);
                        return accounting;
                    })
                    .collect(Collectors.toList());
            this.baseMapper.insertBatchSomeColumn(accountingList);
        }
    }
}




