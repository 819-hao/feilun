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
import com.seeease.flywheel.serve.financial.entity.*;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.mapper.AccountStockRelationMapper;
import com.seeease.flywheel.serve.financial.mapper.AccountsPayableAccountingMapper;
import com.seeease.flywheel.serve.financial.mapper.AuditLoggingDetailMapper;
import com.seeease.flywheel.serve.financial.mapper.AuditLoggingMapper;
import com.seeease.flywheel.serve.financial.template.payment.PaymentDTemplate;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2024/1/11 14:55
 */
@Component
public class PaymentDTemplateImpl implements PaymentDTemplate {

    @Resource
    private CustomerService customerService;

    @Resource
    private StockService stockService;

    @Resource
    private AccountsPayableAccountingMapper accountsPayableAccountingMapper;

    @Resource
    private AuditLoggingMapper auditLoggingMapper;

    @Resource
    private AuditLoggingDetailMapper auditLoggingDetailMapper;

    @Resource
    private AccountStockRelationMapper accountStockRelationMapper;

    /**
     * 商品入库生成应付单
     *
     * @param request
     */
    @Override
    public void generatePayable(JSONObject request) {

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

        ArrayList<@Nullable AccountsPayableAccounting> list = Lists.newArrayList();

        for (BillPurchaseLine billPurchaseLine : purchaseLineList) {

            AccountsPayableAccounting payableAccounting = new AccountsPayableAccounting();
            payableAccounting.setSerialNo(SerialNoGenerator.generateAmountPayableSerialNo());
            payableAccounting.setType(ReceiptPaymentTypeEnum.AMOUNT_PAYABLE);

            FinancialSalesMethodEnum salesMethod;
            FinancialClassificationEnum financialClassificationEnum;

            switch (purchase.getPurchaseSource()) {
                case TH_CG_PL:
                    salesMethod = FinancialSalesMethodEnum.PURCHASE_BATCH;
                    financialClassificationEnum = FinancialClassificationEnum.TH_CG;
                    break;
                case TH_JS:
                    salesMethod = FinancialSalesMethodEnum.PURCHASE_C;
                    financialClassificationEnum = FinancialClassificationEnum.TH_CG;
                    break;
                case GR_JS:
                    salesMethod = FinancialSalesMethodEnum.PURCHASE_C;
                    financialClassificationEnum = FinancialClassificationEnum.GR_HS;
                    break;
                default:
                    throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
            }

            //单数据
            payableAccounting.setSalesMethod(salesMethod);
            payableAccounting.setClassification(financialClassificationEnum);
            payableAccounting.setOriginSerialNo(purchase.getSerialNo());
            payableAccounting.setShopId(purchase.getStoreId());
            payableAccounting.setBelongId(purchase.getPurchaseSubjectId());
            payableAccounting.setStatus(FinancialStatusEnum.PENDING_REVIEW);
            payableAccounting.setCustomerId(purchase.getCustomerId());
            payableAccounting.setCustomerContactId(purchase.getCustomerContactId());
            payableAccounting.setCustomerType(Objects.nonNull(customer) ? customer.getType().getValue() : null);
            payableAccounting.setApplicant(purchase.getCreatedBy());
            payableAccounting.setWhetherUse(WhetherEnum.NO.getValue());
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

    @Override
    public void updatePayable(JSONObject request) {
        ApplyFinancialPayment applyFinancialPayment = request.getObject("afp", ApplyFinancialPayment.class);

        Assert.notNull(applyFinancialPayment, "申请打款单不能为空");

        List<AccountStockRelation> accountStockRelationList = accountStockRelationMapper.selectList(Wrappers.<AccountStockRelation>lambdaQuery().eq(AccountStockRelation::getAfpId, applyFinancialPayment.getId()));

        Assert.notEmpty(accountStockRelationList, "没有关联表数据");
        Assert.isTrue(accountStockRelationList.stream().allMatch(Objects::nonNull), "关联表数据错误");

        Map<String, List<AccountStockRelation>> map = accountStockRelationList.stream().collect(Collectors.groupingBy(AccountStockRelation::getOriginSerialNo));

        ArrayList<@Nullable AccountsPayableAccounting> list = Lists.newArrayList();
        //同一个采购单 分组处理
        for (Map.Entry<String, List<AccountStockRelation>> entry : map.entrySet()) {

            List<AccountsPayableAccounting> accountsPayableAccountingList = accountsPayableAccountingMapper.selectList(Wrappers.<AccountsPayableAccounting>lambdaQuery()
                    .in(AccountsPayableAccounting::getStockId, entry.getValue().stream().map(AccountStockRelation::getStockId).collect(Collectors.toList()))
                    .eq(AccountsPayableAccounting::getType, ReceiptPaymentTypeEnum.AMOUNT_PAYABLE)
                    .eq(AccountsPayableAccounting::getOriginSerialNo, entry.getKey()));

            Assert.notEmpty(accountsPayableAccountingList, "应收应付单不能为空");

            //批量修改
            for (AccountsPayableAccounting accountsPayableAccounting : accountsPayableAccountingList) {
                AccountsPayableAccounting payableAccounting = new AccountsPayableAccounting();
                payableAccounting.setId(accountsPayableAccounting.getId());
                payableAccounting.setAfpSerialNo(applyFinancialPayment.getSerialNo());
                payableAccounting.setAfpId(applyFinancialPayment.getId());
                payableAccounting.setStatus(FinancialStatusEnum.IN_REVIEW);
                accountsPayableAccountingMapper.updateById(payableAccounting);
            }

            list.addAll(accountsPayableAccountingList);
        }

        AuditLogging auditLogging = AuditLoggingConvert.INSTANCE.convertAccountsPayableAccounting(list.get(FlywheelConstant.INDEX));
        auditLogging.setId(null);
        auditLogging.setAuditTime(new Date());
        auditLogging.setAuditName(UserContext.getUser().getUserName());
        auditLogging.setAuditDescription(FlywheelConstant.AUTOMATIC_SYSTEM);
        auditLogging.setNumber(accountStockRelationList.size());
        auditLogging.setAfpId(applyFinancialPayment.getId());
        auditLogging.setAfpSerialNo(applyFinancialPayment.getSerialNo());
        auditLogging.setApplicant(applyFinancialPayment.getCreatedBy());
        auditLogging.setStatus(FinancialStatusEnum.IN_REVIEW);

        auditLoggingMapper.insert(auditLogging);

        ArrayList<@Nullable AuditLoggingDetail> arrayList = Lists.newArrayList();

        for (AccountsPayableAccounting accountsPayableAccounting : list) {

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

            AccountsPayableAccounting payableAccounting = new AccountsPayableAccounting();
            payableAccounting.setId(accountsPayableAccounting.getId());
            payableAccounting.setAfpSerialNo(applyFinancialPayment.getSerialNo());
            payableAccounting.setAfpId(applyFinancialPayment.getId());

            accountsPayableAccountingMapper.updateById(payableAccounting);

            arrayList.add(detail);
        }
        auditLoggingDetailMapper.insertBatchSomeColumn(arrayList);
    }

    /**
     * 申请打款单关联表 && 同时关联采购单
     * 客户一样 采购类型一样
     * 打款成功
     *
     * @param request
     */
    @Override
    public void listenerVerification(JSONObject request) {

        ApplyFinancialPayment applyFinancialPayment = request.getObject("afp", ApplyFinancialPayment.class);

        Assert.notNull(applyFinancialPayment, "申请打款单不能为空");

        List<AccountStockRelation> accountStockRelationList = accountStockRelationMapper.selectList(Wrappers.<AccountStockRelation>lambdaQuery().eq(AccountStockRelation::getAfpId, applyFinancialPayment.getId()));

        Assert.notEmpty(accountStockRelationList, "没有关联表数据");
        Assert.isTrue(accountStockRelationList.stream().allMatch(Objects::nonNull), "关联表数据错误");

        Map<String, List<AccountStockRelation>> map = accountStockRelationList.stream().collect(Collectors.groupingBy(AccountStockRelation::getOriginSerialNo));

        ArrayList<@Nullable AccountsPayableAccounting> list = Lists.newArrayList();
        //同一个采购单 分组处理
        for (Map.Entry<String, List<AccountStockRelation>> entry : map.entrySet()) {

            List<AccountsPayableAccounting> accountsPayableAccountingList = accountsPayableAccountingMapper.selectList(Wrappers.<AccountsPayableAccounting>lambdaQuery()
                    .in(AccountsPayableAccounting::getStockId, entry.getValue().stream().map(AccountStockRelation::getStockId).collect(Collectors.toList()))
                    .eq(AccountsPayableAccounting::getType, ReceiptPaymentTypeEnum.AMOUNT_PAYABLE)
                    .eq(AccountsPayableAccounting::getOriginSerialNo, entry.getKey()));

            Assert.notEmpty(accountsPayableAccountingList, "应收应付单不能为空");

            //批量修改
            for (AccountsPayableAccounting accountsPayableAccounting : accountsPayableAccountingList) {
                AccountsPayableAccounting payableAccounting = new AccountsPayableAccounting();
                payableAccounting.setId(accountsPayableAccounting.getId());
                payableAccounting.setAfpSerialNo(applyFinancialPayment.getSerialNo());
                payableAccounting.setAfpId(applyFinancialPayment.getId());
                payableAccounting.setStatus(FinancialStatusEnum.AUDITED);
                payableAccounting.setAuditTime(new Date());
                payableAccounting.setAuditor(UserContext.getUser().getUserName());
                accountsPayableAccountingMapper.updateById(payableAccounting);
            }

            list.addAll(accountsPayableAccountingList);
        }

        AuditLogging auditLogging = AuditLoggingConvert.INSTANCE.convertAccountsPayableAccounting(list.get(FlywheelConstant.INDEX));
        auditLogging.setId(null);
        auditLogging.setAuditTime(new Date());
        auditLogging.setAuditName(UserContext.getUser().getUserName());
        auditLogging.setAuditDescription(FlywheelConstant.PAYMENT_AUDIT);
        auditLogging.setNumber(accountStockRelationList.size());
        auditLogging.setAfpId(applyFinancialPayment.getId());
        auditLogging.setAfpSerialNo(applyFinancialPayment.getSerialNo());
        auditLogging.setApplicant(applyFinancialPayment.getCreatedBy());
        auditLogging.setStatus(FinancialStatusEnum.AUDITED);

        auditLoggingMapper.insert(auditLogging);

        ArrayList<@Nullable AuditLoggingDetail> arrayList = Lists.newArrayList();

        for (AccountsPayableAccounting accountsPayableAccounting : list) {

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

            AccountsPayableAccounting payableAccounting = new AccountsPayableAccounting();
            payableAccounting.setId(accountsPayableAccounting.getId());
            payableAccounting.setAfpSerialNo(applyFinancialPayment.getSerialNo());
            payableAccounting.setAfpId(applyFinancialPayment.getId());

            accountsPayableAccountingMapper.updateById(payableAccounting);

            arrayList.add(detail);
        }
        auditLoggingDetailMapper.insertBatchSomeColumn(arrayList);
    }
}
