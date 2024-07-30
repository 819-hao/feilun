package com.seeease.flywheel.serve.purchase.event;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.request.AccountReceiptConfirmAddRequest;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.PurchaseReturnLineNotice;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.entity.CustomerPO;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.entity.AccountReceiptConfirm;
import com.seeease.flywheel.serve.financial.entity.AccountStockRelation;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPayment;
import com.seeease.flywheel.serve.financial.entity.FinancialGenerateDto;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.service.*;
import com.seeease.flywheel.serve.financial.template.payment.PaymentHTemplate;
import com.seeease.flywheel.serve.financial.template.payment.PaymentLTemplate;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturn;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturnLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseReturnLineStateEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseReturnLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseReturnService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.LogisticsDeliveryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 采购退货监听 发货
 * @Date create in 2023/3/16 11:07
 */
@Slf4j
@Component
public class PurchaseReturnListenerForLogisticsDelivery implements BillHandlerEventListener<LogisticsDeliveryEvent> {
    private static List<BusinessBillTypeEnum> PURCHASE_RETURN_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.CG_TH
    );
    @Resource
    private BillPurchaseReturnService billPurchaseReturnService;
    @Resource
    private BillPurchaseReturnLineService billPurchaseReturnLineService;
    @Resource
    private FinancialDocumentsService financialDocumentsService;
    @Resource
    private AccountsPayableAccountingService accountingService;
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
    private BillPurchaseService billPurchaseService;

    @Override
    public void onApplicationEvent(LogisticsDeliveryEvent event) {
        List<BillStoreWorkPre> workPreList = event.getWorkPreList();

        if (CollectionUtils.isEmpty(workPreList)) {
            return;
        }

        workPreList.forEach(t -> {

            if (!PURCHASE_RETURN_TYPE.contains(t.getWorkSource())) {
                return;
            }
            switch (t.getWorkSource()) {
                case CG_TH:

                    // 采购退货 发货/仓库出库
                    BillPurchaseReturn purchaseReturn = billPurchaseReturnService.getOne(new LambdaQueryWrapper<BillPurchaseReturn>()
                            .eq(BillPurchaseReturn::getSerialNo, t.getOriginSerialNo()));

                    BillPurchaseReturnLine line = billPurchaseReturnLineService.getOne(new LambdaQueryWrapper<BillPurchaseReturnLine>()
                            .eq(BillPurchaseReturnLine::getStockId, t.getStockId()).eq(BillPurchaseReturnLine::getPurchaseReturnId, purchaseReturn.getId()));

                    FinancialGenerateDto dto = new FinancialGenerateDto();
                    dto.setId(purchaseReturn.getId());
                    dto.setStockList(Lists.newArrayList(t.getStockId()));
                    dto.setType(line.getStockSrc());
                    financialDocumentsService.generatePurchaseReturn(dto);

                    billPurchaseReturnLineService.noticeListener(PurchaseReturnLineNotice.builder().
                            stockId(t.getStockId()).lineState(PurchaseReturnLineStateEnum.WAITING_LOGISTICS_DELIVERY).
                            serialNo(t.getOriginSerialNo()).expressNumber(event.getDeliveryExpressNumber())
                            .build());

                    stockService.updateStockStatus(Arrays.asList(t.getStockId()), StockStatusEnum.TransitionEnum.PURCHASE_RETURNED_ING_PURCHASE_RETURNED);

                    BillPurchase billPurchase = billPurchaseService.billPurchaseQuery(line.getOriginSerialNo());

                    //同行寄售结算后 变成同行采购-批量 //todo

                    //申请打款单 可能没打款 核销中 未核销
                    List<AccountStockRelation> accountStockRelationList = accountStockRelationService.list(Wrappers.<AccountStockRelation>lambdaQuery()
                            .eq(AccountStockRelation::getOriginSerialNo, line.getOriginSerialNo())
                            .eq(AccountStockRelation::getStockId, line.getStockId())
                            .isNotNull(AccountStockRelation::getAfpId)
                    );

                    switch (billPurchase.getPurchaseSource()) {
                        case TH_CG_BH:
                        case TH_CG_DJ:
                        case GR_HS_JHS:
                            //产生确认收款单 产生应付的负数
                            //一个采购退货单 采购退货行 关联多个采购单
                            paymentHTemplate.createReceiptAndGeneratePayable(new JSONObject()
                                    .fluentPut("purchaseReturn", purchaseReturn)
                                    .fluentPut("purchaseReturnLine", Arrays.asList(line))
                                    .fluentPut("purchase", billPurchase));

                            break;
                        case TH_JS:
                        case TH_CG_PL:
                        case GR_JS:

                            if (CollectionUtils.isEmpty(accountStockRelationList)) {

                                if (t.getWorkSource() == BusinessBillTypeEnum.GR_JS && DateUtil.between(DateUtil.parse("2024-03-01 00:00:00"), billPurchase.getCreatedTime(), DateUnit.SECOND, false) >= 0
                                ) {
                                    //未结算
                                    paymentLTemplate.listenerVerification(new JSONObject()
                                            .fluentPut("stockId", t.getStockId())
                                            .fluentPut("originSerialNo", billPurchase.getSerialNo()
                                            )
                                    );
                                } else if (Arrays.asList(BusinessBillTypeEnum.TH_JS, BusinessBillTypeEnum.TH_CG_PL).contains(t.getWorkSource())){
                                    //未结算
                                    paymentLTemplate.listenerVerification(new JSONObject()
                                            .fluentPut("stockId", t.getStockId())
                                            .fluentPut("originSerialNo", billPurchase.getSerialNo()
                                            )
                                    );
                                }

                            } else {
                                AccountStockRelation accountStockRelation = accountStockRelationList.stream().findFirst().get();
                                ApplyFinancialPayment payment = applyFinancialPaymentService.getById(accountStockRelation.getAfpId());

                                if (payment.getState() == ApplyFinancialPaymentStateEnum.PAID) {
                                    //已结算
                                    //创建确认收款单
//                                    accountReceiptConfirmAdd(t, billPurchase, purchaseReturn, line);
                                    paymentHTemplate.createReceiptAndGeneratePayable(new JSONObject()
                                            .fluentPut("purchaseReturn", purchaseReturn)
                                            .fluentPut("purchaseReturnLine", Arrays.asList(line))
                                            .fluentPut("purchase", billPurchase));
                                } else if (payment.getState() == ApplyFinancialPaymentStateEnum.PENDING_REVIEW) {
                                    //结算中 ??? 怎么处理
                                } else {
                                    //申请打款单其他状态
                                    log.warn("打款其他状态，{}", payment.getState());
                                }
                            }

                            break;
                        case GR_HS_ZH:
                            AccountStockRelation accountStockRelation = accountStockRelationList.stream().findFirst().get();
                            ApplyFinancialPayment payment = applyFinancialPaymentService.getById(accountStockRelation.getAfpId());

                            if (CollectionUtils.isNotEmpty(accountStockRelationList)) {
                                if (payment.getState() == ApplyFinancialPaymentStateEnum.PAID) {
                                    paymentHTemplate.createReceiptAndGeneratePayable(new JSONObject()
                                            .fluentPut("purchaseReturn", purchaseReturn)
                                            .fluentPut("purchaseReturnLine", Arrays.asList(line))
                                            .fluentPut("purchase", billPurchase));
                                } else if (payment.getState() == ApplyFinancialPaymentStateEnum.PENDING_REVIEW) {
                                    //结算中 ??? 怎么处理
                                } else {
                                    //申请打款单其他状态
                                    log.warn("打款其他状态，{}", payment.getState());
                                }
                            }
                            break;
                    }


                    //bug PVOJ-420 同行采购-寄售商品进行退货时，不生成确认收款单
//                    if (!BusinessBillTypeEnum.TH_JS.equals(billPurchase.getPurchaseSource())) {
//                        accountReceiptConfirmAdd(t, billPurchase, purchaseReturn, line);
//
//                        //采购退货 物流发货后 预付单生成 //todo
////                        accountingService.createApaByReturn(serialNo, purchaseReturn.getId(), Lists.newArrayList(t.getStockId()),
////                                ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT, FinancialStatusEnum.RETURN_PENDING_REVIEW);
//                        //未打款
//                    } else if (t.getWorkSource() == BusinessBillTypeEnum.TH_JS
//                            && stockService.getById(t.getStockId()).getStockSrc().equals(BusinessBillTypeEnum.TH_JS.getValue())) {
//                        //核销掉应付单 //应收应付核销掉 todo 同行寄售特殊情况
//
//                        paymentLTemplate.listenerVerification(new JSONObject()
//                                .fluentPut("stockId", t.getStockId())
//                                .fluentPut("originSerialNo", t.getOriginSerialNo()
//                                )
//                        );
//                        return;
//                    } else {
//
//                    }
//                    break;
            }
        });
    }


    String accountReceiptConfirmAdd(BillStoreWorkPre billStoreWorkPre, BillPurchase billPurchase, BillPurchaseReturn purchaseReturn, BillPurchaseReturnLine line) {
        log.info("accountReceiptConfirmAdd function of PurchaseReturnListenerForLogisticsDelivery start and billStoreWorkPre = {}", JSON.toJSONString(billStoreWorkPre));
        List<CustomerContacts> customerContactsList = customerContactsService.searchByCustomerId(billStoreWorkPre.getCustomerId());
        customerContactsList = customerContactsList.stream().filter(Objects::nonNull)
                .filter(e -> e.getId().equals(billStoreWorkPre.getCustomerContactId())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(customerContactsList)) {
            log.info("customerContactsList function is empty .");
            return null;
        }

        CustomerPO customerPO = customerService.queryCustomerPO(billStoreWorkPre.getCustomerId());

        CustomerContacts customerContacts = customerContactsList.get(0);
        AccountReceiptConfirmAddRequest request = new AccountReceiptConfirmAddRequest();
        request.setCustomerId(billStoreWorkPre.getCustomerId());
        request.setCustomerName(null != customerPO && StringUtils.isNotEmpty(customerPO.getCustomerName()) ? customerPO.getCustomerName() : FlywheelConstant.CUSTOMER_CONTACTNAME_VALUE);
        request.setContactId(billStoreWorkPre.getCustomerContactId());
        request.setContactName(customerContacts.getName());
        request.setContactAddress(customerContacts.getAddress());
        request.setContactPhone(customerContacts.getPhone());
        request.setShopId(purchaseReturn.getStoreId());
        request.setTotalNumber(FlywheelConstant.ONE);

        request.setMiniAppSource(Boolean.FALSE);
        request.setOriginSerialNo(billPurchase.getSerialNo());
        request.setReceivableAmount(line.getPurchaseReturnPrice());
        request.setWaitAuditPrice(line.getPurchaseReturnPrice());
        request.setOriginType(OriginTypeEnum.CG_TH.getValue());
        request.setClassification(FinancialClassificationEnum.CG_TH.getValue());
        request.setCollectionType(CollectionTypeEnum.CG_TK.getValue());
        request.setStatus(AccountReceiptConfirmStatusEnum.WAIT.getValue());
        request.setSalesMethod(PurchaseModeEnum.convert(billPurchase.getPurchaseMode()).getValue());
        //request.setCollectionNature(CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue());

        AccountReceiptConfirm accountReceiptConfirm = accountReceiptConfirmService.accountReceiptConfirmAdd(request);
        Integer accountReceiptConfirmId = accountReceiptConfirm.getId();

        AccountStockRelation accountStockRelation = new AccountStockRelation();
        accountStockRelation.setArcId(accountReceiptConfirmId);
        accountStockRelation.setOriginSerialNo(purchaseReturn.getSerialNo());
        accountStockRelation.setStockId(line.getStockId());
        accountStockRelation.setOriginPrice(line.getPurchaseReturnPrice());

        accountStockRelationService.AccountStockRelationAdd(accountStockRelation);

        return accountReceiptConfirm.getSerialNo();
    }

    @Resource
    private PaymentLTemplate paymentLTemplate;

    @Resource
    private PaymentHTemplate paymentHTemplate;


    @Resource
    private ApplyFinancialPaymentService applyFinancialPaymentService;
}
