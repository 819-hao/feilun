package com.seeease.flywheel.serve.financial.template.payment.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.convert.AuditLoggingConvert;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPayment;
import com.seeease.flywheel.serve.financial.entity.AuditLogging;
import com.seeease.flywheel.serve.financial.entity.AuditLoggingDetail;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.mapper.AccountsPayableAccountingMapper;
import com.seeease.flywheel.serve.financial.mapper.ApplyFinancialPaymentMapper;
import com.seeease.flywheel.serve.financial.mapper.AuditLoggingDetailMapper;
import com.seeease.flywheel.serve.financial.mapper.AuditLoggingMapper;
import com.seeease.flywheel.serve.financial.template.payment.PaymentCTemplate;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.storework.enums.StoreWorkReturnTypeEnum;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PaymentCTemplateImpl implements PaymentCTemplate {

    @Autowired
    private ApplyFinancialPaymentMapper applyFinancialPaymentMapper;

    @Resource
    private StockService stockService;

    @Resource
    private CustomerService customerService;

    @Resource
    private AccountsPayableAccountingMapper accountsPayableAccountingMapper;
    @Resource
    private AuditLoggingMapper auditLoggingMapper;

    @Resource
    private AuditLoggingDetailMapper auditLoggingDetailMapper;


    /**
     * 上传快递单后 生成预付单
     * 此时已生成有 采购单&&采购单行&&申请打款单（前置绑定）
     *
     * @param request
     */
    @Override
    public void generatePrepaid(JSONObject request) {
        //采购单
        BillPurchase purchase = request.getObject("purchase", BillPurchase.class);
        Assert.notNull(purchase, "采购单不能为空");

        //采购行
        List<BillPurchaseLine> purchaseLineList = request.getJSONArray("purchaseLine").toJavaList(BillPurchaseLine.class);
        Assert.notEmpty(purchaseLineList, "采购行不能为空");
        Assert.isTrue(purchaseLineList.stream().allMatch(Objects::nonNull), "采购行数据不能为空");

        ApplyFinancialPayment applyFinancialPayment = applyFinancialPaymentMapper.selectOne(
                Wrappers.<ApplyFinancialPayment>lambdaQuery().eq(ApplyFinancialPayment::getSerialNo, purchase.getApplyPaymentSerialNo())
        );
        Assert.notNull(applyFinancialPayment, "申请打款单不能为空");
        //表数据
        Map<Integer, StockExt> stockMap = Optional.ofNullable(purchaseLineList.stream().map(BillPurchaseLine::getStockId).filter(Objects::nonNull).collect(Collectors.toList())).filter(CollectionUtils::isNotEmpty).map(ids -> stockService.selectByStockIdList(ids).stream().collect(Collectors.toMap(StockExt::getStockId, Function.identity()))).orElse(Collections.EMPTY_MAP);

        //客户数据
        Customer customer = customerService.getById(purchase.getCustomerId());
        Assert.notNull(customer, "客户不能为空");

        ArrayList<@Nullable AccountsPayableAccounting> list = Lists.newArrayList();

        for (BillPurchaseLine billPurchaseLine : purchaseLineList) {

            AccountsPayableAccounting payableAccounting = new AccountsPayableAccounting();
            payableAccounting.setSerialNo(SerialNoGenerator.generatePrePaidAmountSerialNo());
            payableAccounting.setType(ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT);

            FinancialSalesMethodEnum salesMethod;
            FinancialClassificationEnum financialClassificationEnum;

            switch (purchase.getPurchaseSource()) {
                case TH_CG_BH:
                    salesMethod = FinancialSalesMethodEnum.PURCHASE_PREPARE;
                    financialClassificationEnum = FinancialClassificationEnum.TH_CG;
                    break;
                case TH_CG_DJ:
                    salesMethod = FinancialSalesMethodEnum.PURCHASE_DEPOSIT;
                    financialClassificationEnum = FinancialClassificationEnum.TH_CG;
                    break;
                case GR_HS_JHS:
                    salesMethod = FinancialSalesMethodEnum.PURCHASE_RECYCLE;
                    financialClassificationEnum = FinancialClassificationEnum.GR_HS;
                    break;
                default:
                    throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
            }

            //单数据
            payableAccounting.setSalesMethod(salesMethod);
            payableAccounting.setClassification(financialClassificationEnum);
            payableAccounting.setOriginSerialNo(purchase.getSerialNo());
            payableAccounting.setAfpSerialNo(purchase.getApplyPaymentSerialNo());
            payableAccounting.setAfpId(applyFinancialPayment.getId());
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

            list.add(payableAccounting);
        }

        accountsPayableAccountingMapper.insertBatchSomeColumn(list);
    }

    /**
     * 商品入库 采购单&&
     *
     * @param request
     */
    @Override
    public void listenerVerification(JSONObject request) {

        Integer stockId = request.getInteger("stockId");
        String originSerialNo = request.getString("originSerialNo");
        Integer returnType = request.getInteger("returnType");

        AccountsPayableAccounting payableAccounting = accountsPayableAccountingMapper.selectOne(Wrappers.<AccountsPayableAccounting>lambdaQuery()
                .eq(AccountsPayableAccounting::getStockId, stockId)
                .eq(AccountsPayableAccounting::getType, Objects.isNull(returnType) ? ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT : ReceiptPaymentTypeEnum.AMOUNT_PAYABLE)
                .eq(AccountsPayableAccounting::getOriginSerialNo, originSerialNo));

        Assert.notNull(payableAccounting, "应收应付单不能为空");

        AccountsPayableAccounting accounting = new AccountsPayableAccounting();

        accounting.setId(payableAccounting.getId());
        accounting.setStatus(FinancialStatusEnum.AUDITED);
        accounting.setAuditDescription(Objects.isNull(returnType) ? FlywheelConstant.IN_STORE_AUDIT : StoreWorkReturnTypeEnum.fromCode(returnType) == StoreWorkReturnTypeEnum.OUT_STORE ? FlywheelConstant.RETURN_EXCHANGE_AUDIT : FlywheelConstant.EXCHANGE_AUDIT);
        accounting.setWaitAuditPrice(BigDecimal.ZERO);
        accounting.setAuditTime(new Date());
        accounting.setAuditor(UserContext.getUser().getUserName());
        this.accountsPayableAccountingMapper.updateById(accounting);

        //查询内容
        AccountsPayableAccounting accountsPayableAccounting = accountsPayableAccountingMapper.selectById(payableAccounting.getId());

        AuditLogging auditLogging = AuditLoggingConvert.INSTANCE.convertAccountsPayableAccounting(accountsPayableAccounting);
        auditLogging.setId(null);
        auditLogging.setAuditTime(new Date());
        auditLogging.setAuditDescription(Objects.isNull(returnType) ? FlywheelConstant.IN_STORE_AUDIT : StoreWorkReturnTypeEnum.fromCode(returnType) == StoreWorkReturnTypeEnum.OUT_STORE ? FlywheelConstant.RETURN_EXCHANGE_AUDIT : FlywheelConstant.EXCHANGE_AUDIT);
        auditLogging.setAuditName(UserContext.getUser().getUserName());
        auditLogging.setStatus(FinancialStatusEnum.AUDITED);
        auditLogging.setNumber(1);

        auditLoggingMapper.insert(auditLogging);

        AuditLoggingDetail detail = AuditLoggingConvert.INSTANCE.convertAuditLoggingDetail(accountsPayableAccounting);

        detail.setId(null);
        detail.setAuditLoggingId(auditLogging.getId());
        detail.setApaId(accountsPayableAccounting.getId());
        detail.setSerialNo(accountsPayableAccounting.getSerialNo());
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
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }
        auditLoggingDetailMapper.insert(detail);
    }
}
