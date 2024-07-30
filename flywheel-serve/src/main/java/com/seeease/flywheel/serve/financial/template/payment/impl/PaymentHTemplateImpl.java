package com.seeease.flywheel.serve.financial.template.payment.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.request.AccountReceiptConfirmAddRequest;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.entity.AccountStockRelation;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.mapper.AccountsPayableAccountingMapper;
import com.seeease.flywheel.serve.financial.service.AccountReceiptConfirmService;
import com.seeease.flywheel.serve.financial.service.AccountStockRelationService;
import com.seeease.flywheel.serve.financial.template.payment.PaymentHTemplate;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturn;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturnLine;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
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
public class PaymentHTemplateImpl implements PaymentHTemplate {

    @Resource
    private StockService stockService;

    @Resource
    private CustomerService customerService;

    @Resource
    private CustomerContactsService customerContactsService;

    @Resource
    private AccountReceiptConfirmService accountReceiptConfirmService;

    @Resource
    private AccountStockRelationService accountStockRelationService;

    @Resource
    private AccountsPayableAccountingMapper accountsPayableAccountingMapper;

    @Resource
    private BillPurchaseService billPurchaseService;

    @Override
    public void createReceiptAndGeneratePayable(JSONObject request) {

        //采购退货单
        BillPurchaseReturn purchaseReturn = request.getObject("purchaseReturn", BillPurchaseReturn.class);
        Assert.notNull(purchaseReturn, "采购单不能为空");

        //采购退货行
        List<BillPurchaseReturnLine> javaList = request.getJSONArray("purchaseReturnLine").toJavaList(BillPurchaseReturnLine.class);
        Assert.isTrue(CollectionUtils.isNotEmpty(javaList), "采购退货行不能为空");
        BillPurchaseReturnLine purchaseReturnLine = javaList.get(FlywheelConstant.INDEX);

        Assert.notNull(purchaseReturnLine, "采购行不能为空");

        BillPurchase purchase = request.getObject("purchase", BillPurchase.class);
        Assert.notNull(purchase, "采购不能为空");

        Map<Integer, StockExt> stockMap = stockService.selectByStockIdList(Arrays.asList(purchaseReturnLine.getStockId())).stream().collect(Collectors.toMap(StockExt::getStockId, Function.identity()));

        Customer customer = customerService.getById(purchaseReturn.getCustomerId());
        Assert.notNull(customer, "客户不能为空");

        AccountReceiptConfirmAddRequest accountReceiptConfirmAddRequest = new AccountReceiptConfirmAddRequest();

        accountReceiptConfirmAddRequest.setCustomerId(customer.getId());
        accountReceiptConfirmAddRequest.setCustomerName(customer.getCustomerName());

        CustomerContacts customerContacts = customerContactsService.searchByCustomerId(customer.getId()).get(FlywheelConstant.INDEX);

        accountReceiptConfirmAddRequest.setContactId(customerContacts.getId());
        accountReceiptConfirmAddRequest.setContactName(customerContacts.getName());
        accountReceiptConfirmAddRequest.setContactAddress(customerContacts.getAddress());
        accountReceiptConfirmAddRequest.setContactPhone(customerContacts.getPhone());
        accountReceiptConfirmAddRequest.setShopId(UserContext.getUser().getStore().getId());

        accountReceiptConfirmAddRequest.setMiniAppSource(Boolean.FALSE);
        accountReceiptConfirmAddRequest.setOriginSerialNo(purchaseReturn.getSerialNo());
        accountReceiptConfirmAddRequest.setWaitAuditPrice(purchaseReturn.getReturnPrice());
        accountReceiptConfirmAddRequest.setWaitAuditPrice(purchaseReturnLine.getPurchaseReturnPrice());
        accountReceiptConfirmAddRequest.setReceivableAmount(purchaseReturn.getReturnPrice());
        accountReceiptConfirmAddRequest.setReceivableAmount(purchaseReturnLine.getPurchaseReturnPrice());
        accountReceiptConfirmAddRequest.setOriginType(OriginTypeEnum.CG_TH.getValue());
        accountReceiptConfirmAddRequest.setCollectionType(CollectionTypeEnum.CG_TK.getValue());
        accountReceiptConfirmAddRequest.setStatus(AccountReceiptConfirmStatusEnum.WAIT.getValue());
        accountReceiptConfirmAddRequest.setCollectionNature(CollectionNatureEnum.PURCHASE_RETURN.getValue());
        accountReceiptConfirmAddRequest.setSalesMethod(FinancialSalesMethodEnum.REFUND.getValue());

        switch (purchase.getPurchaseSource()) {
            case TH_CG_DJ:
            case TH_CG_BH:
            case TH_CG_PL:
                accountReceiptConfirmAddRequest.setClassification(FinancialClassificationEnum.TH_CG.getValue());
                accountReceiptConfirmAddRequest.setSalesMethod(FinancialSalesMethodEnum.REFUND.getValue());
                break;
            case GR_HS_JHS:
            case GR_HS_ZH:
                accountReceiptConfirmAddRequest.setClassification(FinancialClassificationEnum.GR_HS.getValue());
                accountReceiptConfirmAddRequest.setSalesMethod(FinancialSalesMethodEnum.REFUND.getValue());
                break;
            case GR_JS:
                accountReceiptConfirmAddRequest.setClassification(FinancialClassificationEnum.GR_JS.getValue());
                accountReceiptConfirmAddRequest.setSalesMethod(FinancialSalesMethodEnum.REFUND.getValue());
                break;
            case TH_JS:
                accountReceiptConfirmAddRequest.setClassification(FinancialClassificationEnum.TH_JS.getValue());
                accountReceiptConfirmAddRequest.setSalesMethod(FinancialSalesMethodEnum.REFUND.getValue());
                break;
        }


        Integer accountReceiptConfirmId = accountReceiptConfirmService.accountReceiptConfirmAdd(accountReceiptConfirmAddRequest).getId();


        ArrayList<@Nullable AccountsPayableAccounting> list = Lists.newArrayList();


        AccountStockRelation accountStockRelation = new AccountStockRelation();

        accountStockRelation.setArcId(accountReceiptConfirmId);
        accountStockRelation.setOriginSerialNo(purchaseReturn.getSerialNo());
        accountStockRelation.setStockId(purchaseReturnLine.getStockId());
        accountStockRelation.setOriginPrice(purchaseReturnLine.getPurchaseReturnPrice());

        accountStockRelationService.AccountStockRelationAdd(accountStockRelation);

        //产生应收应付
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
            case TH_CG_BH:
                salesMethod = FinancialSalesMethodEnum.PURCHASE_PREPARE;
                financialClassificationEnum = FinancialClassificationEnum.TH_CG;
                break;
            case TH_JS:
                salesMethod = FinancialSalesMethodEnum.PURCHASE_C;
                financialClassificationEnum = FinancialClassificationEnum.TH_CG;
                break;
            case TH_CG_DJ:
                salesMethod = FinancialSalesMethodEnum.PURCHASE_DEPOSIT;
                financialClassificationEnum = FinancialClassificationEnum.TH_CG;
                break;
            case GR_HS_ZH:
                salesMethod = FinancialSalesMethodEnum.PURCHASE_DISPLACE;
                financialClassificationEnum = FinancialClassificationEnum.GR_HS;
                break;
            case GR_HS_JHS:
                salesMethod = FinancialSalesMethodEnum.PURCHASE_RECYCLE;
                financialClassificationEnum = FinancialClassificationEnum.GR_HS;
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
        payableAccounting.setOriginSerialNo(purchaseReturn.getSerialNo());
        payableAccounting.setShopId(purchase.getStoreId());
        payableAccounting.setBelongId(purchase.getPurchaseSubjectId());
        payableAccounting.setStatus(FinancialStatusEnum.PENDING_REVIEW);
        payableAccounting.setCustomerId(purchase.getCustomerId());
        payableAccounting.setCustomerContactId(purchase.getCustomerContactId());
        payableAccounting.setCustomerType(Objects.nonNull(customer) ? customer.getType().getValue() : null);
        payableAccounting.setApplicant(purchaseReturn.getCreatedBy());
        payableAccounting.setWhetherUse(WhetherEnum.NO.getValue());
        payableAccounting.setOriginType(OriginTypeEnum.CG_TH);
        payableAccounting.setArcSerialNo(accountReceiptConfirmService.getById(accountReceiptConfirmId).getSerialNo());

        StockExt ext = stockMap.get(purchaseReturnLine.getStockId());

        //行数据
        payableAccounting.setTotalPrice(purchaseReturnLine.getPurchasePrice().negate());
        payableAccounting.setStockId(purchaseReturnLine.getStockId());
        payableAccounting.setStockSn(ext.getStockSn());
        payableAccounting.setWaitAuditPrice(purchaseReturnLine.getPurchasePrice().negate());
        payableAccounting.setBrandName(ext.getBrandName());
        payableAccounting.setSeriesName(ext.getSeriesName());
        payableAccounting.setModel(ext.getModel());

        list.add(payableAccounting);

        accountsPayableAccountingMapper.insertBatchSomeColumn(list);
    }

    @Override
    public void listenerVerification(JSONObject request) {

    }
}
