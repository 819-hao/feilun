package com.seeease.flywheel.serve.financial.template.payment.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.request.AccountReceiptConfirmAddRequest;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentCreateRequest;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentCreateResult;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.convert.AuditLoggingConvert;
import com.seeease.flywheel.serve.financial.entity.*;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.mapper.*;
import com.seeease.flywheel.serve.financial.service.AccountReceiptConfirmService;
import com.seeease.flywheel.serve.financial.service.ApplyFinancialPaymentService;
import com.seeease.flywheel.serve.financial.template.payment.PaymentETemplate;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.FinancialStatementCompany;
import com.seeease.flywheel.serve.maindata.service.FinancialStatementCompanyService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2024/1/11 14:55
 */
@Component
@Slf4j
public class PaymentETemplateImpl implements PaymentETemplate {

    @Resource
    private CustomerService customerService;

    @Resource
    private CustomerContactsService customerContactsService;

    @Resource
    private StockService stockService;

    @Resource
    private ApplyFinancialPaymentService applyFinancialPaymentService;

    @Resource
    private ApplyFinancialPaymentMapper applyFinancialPaymentMapper;

    @Resource
    private AccountsPayableAccountingMapper accountsPayableAccountingMapper;

    @Resource
    private AuditLoggingMapper auditLoggingMapper;

    @Resource
    private AuditLoggingDetailMapper auditLoggingDetailMapper;

    @Resource
    private AccountStockRelationMapper accountStockRelationMapper;

    @Resource
    private AccountReceiptConfirmService accountReceiptConfirmService;
    @Resource
    private AccountReceiptConfirmMapper accountReceiptConfirmMapper;

    @Resource
    private FinancialStatementCompanyService financialStatementCompanyService;

    @Resource
    private BillPurchaseService billPurchaseService;

    @Override
    public void createPaymentAndGeneratePayable(JSONObject request) {

        //采购单
        BillPurchase purchase = request.getObject("purchase", BillPurchase.class);
        Assert.notNull(purchase, "采购单不能为空");

        //采购行
        List<BillPurchaseLine> purchaseLineList = request.getJSONArray("purchaseLine").toJavaList(BillPurchaseLine.class);
        Assert.notEmpty(purchaseLineList, "采购行不能为空");
        Assert.isTrue(purchaseLineList.stream().allMatch(Objects::nonNull), "采购行数据不能为空");

        Map<Integer, StockExt> stockMap = Optional.ofNullable(purchaseLineList.stream().map(BillPurchaseLine::getStockId).filter(Objects::nonNull).collect(Collectors.toList())).filter(CollectionUtils::isNotEmpty).map(ids -> stockService.selectByStockIdList(ids).stream().collect(Collectors.toMap(StockExt::getStockId, Function.identity()))).orElse(Collections.EMPTY_MAP);

        Customer customer = customerService.getById(purchase.getCustomerId());
        Assert.notNull(customer, "客户不能为空");

        ApplyFinancialPaymentCreateResult paymentCreateResult = null;

        //申请打款
        ApplyFinancialPaymentCreateRequest.ApplyFinancialPaymentCreateRequestBuilder builder = ApplyFinancialPaymentCreateRequest.builder().originSerialNo(purchase.getSerialNo())

                .payment(ApplyFinancialPaymentEnum.PAID.getValue()).typePayment(ApplyFinancialPaymentTypeEnum.PERSONAL_RECYCLING.getValue()).subjectPayment(purchase.getPurchaseSubjectId()).bankAccount(purchase.getBankAccount()).bankCard(purchase.getBankAccount()).bankCustomerName(purchase.getBankCustomerName()).bankName(purchase.getAccountName()).customerName(customer.getCustomerName()).demanderStoreId(purchase.getDemanderStoreId()).shopId(UserContext.getUser().getStore().getId()).manualCreation(WhetherEnum.NO.getValue()).whetherUse(1).batchPictureUrl(purchase.getBatchPictureUrl());

        AccountReceiptConfirm accountReceiptConfirm = null;

        switch (purchase.getPaymentMethod()) {
            case FK_QK:
                paymentCreateResult = applyFinancialPaymentService.create(builder.pricePayment(purchase.getTotalPurchasePrice()).salesMethod(FinancialSalesMethodEnum.PURCHASE_FUll_AMOUNT.getValue()).build());//
                break;
            case FK_CE:
                //打款
                if (purchase.getSalePrice().compareTo(purchase.getTotalPurchasePrice()) < 0) {
                    //申请打款
                    paymentCreateResult = applyFinancialPaymentService.create(builder.pricePayment(purchase.getTotalPurchasePrice().subtract(purchase.getSalePrice())).salesMethod(FinancialSalesMethodEnum.PURCHASE_D.getValue()).build());
                } else if (purchase.getSalePrice().compareTo(purchase.getTotalPurchasePrice()) > 0) {

                    //确认收款
                    accountReceiptConfirm = accountReceiptConfirmService.accountReceiptConfirmAdd(AccountReceiptConfirmAddRequest.builder().receivableAmount(purchase.getTotalPurchasePrice().negate()).collectionType(CollectionTypeEnum.XF_TK.getValue()).waitAuditPrice(purchase.getTotalPurchasePrice()).status(AccountReceiptConfirmStatusEnum.WAIT.getValue()).shopId(purchase.getStoreId()).miniAppSource(Boolean.FALSE).classification(FinancialClassificationEnum.GR_HS.getValue()).salesMethod(FinancialSalesMethodEnum.PURCHASE_D.getValue()).payer("-").originSerialNo(purchase.getSerialNo()).statementCompanyId(financialStatementCompanyService.list(Wrappers.<FinancialStatementCompany>lambdaQuery().like(FinancialStatementCompany::getSubjectId, purchase.getPurchaseSubjectId())).stream().findFirst().get().getId()).customerName(customerService.getById(purchase.getCustomerId()).getCustomerName()).contactPhone(customerContactsService.getById(purchase.getCustomerContactId()).getPhone()).build());
                } else {
                    log.warn("平帐,purchase={}", purchase.getSerialNo());
                }
                break;
            default:
                break;
        }

        //创建申请打款单&关联关系
        if (Objects.nonNull(paymentCreateResult)) {

            for (BillPurchaseLine billPurchaseLine : purchaseLineList) {

                AccountStockRelation accountStockRelation = new AccountStockRelation();
                accountStockRelation.setOriginSerialNo(purchase.getSerialNo());
                accountStockRelation.setOriginPrice(billPurchaseLine.getPurchasePrice());
                accountStockRelation.setStockId(billPurchaseLine.getStockId());
                accountStockRelation.setAfpId(paymentCreateResult.getId());

                accountStockRelationMapper.insert(accountStockRelation);
            }
            BillPurchase billPurchase = new BillPurchase();
            billPurchase.setId(purchase.getId());
            billPurchase.setApplyPaymentSerialNo(paymentCreateResult.getSerialNo());
            //回填申请单到采购单
            billPurchaseService.updateById(billPurchase);
        }
        //创建确认收款单&关联关系
        if (Objects.nonNull(accountReceiptConfirm)) {
            for (BillPurchaseLine billPurchaseLine : purchaseLineList) {

                AccountStockRelation accountStockRelation = new AccountStockRelation();
                accountStockRelation.setOriginSerialNo(purchase.getSerialNo());
                accountStockRelation.setOriginPrice(billPurchaseLine.getPurchasePrice());
                accountStockRelation.setStockId(billPurchaseLine.getStockId());
                accountStockRelation.setArcId(accountReceiptConfirm.getId());

                accountStockRelationMapper.insert(accountStockRelation);
            }
        }

        for (BillPurchaseLine billPurchaseLine : purchaseLineList) {

            //创建应付单
            AccountsPayableAccounting payableAccounting = new AccountsPayableAccounting();
            payableAccounting.setSerialNo(SerialNoGenerator.generateAmountPayableSerialNo());
            payableAccounting.setType(ReceiptPaymentTypeEnum.AMOUNT_PAYABLE);

            //单数据
            payableAccounting.setSalesMethod(FinancialSalesMethodEnum.PURCHASE_DISPLACE);
            payableAccounting.setClassification(FinancialClassificationEnum.GR_HS);
            payableAccounting.setOriginSerialNo(purchase.getSerialNo());

            if (Objects.nonNull(paymentCreateResult)) {
                payableAccounting.setAfpSerialNo(paymentCreateResult.getSerialNo());
                payableAccounting.setAfpId(paymentCreateResult.getId());
            }

            if (Objects.nonNull(accountReceiptConfirm)) {
                payableAccounting.setArcSerialNo(accountReceiptConfirm.getSerialNo());
            }

            payableAccounting.setShopId(purchase.getStoreId());
            payableAccounting.setBelongId(purchase.getPurchaseSubjectId());
            payableAccounting.setStatus(FinancialStatusEnum.PENDING_REVIEW);
            payableAccounting.setCustomerId(purchase.getCustomerId());
            payableAccounting.setCustomerContactId(purchase.getCustomerContactId());
            payableAccounting.setCustomerType(Objects.nonNull(customer) ? customer.getType().getValue() : null);
            payableAccounting.setApplicant(purchase.getCreatedBy());
            payableAccounting.setWhetherUse(WhetherEnum.YES.getValue());
            payableAccounting.setPurchaseId(purchase.getPurchaseId());
            payableAccounting.setDemanderStoreId(purchase.getDemanderStoreId());
            payableAccounting.setOriginType(OriginTypeEnum.CG);

            StockExt ext = stockMap.get(billPurchaseLine.getStockId());

            //行数据
            payableAccounting.setTotalPrice(billPurchaseLine.getPurchasePrice());
            payableAccounting.setStockId(billPurchaseLine.getStockId());
            payableAccounting.setStockSn(billPurchaseLine.getStockSn());
            payableAccounting.setWaitAuditPrice(billPurchaseLine.getPurchasePrice());
            payableAccounting.setBrandName(ext.getBrandName());
            payableAccounting.setSeriesName(ext.getSeriesName());
            payableAccounting.setModel(ext.getModel());

            accountsPayableAccountingMapper.insert(payableAccounting);
        }
    }

    @Override
    public void listenerVerification(JSONObject request) {

        Integer node = request.getInteger("node");

        //核销->打款成功&&商品入库
        Integer stockId = request.getInteger("stockId");
        String originSerialNo = request.getString("originSerialNo");
        //置换采购单
        BillPurchase billPurchase = request.getObject("purchase", BillPurchase.class);

        ApplyFinancialPayment applyFinancialPayment = null;

        AccountReceiptConfirm accountReceiptConfirm = null;

        List<AccountStockRelation> accountStockRelationList = null;

        AccountStockRelation accountStockRelation = null;

        Boolean check = null;

        if (Objects.nonNull(billPurchase)) {
            switch (billPurchase.getPaymentMethod()) {
                case FK_QK:
                    //申请打款
                    accountStockRelationList = accountStockRelationMapper.selectList(Wrappers.<AccountStockRelation>lambdaQuery().eq(AccountStockRelation::getOriginSerialNo, originSerialNo).eq(AccountStockRelation::getStockId, stockId));

                    //自动创建关联
                    Assert.isTrue(CollectionUtils.isNotEmpty(accountStockRelationList), "关联表不能为空");
                    Assert.isTrue(accountStockRelationList.size() == 1, "关联数量不能为空");
                    accountStockRelation = accountStockRelationList.get(FlywheelConstant.INDEX);
                    applyFinancialPayment = applyFinancialPaymentMapper.selectById(accountStockRelation.getAfpId());

                    Assert.notNull(applyFinancialPayment, "申请打款单不能为空");
                    check = true;
                    break;
                case FK_CE:
                    //打款
                    if (billPurchase.getSalePrice().compareTo(billPurchase.getTotalPurchasePrice()) < 0) {
                        //申请打款
                        accountStockRelationList = accountStockRelationMapper.selectList(Wrappers.<AccountStockRelation>lambdaQuery().eq(AccountStockRelation::getOriginSerialNo, originSerialNo).eq(AccountStockRelation::getStockId, stockId));

                        //自动创建关联
                        Assert.isTrue(CollectionUtils.isNotEmpty(accountStockRelationList), "关联表不能为空");
                        Assert.isTrue(accountStockRelationList.size() == 1, "关联数量不能为空");
                        accountStockRelation = accountStockRelationList.get(FlywheelConstant.INDEX);
                        applyFinancialPayment = applyFinancialPaymentMapper.selectById(accountStockRelation.getAfpId());

                        Assert.notNull(applyFinancialPayment, "申请打款单不能为空");
                        check = true;
                    } else if (billPurchase.getSalePrice().compareTo(billPurchase.getTotalPurchasePrice()) > 0) {
                        //确认收款
                        accountStockRelationList = accountStockRelationMapper.selectList(Wrappers.<AccountStockRelation>lambdaQuery().eq(AccountStockRelation::getOriginSerialNo, originSerialNo).eq(AccountStockRelation::getStockId, stockId));

                        //自动创建关联
                        Assert.isTrue(CollectionUtils.isNotEmpty(accountStockRelationList), "关联表不能为空");
                        Assert.isTrue(accountStockRelationList.size() == 1, "关联数量不能为空");
                        accountStockRelation = accountStockRelationList.get(FlywheelConstant.INDEX);
                        accountReceiptConfirm = accountReceiptConfirmMapper.selectById(accountStockRelation.getArcId());

                        Assert.notNull(accountReceiptConfirm, "确认收款单不能为空");
                        check = false;
                    } else {
                        log.warn("平帐,purchase={}", billPurchase.getSerialNo());
                    }

                    break;
            }
        } else {
            //仅回收
            accountStockRelationList = accountStockRelationMapper.selectList(Wrappers.<AccountStockRelation>lambdaQuery().eq(AccountStockRelation::getOriginSerialNo, originSerialNo).eq(AccountStockRelation::getStockId, stockId));

            //自动创建关联
            Assert.isTrue(CollectionUtils.isNotEmpty(accountStockRelationList), "关联表不能为空");
            Assert.isTrue(accountStockRelationList.size() == 1, "关联数量不能为空");
            accountStockRelation = accountStockRelationList.get(FlywheelConstant.INDEX);
            applyFinancialPayment = applyFinancialPaymentMapper.selectById(accountStockRelation.getAfpId());

            Assert.notNull(applyFinancialPayment, "申请打款单不能为空");
            check = true;
        }

        Stock stock = stockService.getById(stockId);

        switch (node) {
            //入库成功 && 申请打款状态
            case 1:

                if (Objects.nonNull(check) && check) {
                    if (ObjectUtils.isEmpty(applyFinancialPayment) || !applyFinancialPayment.getState().equals(ApplyFinancialPaymentStateEnum.PAID)) {
                        log.warn("入库成功&未打款，表的id={}, 申请打款单号={}", stockId, applyFinancialPayment.getSerialNo());
                        return;
                    }
                } else if (Objects.nonNull(check) && !check) {
                    if (ObjectUtils.isEmpty(accountReceiptConfirm) || !accountReceiptConfirm.getStatus().equals(AccountReceiptConfirmStatusEnum.FINISH.getValue())) {
                        log.warn("入库成功&未收款，表的id={}, 确认收款单号={}", stockId, accountReceiptConfirm.getSerialNo());
                        return;
                    }
                }

                break;
            //打款成功 || 收款成功
            case 2:
                if (!Arrays.asList(StockStatusEnum.WAIT_PRICING, StockStatusEnum.MARKETABLE).contains(stock.getStockStatus())) {
                    return;
                }
                break;
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }

        AccountsPayableAccounting payableAccounting = accountsPayableAccountingMapper.selectOne(Wrappers.<AccountsPayableAccounting>lambdaQuery()
                .eq(AccountsPayableAccounting::getStockId, stockId)
                .eq(AccountsPayableAccounting::getType, ReceiptPaymentTypeEnum.AMOUNT_PAYABLE)
                .eq(AccountsPayableAccounting::getOriginSerialNo, originSerialNo));

        Assert.notNull(payableAccounting, "应收应付单不能为空");

        AccountsPayableAccounting accountsPayableAccounting = new AccountsPayableAccounting();
        accountsPayableAccounting.setId(payableAccounting.getId());
        if (Objects.nonNull(check) && check) {
            accountsPayableAccounting.setAfpSerialNo(applyFinancialPayment.getSerialNo());
            accountsPayableAccounting.setAfpId(applyFinancialPayment.getId());
        } else if (Objects.nonNull(check) && !check) {
            accountsPayableAccounting.setArcSerialNo(accountReceiptConfirm.getSerialNo());
        }
        accountsPayableAccounting.setStatus(FinancialStatusEnum.AUDITED);
        accountsPayableAccounting.setAuditDescription(node == 1 ? FlywheelConstant.IN_STORE_AUDIT : FlywheelConstant.PAYMENT_AUDIT);
        accountsPayableAccounting.setWaitAuditPrice(BigDecimal.ZERO);
        accountsPayableAccounting.setAuditor(UserContext.getUser().getUserName());
        accountsPayableAccounting.setAuditTime(new Date());
        accountsPayableAccountingMapper.updateById(accountsPayableAccounting);

        //查询内容
        AccountsPayableAccounting accounting = accountsPayableAccountingMapper.selectById(payableAccounting.getId());

        AuditLogging auditLogging = AuditLoggingConvert.INSTANCE.convertAccountsPayableAccounting(accounting);
        auditLogging.setId(null);
        auditLogging.setAuditTime(new Date());
        auditLogging.setAuditDescription(FlywheelConstant.IN_STORE_AUDIT);
        auditLogging.setAuditName(UserContext.getUser().getUserName());
        auditLogging.setStatus(FinancialStatusEnum.AUDITED);
        auditLogging.setNumber(1);

        auditLoggingMapper.insert(auditLogging);

        AuditLoggingDetail detail = AuditLoggingConvert.INSTANCE.convertAuditLoggingDetail(accounting);

        detail.setId(null);
        detail.setAuditLoggingId(auditLogging.getId());
        detail.setApaId(accounting.getId());
        detail.setSerialNo(accounting.getSerialNo());
        switch (auditLogging.getType()) {
            case PRE_PAID_AMOUNT:
                detail.setPrePaidAmount(accounting.getTotalPrice());
                break;
            case AMOUNT_PAYABLE:
                detail.setAmountPayable(accounting.getTotalPrice());
                break;
            case AMOUNT_RECEIVABLE:
                detail.setAmountReceivable(accounting.getTotalPrice());
                break;
            case PRE_RECEIVE_AMOUNT:
                detail.setPreReceiveAmount(accounting.getTotalPrice());
                break;
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }
        auditLoggingDetailMapper.insert(detail);
    }
}
