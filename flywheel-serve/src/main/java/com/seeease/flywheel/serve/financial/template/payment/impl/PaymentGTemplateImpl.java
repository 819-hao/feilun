package com.seeease.flywheel.serve.financial.template.payment.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.entity.AccountReceiptConfirm;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.mapper.AccountReceiptConfirmMapper;
import com.seeease.flywheel.serve.financial.mapper.AccountsPayableAccountingMapper;
import com.seeease.flywheel.serve.financial.template.payment.PaymentGTemplate;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
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
public class PaymentGTemplateImpl implements PaymentGTemplate {

    @Resource
    private CustomerService customerService;

    @Resource
    private AccountsPayableAccountingMapper accountsPayableAccountingMapper;

    @Resource
    private StockService stockService;

    @Resource
    private AccountReceiptConfirmMapper accountReceiptConfirmMapper;

    @Override
    public void generatePayable(JSONObject request) {

        Integer customerId = request.getInteger("customerId");
        Assert.notNull(customerId, "客户不能为空");

        Integer customerContactId = request.getInteger("customerContactId");
        Assert.notNull(customerContactId, "客户联系人不能为空");

        BillPurchase purchase = request.getObject("purchase", BillPurchase.class);
        Assert.notNull(purchase, "采购不能为空");

        BillPurchaseLine purchaseLine = request.getObject("purchaseLine", BillPurchaseLine.class);
        Assert.notNull(purchaseLine, "采购行不能为空");

        List<BillPurchaseLine> purchaseLineList = Arrays.asList(purchaseLine);

        Integer arc = request.getInteger("arc");

        Map<Integer, StockExt> stockMap = Optional.ofNullable(purchaseLineList.stream()
                .map(BillPurchaseLine::getStockId).filter(Objects::nonNull).collect(Collectors.toList())).filter(CollectionUtils::isNotEmpty).map(ids -> stockService.selectByStockIdList(ids).stream().collect(Collectors.toMap(StockExt::getStockId, Function.identity()))).orElse(Collections.EMPTY_MAP);

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
                case TH_CG_BH:
                    salesMethod = FinancialSalesMethodEnum.PURCHASE_PREPARE;
                    financialClassificationEnum = FinancialClassificationEnum.TH_CG;
                    break;
                case TH_CG_DJ:
                    salesMethod = FinancialSalesMethodEnum.PURCHASE_DEPOSIT;
                    financialClassificationEnum = FinancialClassificationEnum.TH_CG;
                    break;
                case GR_JS:
                    salesMethod = FinancialSalesMethodEnum.PURCHASE_C;
                    financialClassificationEnum = FinancialClassificationEnum.GR_HS;
                    break;
                case GR_HS_JHS:
                    salesMethod = FinancialSalesMethodEnum.PURCHASE_RECYCLE;
                    financialClassificationEnum = FinancialClassificationEnum.GR_HS;
                    break;
                case GR_HS_ZH:
                    salesMethod = FinancialSalesMethodEnum.PURCHASE_DISPLACE;
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
            payableAccounting.setOriginType(OriginTypeEnum.CG_TH);
            AccountReceiptConfirm confirm = accountReceiptConfirmMapper.selectById(arc);
            payableAccounting.setArcSerialNo(Objects.nonNull(confirm) ? confirm.getSerialNo() : "-");

            StockExt ext = stockMap.get(billPurchaseLine.getStockId());

            //行数据
            payableAccounting.setTotalPrice(billPurchaseLine.getPurchasePrice().negate());
            payableAccounting.setStockId(billPurchaseLine.getStockId());
            payableAccounting.setStockSn(billPurchaseLine.getStockSn());
            payableAccounting.setWaitAuditPrice(billPurchaseLine.getPurchasePrice().negate());
            payableAccounting.setBrandName(ext.getBrandName());
            payableAccounting.setSeriesName(ext.getSeriesName());
            payableAccounting.setModel(ext.getModel());

            list.add(payableAccounting);
        }

        accountsPayableAccountingMapper.insertBatchSomeColumn(list);

    }

    //待确认 已经写了 思考一下拿回来
    @Override
    public void createReceipt(JSONObject request) {

    }

    //待确认 没有监听
    @Override
    public void listenerVerification(JSONObject request) {

    }
}
