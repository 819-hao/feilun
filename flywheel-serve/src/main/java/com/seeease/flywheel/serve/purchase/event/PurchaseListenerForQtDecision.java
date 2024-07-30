package com.seeease.flywheel.serve.purchase.event;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.request.AccountReceiptConfirmAddRequest;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.PurchaseLineNotice;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.entity.CustomerPO;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.entity.AccountStockRelation;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.entity.FinancialGenerateDto;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.service.AccountReceiptConfirmService;
import com.seeease.flywheel.serve.financial.service.AccountStockRelationService;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.financial.service.FinancialDocumentsService;
import com.seeease.flywheel.serve.financial.template.payment.PaymentGTemplate;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.flywheel.serve.qt.event.QtDecisionEvent;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 质检判定结果
 * @Date create in 2023/3/13 14:19
 */
@Slf4j
@Component
public class PurchaseListenerForQtDecision implements BillHandlerEventListener<QtDecisionEvent> {

    private static List<BusinessBillTypeEnum> PURCHASE_TYPE = Lists.newArrayList(BusinessBillTypeEnum.TH_CG_DJ,
            BusinessBillTypeEnum.TH_CG_BH,
            BusinessBillTypeEnum.TH_CG_PL, BusinessBillTypeEnum.TH_CG_QK,
            BusinessBillTypeEnum.TH_CG_DJTP, BusinessBillTypeEnum.TH_JS,
            BusinessBillTypeEnum.GR_JS, BusinessBillTypeEnum.GR_HS_JHS,
            BusinessBillTypeEnum.GR_HS_ZH, BusinessBillTypeEnum.GR_HG_ZH,
            BusinessBillTypeEnum.GR_HG_JHS

    );

    @Resource
    private BillPurchaseLineService billPurchaseLineService;

    @Resource
    private StockService stockService;

    @Resource
    private AccountsPayableAccountingService accountingService;

    @Resource
    private BillPurchaseService billPurchaseService;

    @Resource
    private FinancialDocumentsService financialDocumentsService;

    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerContactsService customerContactsService;
    @Resource
    private AccountReceiptConfirmService accountReceiptConfirmService;
    @Resource
    private AccountStockRelationService accountStockRelationService;

    @Override
    public void onApplicationEvent(QtDecisionEvent event) {
        log.info("onApplicationEvent function of PurchaseListenerForQtDecision start and event = {}", JSON.toJSONString(event));

        BillPurchase billPurchase = billPurchaseService.getOne(Wrappers.<BillPurchase>lambdaQuery().eq(BillPurchase::getSerialNo, event.getOriginSerialNo()));

        if (ObjectUtils.isEmpty(billPurchase) || !PURCHASE_TYPE.contains(billPurchase.getPurchaseSource())) {
            return;
        }

        QualityTestingStateEnum qtState = event.getQtState();

        switch (qtState) {
            case NORMAL:
            case ANOMALY:
                billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().stockId(event.getStockId()).lineState(PurchaseLineStateEnum.IN_QUALITY_INSPECTION).serialNo(event.getOriginSerialNo()).build());

                break;
            case FIX:
                billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().stockId(event.getStockId()).lineState(PurchaseLineStateEnum.IN_FIX_INSPECTION).planFixPrice(event.getFixMoney()).serialNo(event.getOriginSerialNo()).build());
                break;
            case RETURN:
                billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().stockId(event.getStockId()).lineState(PurchaseLineStateEnum.IN_RETURN).serialNo(event.getOriginSerialNo()).build());
                stockService.updateStockStatus(Arrays.asList(event.getStockId()), StockStatusEnum.TransitionEnum.PURCHASE_IN_TRANSIT_PURCHASE_RETURNED_ING);
                FinancialGenerateDto dto = new FinancialGenerateDto();
                dto.setId(billPurchase.getId());
                dto.setStockList(Lists.newArrayList(event.getStockId()));
                dto.setType(billPurchase.getPurchaseSource().getValue());
                financialDocumentsService.generatePurchaseQtReturn(dto);

                //查询预付金额
                List<AccountsPayableAccounting> list = accountingService
                        .selectListByOriginSerialNoAndStatusAndType(billPurchase.getSerialNo(),
                                Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW), Lists.newArrayList(ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT));
                //返修和换货
                if (ObjectUtils.isNotEmpty(event.getReturnOrChange()) && event.getReturnOrChange()) {
                    accountingService.batchAudit(list.stream().filter(a -> event.getStockId().equals(a.getStockId())).map(AccountsPayableAccounting::getId).collect(Collectors.toList()), FlywheelConstant.RETURN_EXCHANGE_AUDIT, UserContext.getUser().getUserName());
//                    accountingService.createApa(billPurchase.getSerialNo(), ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT,
//                            FinancialStatusEnum.RETURN_PENDING_REVIEW, Lists.newArrayList(event.getStockId()), null, true);
                    BillPurchaseLine billPurchaseLine = billPurchaseLineService.billPurchaseLineQuery(billPurchase.getId(), event.getStockId());
                    paymentGTemplate.generatePayable(new JSONObject()
                            .fluentPut("customerId", billPurchase.getCustomerId())
                            .fluentPut("customerContactId", billPurchase.getCustomerContactId())
                            .fluentPut("purchase", billPurchase)
                            .fluentPut("purchaseLine", billPurchaseLine)
                            .fluentPut("arc", null)
                    );
                } else if (ObjectUtils.isNotEmpty(event.getReturnOrChange()) && !event.getReturnOrChange()) {
                    accountingService.batchAudit(list.stream().filter(a -> event.getStockId().equals(a.getStockId())).map(AccountsPayableAccounting::getId).collect(Collectors.toList()), FlywheelConstant.EXCHANGE_AUDIT, UserContext.getUser().getUserName());
//                    accountingService.createApa(billPurchase.getSerialNo(), ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT,
//                            FinancialStatusEnum.RETURN_PENDING_REVIEW, Lists.newArrayList(event.getStockId()), null, true);
                    BillPurchaseLine billPurchaseLine = billPurchaseLineService.billPurchaseLineQuery(billPurchase.getId(), event.getStockId());
                    paymentGTemplate.generatePayable(new JSONObject()
                            .fluentPut("customerId", billPurchase.getCustomerId())
                            .fluentPut("customerContactId", billPurchase.getCustomerContactId())
                            .fluentPut("purchase", billPurchase)
                            .fluentPut("purchaseLine", billPurchaseLine)
                            .fluentPut("arc", null)
                    );
                } else {

                    switch (event.getBusinessBillTypeEnum()) {
                        case TH_CG_DJ:
                        case TH_CG_BH:
                        case GR_HS_JHS:
                            // 修改掉预付 && 上传快递单号产生的预付。。
                            accountingService.batchAudit(list.stream().filter(a -> event.getStockId().equals(a.getStockId())).map(AccountsPayableAccounting::getId).collect(Collectors.toList()), FlywheelConstant.REJECTION_AUDIT, UserContext.getUser().getUserName());
                            break;
                        case GR_HS_ZH:
                            List<AccountsPayableAccounting> list2 = accountingService.selectListByOriginSerialNoAndStatusAndType(billPurchase.getSerialNo(), Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW), Lists.newArrayList(ReceiptPaymentTypeEnum.AMOUNT_PAYABLE));
                            accountingService.batchAudit(list2.stream().filter(a -> event.getStockId().equals(a.getStockId())).map(AccountsPayableAccounting::getId).collect(Collectors.toList()), FlywheelConstant.REJECTION_AUDIT, UserContext.getUser().getUserName());
                            break;
                        //没有操作
//                        case TH_JS:
//                        case TH_CG_PL:
//                        case GR_HS_ZH:
//                            //处理掉应付
//                            List<AccountsPayableAccounting> list2 = accountingService.selectListByOriginSerialNoAndStatusAndType(billPurchase.getSerialNo(), Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW), Lists.newArrayList(ReceiptPaymentTypeEnum.AMOUNT_PAYABLE));
//                            accountingService.batchAudit(list2.stream().filter(a -> event.getStockId().equals(a.getStockId())).map(AccountsPayableAccounting::getId).collect(Collectors.toList()), FlywheelConstant.REJECTION_AUDIT, UserContext.getUser().getUserName());
//                            break;
                        default:
                    }

                }

//                if (event.getReturnOrChange() == null) {
//                    accountingService.createApa(billPurchase.getSerialNo(), ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT,
//                            FinancialStatusEnum.RETURN_PENDING_REVIEW, Lists.newArrayList(event.getStockId()), null, true);
//                }

                //定金/备货，质检不通过，生成待核销确认收款单
                if (event.getReturnOrChange() == null && (
                        billPurchase.getPurchaseSource().equals(BusinessBillTypeEnum.TH_CG_DJ)
                                || billPurchase.getPurchaseSource().equals(BusinessBillTypeEnum.TH_CG_BH)
                                || billPurchase.getPurchaseSource().equals(BusinessBillTypeEnum.TH_CG_QK)
                                || billPurchase.getPurchaseSource().equals(BusinessBillTypeEnum.GR_HS_JHS)
                                || billPurchase.getPurchaseSource().equals(BusinessBillTypeEnum.TH_CG_DJTP)
                                || billPurchase.getPurchaseSource().equals(BusinessBillTypeEnum.GR_HS_ZH)
//                                || billPurchase.getPurchaseSource().equals(BusinessBillTypeEnum.TH_JS)
//                                || billPurchase.getPurchaseSource().equals(BusinessBillTypeEnum.TH_CG_PL)
                )) {
                    BillPurchaseLine billPurchaseLine = billPurchaseLineService.billPurchaseLineQuery(billPurchase.getId(), event.getStockId());
                    log.info("TH_CG_DJ or TH_CG_BH ,accountReceiptConfirmAdd of PurchaseListenerForQtDecision function billPurchase = {},billPurchaseLine ={}", JSON.toJSONString(billPurchase), JSON.toJSONString(billPurchaseLine));
                    if (null != billPurchaseLine
//                            &&
//                            (PurchaseLineStateEnum.RETURNED.equals(billPurchaseLine.getPurchaseLineState()) ||
//                                    PurchaseLineStateEnum.IN_RETURN.equals(billPurchaseLine.getPurchaseLineState()))

                    ) {
                        Integer confirmAdd = accountReceiptConfirmAdd(billPurchase.getCustomerId(), billPurchase.getCustomerContactId(), billPurchase, billPurchaseLine);
                        paymentGTemplate.generatePayable(new JSONObject()
                                .fluentPut("customerId", billPurchase.getCustomerId())
                                .fluentPut("customerContactId", billPurchase.getCustomerContactId())
                                .fluentPut("purchase", billPurchase)
                                .fluentPut("purchaseLine", billPurchaseLine)
                                .fluentPut("arc", confirmAdd)
                        );
                    }
                }

                break;
            case CONFIRM_FIX:

                billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().stockId(event.getStockId()).lineState(PurchaseLineStateEnum.TO_CUSTOMER_CONFIRMATION).planFixPrice(event.getFixMoney()).computeBuyBackPrice(WhetherEnum.YES)
//                        .isSettlement(WhetherEnum.YES)
                        .serialNo(event.getOriginSerialNo()).build());

                break;
        }
    }

    Integer accountReceiptConfirmAdd(Integer customerId, Integer customerContactId, BillPurchase billPurchase, BillPurchaseLine billPurchaseLine) {
        log.info("accountReceiptConfirmAdd function of PurchaseListenerForQtDecision start ");
        List<CustomerContacts> customerContactsList = customerContactsService.searchByCustomerId(customerId);
        customerContactsList = customerContactsList.stream().filter(Objects::nonNull).filter(e -> e.getId().equals(customerContactId)).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(customerContactsList)) {
            log.info("customerContactsList function is empty .");
            return null;
        }

        CustomerPO customerPO = customerService.queryCustomerPO(customerId);

        CustomerContacts customerContacts = customerContactsList.get(0);
        AccountReceiptConfirmAddRequest request = new AccountReceiptConfirmAddRequest();
        request.setCustomerId(customerId);
        request.setCustomerName(null != customerPO && StringUtils.isNotEmpty(customerPO.getCustomerName()) ? customerPO.getCustomerName() : FlywheelConstant.CUSTOMER_CONTACTNAME_VALUE);
        request.setContactId(customerContactId);
        request.setContactName(customerContacts.getName());
        request.setContactAddress(customerContacts.getAddress());
        request.setContactPhone(customerContacts.getPhone());
        request.setShopId(billPurchase.getStoreId());

        request.setMiniAppSource(Boolean.FALSE);
        request.setOriginSerialNo(billPurchase.getSerialNo());
        request.setWaitAuditPrice(billPurchaseLine.getPurchasePrice());
        request.setReceivableAmount(billPurchaseLine.getPurchasePrice());
        request.setOriginType(OriginTypeEnum.CG_TH.getValue());
        request.setCollectionNature(CollectionNatureEnum.PURCHASE_RETURN.getValue());
        //收款类型
        request.setCollectionType(CollectionTypeEnum.CG_TK.getValue());
        request.setStatus(AccountReceiptConfirmStatusEnum.WAIT.getValue());

        switch (billPurchase.getPurchaseSource()) {
            case TH_CG_DJ:
            case TH_CG_BH:
                request.setClassification(FinancialClassificationEnum.TH_CG.getValue());
                request.setSalesMethod(FinancialSalesMethodEnum.REFUND.getValue());
                break;
            case GR_HS_JHS:
            case GR_HS_ZH:
                request.setClassification(FinancialClassificationEnum.GR_HS.getValue());
                request.setSalesMethod(FinancialSalesMethodEnum.REFUND.getValue());
                break;
        }

//        request.setSalesMethod(PurchaseModeEnum.convert(billPurchase.getPurchaseMode()).getValue());

        Integer accountReceiptConfirmId = accountReceiptConfirmService.accountReceiptConfirmAdd(request).getId();

        AccountStockRelation accountStockRelation = new AccountStockRelation();
        accountStockRelation.setArcId(accountReceiptConfirmId);
        accountStockRelation.setOriginSerialNo(billPurchase.getSerialNo());
        accountStockRelation.setStockId(billPurchaseLine.getStockId());
        accountStockRelation.setOriginPrice(billPurchaseLine.getPurchasePrice());

        accountStockRelationService.AccountStockRelationAdd(accountStockRelation);

        return accountReceiptConfirmId;
    }

    @Resource
    private PaymentGTemplate paymentGTemplate;
}
