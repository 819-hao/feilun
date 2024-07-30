package com.seeease.flywheel.serve.financial.event;


import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.enums.ReceiptPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.service.AccountStockRelationService;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.financial.service.ApplyFinancialPaymentService;
import com.seeease.flywheel.serve.financial.template.payment.PaymentDTemplate;
import com.seeease.flywheel.serve.financial.template.payment.PaymentETemplate;
import com.seeease.flywheel.serve.financial.template.payment.PaymentFTemplate;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.springframework.context.UserContext;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 申请打款确认打款
 */
@Component
public class ApplyFinancialPaymentListenerForPass implements BillHandlerEventListener<ApplyFinancialPaymentPassEvent> {

    @Resource
    private AccountsPayableAccountingService accountingService;
    @Resource
    private StockService stockService;
    @Resource
    private BillPurchaseService purchaseService;
    @Resource
    private BillPurchaseLineService purchaseLineService;
    private static List<ApplyFinancialPaymentTypeEnum> TYPE_ENUMS = Lists.newArrayList(
            ApplyFinancialPaymentTypeEnum.PEER_PROCUREMENT,
            ApplyFinancialPaymentTypeEnum.INDEPENDENT_FINANCIAL_SETTLEMENT,
            ApplyFinancialPaymentTypeEnum.PEER_CONSIGNMENT,
            ApplyFinancialPaymentTypeEnum.SEND_PERSON);

    @Override
    public void onApplicationEvent(ApplyFinancialPaymentPassEvent event) {
        if (StringUtils.isNotEmpty(event.getAfpSerialNo()) && TYPE_ENUMS.contains(event.getTypePayment())) {
            List<AccountsPayableAccounting> list = accountingService.selectListByAfpSerialNoAndStatusAndType(event.getAfpSerialNo(),
                    Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW),
                    Lists.newArrayList(ReceiptPaymentTypeEnum.AMOUNT_PAYABLE));
            accountingService.batchAudit(list.stream().map(AccountsPayableAccounting::getId)
                    .collect(Collectors.toList()), FlywheelConstant.PAYMENT_AUDIT, FlywheelConstant.AUTOMATIC_SYSTEM);
            //当所关联的申请打款单已打款后，将商品上原有的【同行采购-寄售】变更为【同行采购-批量】
            if (ApplyFinancialPaymentTypeEnum.PEER_CONSIGNMENT.equals(event.getTypePayment())) {
                List<Stock> stockList = list.stream().map(t -> {
                    Stock stock = new Stock();
                    stock.setId(t.getStockId());
                    stock.setStockSrc(BusinessBillTypeEnum.TH_CG_BH.getValue());
//                    stock.setStockSrc(BusinessBillTypeEnum.TH_CG_PL.getValue());
                    return stock;
                }).collect(Collectors.toList());
                stockService.updateBatchById(stockList);
            }
        } else if (StringUtils.isNotEmpty(event.getAfpSerialNo()) && ApplyFinancialPaymentTypeEnum.WRONG_AIRWAY_BILL.equals(event.getTypePayment())) {
            List<AccountsPayableAccounting> list = accountingService
                    .selectListByOriginSerialNoAndStatusAndType(event.getOriginSerialNo(),
                            Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW, FinancialStatusEnum.RETURN_PENDING_REVIEW)
                            , Lists.newArrayList(ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE));
            accountingService.batchAudit(list.stream()
                            .map(AccountsPayableAccounting::getId)
                            .collect(Collectors.toList()),
                    FlywheelConstant.WRONG_AIRWAY_BILL_AUDIT, UserContext.getUser().getUserName());
        } else if (StringUtils.isNotEmpty(event.getAfpSerialNo()) && ApplyFinancialPaymentTypeEnum.PERSONAL_SALES_RETURNS.equals(event.getTypePayment())) {

            List<AccountsPayableAccounting> list = accountingService
                    .selectListByOriginSerialNoAndStatusAndType(event.getOriginSerialNo(),
                            Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW, FinancialStatusEnum.RETURN_PENDING_REVIEW)
                            , Lists.newArrayList(ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE));
            accountingService.batchAudit(list.stream()
                            .map(AccountsPayableAccounting::getId)
                            .collect(Collectors.toList()),
                    FlywheelConstant.RETURN_AUDIT, UserContext.getUser().getUserName());
        }
        if (StringUtils.isEmpty(event.getOriginSerialNo()))
            return;
        BillPurchase purchase = purchaseService.getOne(new LambdaQueryWrapper<BillPurchase>()
                .eq(BillPurchase::getSerialNo, event.getOriginSerialNo()));
        if (ObjectUtils.isEmpty(purchase))
            return;
        List<BillPurchaseLine> lineList = purchaseLineService.list(new LambdaQueryWrapper<BillPurchaseLine>()
                .eq(BillPurchaseLine::getPurchaseId, purchase.getId()));
        switch (purchase.getPurchaseSource()) {
            case GR_HG_JHS:
            case GR_HG_ZH:
                //个人回购-仅回收 个人回购-置换 申请打款单打完款之后 预付单生成

//                lineList.forEach(line -> {
//                    accountingService.createApa(event.getOriginSerialNo(), ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT,
//                            (PurchaseLineStateEnum.WAREHOUSED.equals(line.getPurchaseLineState()) ||
//                                    PurchaseLineStateEnum.TO_BE_SETTLED.equals(line.getPurchaseLineState()) ||
//                                    PurchaseLineStateEnum.IN_SETTLED.equals(line.getPurchaseLineState()))  ?
//                                    FinancialStatusEnum.AUDITED : FinancialStatusEnum.PENDING_REVIEW,
//                            Lists.newArrayList(line.getStockId()), null, false);
//                });

                if (DateUtil.between(purchase.getCreatedTime(), DateUtil.parse("2024-03-01 00:00:00"), DateUnit.SECOND, false) <= 0) {
                    paymentFTemplate.listenerVerification(new JSONObject().fluentPut("node", 2).fluentPut("originSerialNo", event.getOriginSerialNo()).fluentPut("stockId", accountStockRelationService.selectByAfpIds(Arrays.asList(event.getAfpId())).stream().findFirst().get().getStockId()));
                }

                break;
            case TH_CG_DJ:
            case TH_CG_BH:
            case TH_CG_QK:
            case TH_CG_DJTP:
                //同行采购（订金/备货）申请打款单财务确认打款后 v预付单生成
//                accountingService.createApa(event.getOriginSerialNo(), ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT,
//                        FinancialStatusEnum.PENDING_REVIEW,
//                        lineList.stream()
//                                .map(BillPurchaseLine::getStockId).collect(Collectors.toList()), null);
                break;

            case GR_HS_ZH:
                //个人置换 申请打款单打完款之后 预付单生成
                //同行采购（订金/备货）申请打款单财务确认打款后 v预付单生成
//                lineList.forEach(line -> {
//                    accountingService.createApa(event.getOriginSerialNo(), ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT,
//                            (PurchaseLineStateEnum.WAREHOUSED.equals(line.getPurchaseLineState()) ||
//                                    PurchaseLineStateEnum.TO_BE_SETTLED.equals(line.getPurchaseLineState()) ||
//                                    PurchaseLineStateEnum.IN_SETTLED.equals(line.getPurchaseLineState())) ?
//                                    FinancialStatusEnum.AUDITED : FinancialStatusEnum.PENDING_REVIEW,
//                            Lists.newArrayList(line.getStockId()),
//                            purchase.getSalePrice().subtract(purchase.getTotalPurchasePrice()).abs(), false);
//                });

                if (DateUtil.between(purchase.getCreatedTime(), DateUtil.parse("2024-03-01 00:00:00"), DateUnit.SECOND, false) <= 0) {
                    paymentETemplate.listenerVerification(new JSONObject()
                            .fluentPut("originSerialNo", event.getOriginSerialNo())
                            .fluentPut("node", 2)
                            .fluentPut("stockId", accountStockRelationService.selectByAfpIds(Arrays.asList(event.getAfpId())).stream().findFirst().get().getStockId())
                    );
                }


                break;
            case TH_CG_PL:
                //同行采购（批量）所涉及的申请打款单打款后 自动核销
            case GR_JS:
                //所涉及的申请打款单打款后 应付单 自动核销
            case TH_JS:
                //同行采购（寄售）所涉及的申请打款单打款后 自动核销
//                List<AccountsPayableAccounting> list = accountingService.selectListByAfpSerialNoAndStatusAndType(event.getAfpSerialNo(),
//                        Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW),
//                        Lists.newArrayList(ReceiptPaymentTypeEnum.AMOUNT_PAYABLE));
//                accountingService.batchAudit(list.stream().map(AccountsPayableAccounting::getId)
//                        .collect(Collectors.toList()), FlywheelConstant.IN_STORE_AUDIT, FlywheelConstant.AUTOMATIC_SYSTEM);

                if (purchase.getPurchaseSource() == BusinessBillTypeEnum.GR_JS && DateUtil.between(DateUtil.parse("2024-03-01 00:00:00"), purchase.getCreatedTime(), DateUnit.SECOND, false) >= 0) {
                    paymentDTemplate.listenerVerification(new JSONObject().fluentPut("afp",
                            applyFinancialPaymentService.getById(event.getAfpId())));
                } else if (Arrays.asList(BusinessBillTypeEnum.TH_JS, BusinessBillTypeEnum.TH_CG_PL).contains(purchase.getPurchaseSource())) {
                    paymentDTemplate.listenerVerification(new JSONObject().fluentPut("afp",
                            applyFinancialPaymentService.getById(event.getAfpId())));
                }
                break;
        }

    }

    @Resource
    private PaymentDTemplate paymentDTemplate;
    @Resource
    private PaymentETemplate paymentETemplate;

    @Resource
    private PaymentFTemplate paymentFTemplate;

    @Resource
    private ApplyFinancialPaymentService applyFinancialPaymentService;

    @Resource
    private AccountStockRelationService accountStockRelationService;
}


