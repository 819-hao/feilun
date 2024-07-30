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
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPayment;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.mapper.ApplyFinancialPaymentMapper;
import com.seeease.flywheel.serve.financial.service.AccountReceiptConfirmService;
import com.seeease.flywheel.serve.financial.service.AccountStockRelationService;
import com.seeease.flywheel.serve.financial.template.payment.PaymentCTemplate;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.LogisticsDeliveryEvent;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 发货 目前只针对总部
 *
 * @author Tiro
 * @date 2023/3/9
 */
@Slf4j
@Component
public class PurchaseListenerForLogisticsDelivery implements BillHandlerEventListener<LogisticsDeliveryEvent> {

    private static List<BusinessBillTypeEnum> PURCHASE_TYPE = Lists.newArrayList(BusinessBillTypeEnum.TO_C_XS, BusinessBillTypeEnum.TO_B_XS, BusinessBillTypeEnum.TO_B_JS,

            BusinessBillTypeEnum.TH_CG_DJ, BusinessBillTypeEnum.TH_CG_BH, BusinessBillTypeEnum.TH_CG_PL, BusinessBillTypeEnum.TH_CG_QK, BusinessBillTypeEnum.TH_CG_DJTP, BusinessBillTypeEnum.TH_JS, BusinessBillTypeEnum.GR_JS, BusinessBillTypeEnum.GR_HS_JHS, BusinessBillTypeEnum.GR_HS_ZH, BusinessBillTypeEnum.GR_HG_ZH, BusinessBillTypeEnum.GR_HG_JHS);

    @Resource
    private BillPurchaseLineService billPurchaseLineService;

    @Resource
    private StockService stockService;
    @Resource
    private BillPurchaseService billPurchaseService;
    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerContactsService customerContactsService;
    @Resource
    private AccountReceiptConfirmService accountReceiptConfirmService;
    @Resource
    private AccountStockRelationService accountStockRelationService;

    @Resource
    private ApplyFinancialPaymentMapper applyFinancialPaymentMapper;

    @Override
    public void onApplicationEvent(LogisticsDeliveryEvent event) {

        List<BillStoreWorkPre> workPreList = event.getWorkPreList();

        if (CollectionUtils.isEmpty(workPreList)) {
            return;
        }

        log.info("workPreList of PurchaseListenerForLogisticsDelivery= {}", JSON.toJSONString(workPreList));

        workPreList.forEach(t -> {

            if (!PURCHASE_TYPE.contains(t.getWorkSource())) {
                return;
            }

            switch (t.getWorkSource()) {
                case TH_CG_DJ:
                case TH_CG_BH:
                case TH_CG_QK:
                case TH_CG_DJTP:
                case TH_CG_PL:
                case TH_JS:
                case GR_HS_JHS:
                case GR_HS_ZH:
                case GR_HG_JHS:
                case GR_HG_ZH:

                    billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().stockId(t.getStockId()).lineState(PurchaseLineStateEnum.RETURNED).serialNo(t.getOriginSerialNo()).build());

                    //直接退货完成
                    stockService.updateStockStatus(Arrays.asList(t.getStockId()), StockStatusEnum.TransitionEnum.PURCHASE_RETURNED_ING_PURCHASE_RETURNED);
                    //定金/备货，质检不通过，生成待核销确认收款单
//                    if (t.getWorkSource().equals(BusinessBillTypeEnum.TH_CG_DJ) ||
//                            t.getWorkSource().equals(BusinessBillTypeEnum.TH_CG_BH)) {
//                        BillPurchase billPurchase = billPurchaseService.billPurchaseQuery(t.getOriginSerialNo());
//                        BillPurchaseLine billPurchaseLine = billPurchaseLineService.billPurchaseLineQuery(billPurchase.getId(), t.getStockId());
//                        log.info("TH_CG_DJ or TH_CG_BH ,accountReceiptConfirmAdd of PurchaseListenerForLogisticsDelivery function billPurchase = {},billPurchaseLine ={}",JSON.toJSONString(billPurchase),JSON.toJSONString(billPurchaseLine));
//                        if (null != billPurchaseLine && PurchaseLineStateEnum.RETURNED.equals(billPurchaseLine.getPurchaseLineState())) {
//                            accountReceiptConfirmAdd(t, billPurchase, billPurchaseLine);
//                        }
//                    }

                    BillPurchase billPurchase = billPurchaseService.getOne(Wrappers.<BillPurchase>lambdaQuery().eq(BillPurchase::getSerialNo, t.getOriginSerialNo()));

                    if (ObjectUtils.isEmpty(billPurchase) || !PURCHASE_TYPE.contains(billPurchase.getPurchaseSource())) {
                        return;
                    }
                    BillPurchaseLine billPurchaseLine2 = billPurchaseLineService.getOne(Wrappers.<BillPurchaseLine>lambdaQuery()
                                    .eq(BillPurchaseLine::getStockId, t.getStockId())
                                    .eq(BillPurchaseLine::getPurchaseId, billPurchase.getId())
//                            .eq(BillPurchaseLine::getPurchaseLineState, PurchaseLineStateEnum.ON_CONSIGNMENT)
                    );
                    if (ObjectUtils.isNotEmpty(billPurchaseLine2) && billPurchaseLine2.getPurchaseLineState() == PurchaseLineStateEnum.ON_CONSIGNMENT) {
                        return;
                    }

                    if (t.getWorkSource() == BusinessBillTypeEnum.TH_JS
                            && stockService.getById(t.getStockId()).getStockSrc().equals(BusinessBillTypeEnum.TH_JS.getValue())) {
                        //核销掉应付单 //应收应付核销掉 todo 同行寄售特殊情况

//                        paymentLTemplate.listenerVerification(new JSONObject()
//                                .fluentPut("stockId", t.getStockId())
//                                .fluentPut("originSerialNo", t.getOriginSerialNo()
//                                )
//                        );
                        return;
                    }

                    //生成待核销确认收款单  质检标记退回
//                    if (t.getWorkSource()==  BusinessBillTypeEnum.GR_HS_ZH){
//                        accountReceiptConfirmAdd(billPurchase.getCustomerId(), billPurchase.getCustomerContactId(), billPurchase, billPurchaseLine2);
//                    }

                    if ((t.getWorkSource() == BusinessBillTypeEnum.TH_CG_DJ || t.getWorkSource() == BusinessBillTypeEnum.TH_CG_BH) && Objects.nonNull(t.getReturnType())) {
                        paymentCTemplate.listenerVerification(new JSONObject().fluentPut("stockId", t.getStockId()).fluentPut("originSerialNo", t.getOriginSerialNo()).fluentPut("returnType", t.getReturnType().getValue()));
                    }

                    if (Arrays.asList(BusinessBillTypeEnum.GR_HG_JHS, BusinessBillTypeEnum.GR_HG_ZH).contains(t.getWorkSource())) {

                        List<ApplyFinancialPayment> list = applyFinancialPaymentMapper.selectList(Wrappers.<ApplyFinancialPayment>lambdaQuery()
                                .eq(ApplyFinancialPayment::getOriginSerialNo, t.getOriginSerialNo()));

                        if (CollectionUtils.isNotEmpty(list)) {
                            ApplyFinancialPayment applyFinancialPayment = list.stream().findFirst().get();
                            if (applyFinancialPayment.getState() == ApplyFinancialPaymentStateEnum.PAID) {
                                //作废申请打款
                                ApplyFinancialPayment payment = new ApplyFinancialPayment();
                                payment.setId(applyFinancialPayment.getId());
                                payment.setTransitionStateEnum(ApplyFinancialPaymentStateEnum.TransitionEnum.PAID_TO_OBSOLETE);
                                UpdateByIdCheckState.update(applyFinancialPaymentMapper, payment);
                            } else if (applyFinancialPayment.getState() == ApplyFinancialPaymentStateEnum.PENDING_REVIEW) {
                                //作废申请打款
                                ApplyFinancialPayment payment = new ApplyFinancialPayment();
                                payment.setId(applyFinancialPayment.getId());
                                payment.setTransitionStateEnum(ApplyFinancialPaymentStateEnum.TransitionEnum.PENDING_REVIEW_TO_CANCEL);
                                UpdateByIdCheckState.update(applyFinancialPaymentMapper, payment);
                            }
                        }
                    }

                    break;
                case GR_JS:
                    billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().stockId(t.getStockId()).lineState(PurchaseLineStateEnum.IN_RETURN).serialNo(t.getOriginSerialNo()).build());
                    //应收应付核销掉
//                    paymentLTemplate.listenerVerification(new JSONObject()
//                            .fluentPut("stockId", t.getStockId())
//                            .fluentPut("originSerialNo", t.getOriginSerialNo())
//                    );
                    break;
                case TO_C_XS:
                case TO_B_XS:
                case TO_B_JS:

                    //只有销售发货 会通知采购这边 寄售中才能
                    BillPurchaseLine billPurchaseLine = billPurchaseLineService.getOne(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getStockId, t.getStockId()).eq(BillPurchaseLine::getPurchaseLineState, PurchaseLineStateEnum.ON_CONSIGNMENT));
                    if (ObjectUtils.isEmpty(billPurchaseLine)) {
                        return;
                    }
                    billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().stockId(t.getStockId()).lineState(PurchaseLineStateEnum.TO_BE_SETTLED).purchaseId(billPurchaseLine.getPurchaseId()).isSettlement(WhetherEnum.YES).build());
                    break;
            }
        });
    }

    @Resource
    private PaymentCTemplate paymentCTemplate;

//    void accountReceiptConfirmAdd(BillStoreWorkPre billStoreWorkPre, BillPurchase billPurchase, BillPurchaseLine billPurchaseLine) {
//        log.info("accountReceiptConfirmAdd function of PurchaseListenerForLogisticsDelivery start and billStoreWorkPre = {}", JSON.toJSONString(billStoreWorkPre));
//        List<CustomerContacts> customerContactsList = customerContactsService.searchByCustomerId(billStoreWorkPre.getCustomerId());
//        customerContactsList = customerContactsList.stream().filter(Objects::nonNull)
//                .filter(e -> e.getId().equals(billStoreWorkPre.getCustomerContactId())).collect(Collectors.toList());
//        if (CollectionUtil.isEmpty(customerContactsList)) {
//            log.info("customerContactsList function is empty .");
//            return;
//        }
//
//        CustomerPO customerPO = customerService.queryCustomerPO(billStoreWorkPre.getCustomerId());
//
//        CustomerContacts customerContacts = customerContactsList.get(0);
//        AccountReceiptConfirmAddRequest request = new AccountReceiptConfirmAddRequest();
//        request.setCustomerId(billStoreWorkPre.getCustomerId());
//        request.setCustomerName(null != customerPO && StringUtils.isNotEmpty(customerPO.getCustomerName()) ? customerPO.getCustomerName() : FlywheelConstant.CUSTOMER_CONTACTNAME_VALUE);
//        request.setContactId(billStoreWorkPre.getCustomerContactId());
//        request.setContactName(customerContacts.getName());
//        request.setContactAddress(customerContacts.getAddress());
//        request.setContactPhone(customerContacts.getPhone());
//        request.setShopId(billPurchase.getStoreId());
//
//        request.setMiniAppSource(Boolean.FALSE);
//        request.setOriginSerialNo(billPurchase.getSerialNo());
//        request.setWaitAuditPrice(billPurchaseLine.getPurchasePrice());
//        request.setReceivableAmount(billPurchaseLine.getPurchasePrice());
//        request.setOriginType(OriginTypeEnum.CG_TH.getValue());
//        request.setClassification(FinancialClassificationEnum.CG_TH.getValue());
//        //request.setCollectionType();
//        request.setStatus(AccountReceiptConfirmStatusEnum.WAIT.getValue());
//        request.setSalesMethod(PurchaseModeEnum.convert(billPurchase.getPurchaseMode()).getValue());
//        request.setCollectionNature(CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue());
//
//        Integer accountReceiptConfirmId = accountReceiptConfirmService.accountReceiptConfirmAdd(request).getId();
//
//        AccountStockRelation accountStockRelation = new AccountStockRelation();
//        accountStockRelation.setArcId(accountReceiptConfirmId);
//        accountStockRelation.setOriginSerialNo(billPurchase.getSerialNo());
//        accountStockRelation.setStockId(billPurchaseLine.getStockId());
//        accountStockRelation.setOriginPrice(billPurchaseLine.getPurchasePrice());
//
//        accountStockRelationService.AccountStockRelationAdd(accountStockRelation);
//
//    }

    void accountReceiptConfirmAdd(Integer customerId, Integer customerContactId, BillPurchase billPurchase, BillPurchaseLine billPurchaseLine) {
        log.info("accountReceiptConfirmAdd function of PurchaseListenerForQtDecision start ");
        List<CustomerContacts> customerContactsList = customerContactsService.searchByCustomerId(customerId);
        customerContactsList = customerContactsList.stream().filter(Objects::nonNull).filter(e -> e.getId().equals(customerContactId)).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(customerContactsList)) {
            log.info("customerContactsList function is empty .");
            return;
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
        request.setClassification(FinancialClassificationEnum.TH_CG.getValue());
        request.setCollectionType(CollectionTypeEnum.CG_TK.getValue());
        request.setStatus(AccountReceiptConfirmStatusEnum.WAIT.getValue());
        request.setSalesMethod(PurchaseModeEnum.convert(billPurchase.getPurchaseMode()).getValue());
        //request.setCollectionNature(CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue());

        Integer accountReceiptConfirmId = accountReceiptConfirmService.accountReceiptConfirmAdd(request).getId();

        AccountStockRelation accountStockRelation = new AccountStockRelation();
        accountStockRelation.setArcId(accountReceiptConfirmId);
        accountStockRelation.setOriginSerialNo(billPurchase.getSerialNo());
        accountStockRelation.setStockId(billPurchaseLine.getStockId());
        accountStockRelation.setOriginPrice(billPurchaseLine.getPurchasePrice());

        accountStockRelationService.AccountStockRelationAdd(accountStockRelation);
    }
}
