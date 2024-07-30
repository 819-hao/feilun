package com.seeease.flywheel.serve.purchase.event;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.PurchaseLineNotice;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.financial.service.ApplyFinancialPaymentService;
import com.seeease.flywheel.serve.financial.template.payment.PaymentCTemplate;
import com.seeease.flywheel.serve.financial.template.payment.PaymentDTemplate;
import com.seeease.flywheel.serve.financial.template.payment.PaymentETemplate;
import com.seeease.flywheel.serve.financial.template.payment.PaymentFTemplate;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.InStorageEvent;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 仓库入库
 *
 * @author Tiro
 * @date 2023/3/9
 */
@Component
@Slf4j
public class PurchaseListenerForInStorage implements BillHandlerEventListener<InStorageEvent> {

    private static List<BusinessBillTypeEnum> PURCHASE_TYPE = Lists.newArrayList(BusinessBillTypeEnum.TO_C_XS_TH, BusinessBillTypeEnum.TO_C_XS_TH,

            BusinessBillTypeEnum.TH_CG_DJ, BusinessBillTypeEnum.TH_CG_BH, BusinessBillTypeEnum.TH_CG_PL, BusinessBillTypeEnum.TH_CG_QK, BusinessBillTypeEnum.TH_CG_DJTP, BusinessBillTypeEnum.TH_JS, BusinessBillTypeEnum.GR_JS, BusinessBillTypeEnum.GR_HS_JHS, BusinessBillTypeEnum.GR_HS_ZH, BusinessBillTypeEnum.GR_HG_ZH, BusinessBillTypeEnum.GR_HG_JHS);

    @Resource
    private BillPurchaseLineService billPurchaseLineService;

    @Resource
    private BillPurchaseService billPurchaseService;

    @Resource
    private StockService stockService;

    @Resource
    private AccountsPayableAccountingService accountingService;

    @Resource
    private ApplyFinancialPaymentService paymentService;

    @Resource
    private PaymentCTemplate paymentCTemplate;
    @Resource
    private PaymentDTemplate paymentDTemplate;
    @Resource
    private PaymentETemplate paymentETemplate;
    @Resource
    private PaymentFTemplate paymentFTemplate;

    @Override
    public void onApplicationEvent(InStorageEvent event) {

        List<BillStoreWorkPre> workPreList = event.getWorkPreList();

        if (CollectionUtils.isEmpty(workPreList)) {
            return;
        }
        workPreList.forEach(t -> {

            if (!PURCHASE_TYPE.contains(t.getWorkSource())) {
                return;
            }

            switch (t.getWorkSource()) {
                case TH_CG_DJ:
                case TH_CG_BH:
                case TH_CG_PL:
                case TH_JS:
                case GR_HS_JHS:
                case GR_HS_ZH:
                case GR_HG_JHS:
                case GR_HG_ZH:
                case TH_CG_QK:
                case TH_CG_DJTP:

                    Stock stock = stockService.getById(t.getStockId());

                    if (ObjectUtils.isEmpty(stock)) {
                        log.error("表不存在，id={}", t.getStockId());
                        break;
                    }

                    if (t.getExceptionMark().equals(WhetherEnum.YES)) {
                        //
                        stockService.updateStockStatus(Arrays.asList(t.getStockId()), StockStatusEnum.TransitionEnum.PURCHASE_IN_TRANSIT_EXCEPTION);
                    } else {
                        /**
                         * 从采购途中变待定价
                         */
                        if ((ObjectUtils.isNotEmpty(stock.getStockStatus()) && stock.getStockStatus() == StockStatusEnum.PURCHASE_IN_TRANSIT) && (ObjectUtils.isNotEmpty(stock.getTobPrice()) && ObjectUtils.isNotEmpty(stock.getTocPrice()) && ObjectUtils.isNotEmpty(stock.getTagPrice()))) {

                            stockService.updateStockStatus(Arrays.asList(t.getStockId()), StockStatusEnum.TransitionEnum.PURCHASE_IN_TRANSIT_MARKETABLE);
                        } else {
                            stockService.updateStockStatus(Arrays.asList(t.getStockId()), StockStatusEnum.TransitionEnum.PURCHASE_IN_TRANSIT_WAIT_PRICING);
                        }
                    }

                    billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().stockId(t.getStockId()).lineState(PurchaseLineStateEnum.WAREHOUSED).serialNo(t.getOriginSerialNo()).computeConsignmentPrice(WhetherEnum.YES).build());

                    switch (t.getWorkSource()) {
                        //同行采购（订金/备货) 商品入库后 自动核销节点
                        case TH_CG_DJ:
                        case TH_CG_BH:
                        case GR_HS_JHS:
                            //个人回收 入库后自动核销
                            paymentCTemplate.listenerVerification(new JSONObject().fluentPut("stockId", t.getStockId()).fluentPut("originSerialNo", t.getOriginSerialNo()));
                            break;

                        case TH_CG_QK:
                        case TH_CG_DJTP:
                        case GR_HS_ZH:
                            //个人置换 预付单入库后自动核销
                            if (t.getWorkSource() == BusinessBillTypeEnum.GR_HS_ZH) {
                                List<BillPurchaseLine> purchaseLineList = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getStockId, t.getStockId()));

                                Assert.notEmpty(purchaseLineList, "采购行为空");

                                List<BillPurchase> purchaseList = billPurchaseService.list(Wrappers.<BillPurchase>lambdaQuery().eq(BillPurchase::getSerialNo, t.getOriginSerialNo()));

                                Assert.notEmpty(purchaseList, "采购为空");

                                if (DateUtil.between(purchaseList.stream().findFirst().get().getCreatedTime(), DateUtil.parse("2024-03-01 00:00:00"), DateUnit.SECOND, false) <= 0) {
                                    paymentETemplate.listenerVerification(new JSONObject().fluentPut("node", 1).fluentPut("purchase", purchaseList.stream().findFirst().get()).fluentPut("originSerialNo", t.getOriginSerialNo()).fluentPut("stockId", t.getStockId()));
                                }

                            } else {
                                paymentETemplate.listenerVerification(new JSONObject().fluentPut("node", 1).fluentPut("originSerialNo", t.getOriginSerialNo()).fluentPut("stockId", t.getStockId()));
                            }

                            break;
                        case GR_HG_JHS:
                        case GR_HG_ZH:
//                            List<AccountsPayableAccounting> list = accountingService.selectListByOriginSerialNoAndStatusAndType(t.getOriginSerialNo(), Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW), Lists.newArrayList(ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT, ReceiptPaymentTypeEnum.AMOUNT_PAYABLE));
//                            accountingService.batchAudit(list.stream().filter(a -> t.getStockId().equals(a.getStockId())).map(AccountsPayableAccounting::getId).collect(Collectors.toList()), FlywheelConstant.IN_STORE_AUDIT, UserContext.getUser().getUserName());
                            List<BillPurchase> purchaseList = billPurchaseService.list(Wrappers.<BillPurchase>lambdaQuery().eq(BillPurchase::getSerialNo, t.getOriginSerialNo()));

                            Assert.notEmpty(purchaseList, "采购为空");
                            if (t.getWorkSource() == BusinessBillTypeEnum.GR_HG_ZH) {
                                List<BillPurchaseLine> purchaseLineList = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getStockId, t.getStockId()));

                                Assert.notEmpty(purchaseLineList, "采购行为空");

//                                List<BillPurchase> purchaseList = billPurchaseService.list(Wrappers.<BillPurchase>lambdaQuery().eq(BillPurchase::getSerialNo, t.getOriginSerialNo()));
//
//                                Assert.notEmpty(purchaseList, "采购为空");
                                if (DateUtil.between(purchaseList.stream().findFirst().get().getCreatedTime(), DateUtil.parse("2024-03-01 00:00:00"), DateUnit.SECOND, false) <= 0) {

                                    paymentFTemplate.listenerVerification(new JSONObject().fluentPut("node", 1).fluentPut("purchase", purchaseList.stream().findFirst().get()).fluentPut("originSerialNo", t.getOriginSerialNo()).fluentPut("stockId", t.getStockId()));
                                }
                            } else {
                                if (DateUtil.between(purchaseList.stream().findFirst().get().getCreatedTime(), DateUtil.parse("2024-03-01 00:00:00"), DateUnit.SECOND, false) <= 0) {

                                    paymentFTemplate.listenerVerification(new JSONObject().fluentPut("node", 1).fluentPut("originSerialNo", t.getOriginSerialNo()).fluentPut("stockId", t.getStockId()));
                                }
                            }

                            break;
                        case TH_CG_PL:
                            //新增 & 同行寄售
                        case TH_JS:

                            List<BillPurchaseLine> purchaseLineList = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getStockId, t.getStockId()));

                            Assert.notEmpty(purchaseLineList, "采购行为空");

                            List<BillPurchase> purchaseList2 = billPurchaseService.list(Wrappers.<BillPurchase>lambdaQuery().eq(BillPurchase::getSerialNo, t.getOriginSerialNo()));

                            Assert.notEmpty(purchaseList2, "采购为空");

                            paymentDTemplate.generatePayable(new JSONObject().fluentPut("purchase", purchaseList2.stream().findFirst().get()).fluentPut("purchaseLine", purchaseLineList));
                            //同行采购（批量） 商品入库后 应付单生成节点
//                            accountingService.createApa(t.getOriginSerialNo(), ReceiptPaymentTypeEnum.AMOUNT_PAYABLE,
//                                    FinancialStatusEnum.PENDING_REVIEW, Lists.newArrayList(t.getStockId()), null, false);
                            break;
                    }

                    break;
                case GR_JS:
                    billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().stockId(t.getStockId()).lineState(PurchaseLineStateEnum.ON_CONSIGNMENT).computeConsignmentPrice(WhetherEnum.YES).serialNo(t.getOriginSerialNo()).build());

                    Stock stockServiceById = stockService.getById(t.getStockId());

                    if (ObjectUtils.isEmpty(stockServiceById)) {
                        log.error("表不存在，id={}", t.getStockId());
                        break;
                    }
                    /**
                     * 从采购途中变待定价
                     */
                    if ((ObjectUtils.isNotEmpty(stockServiceById.getStockStatus()) && stockServiceById.getStockStatus() == StockStatusEnum.PURCHASE_IN_TRANSIT) && stockServiceById.getTobPrice().compareTo(BigDecimal.ZERO) > 0 && stockServiceById.getTocPrice().compareTo(BigDecimal.ZERO) > 0 && stockServiceById.getTagPrice().compareTo(BigDecimal.ZERO) > 0) {
                        stockService.updateStockStatus(Arrays.asList(t.getStockId()), StockStatusEnum.TransitionEnum.PURCHASE_IN_TRANSIT_MARKETABLE);
                    } else {
                        stockService.updateStockStatus(Arrays.asList(t.getStockId()), StockStatusEnum.TransitionEnum.PURCHASE_IN_TRANSIT_WAIT_PRICING);
                    }

                    List<BillPurchaseLine> purchaseLineList = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getStockId, t.getStockId()));

                    Assert.notEmpty(purchaseLineList, "采购行为空");

                    List<BillPurchase> purchaseList = billPurchaseService.list(Wrappers.<BillPurchase>lambdaQuery().eq(BillPurchase::getSerialNo, t.getOriginSerialNo()));

                    Assert.notEmpty(purchaseList, "采购为空");

                    paymentDTemplate.generatePayable(new JSONObject().fluentPut("purchase", purchaseList.stream().findFirst().get()).fluentPut("purchaseLine", purchaseLineList));


                    break;

                /**
                 * ？？？？？
                 */
                case TO_C_XS_TH:
                case TO_B_XS_TH:

                    //只有销售发货 会通知采购这边 寄售中才能
                    BillPurchaseLine billPurchaseLineTh = billPurchaseLineService.getOne(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getStockId, t.getStockId()).eq(BillPurchaseLine::getPurchaseLineState, PurchaseLineStateEnum.TO_BE_SETTLED));
                    if (ObjectUtils.isEmpty(billPurchaseLineTh)) {
                        return;
                    }
                    billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().stockId(t.getStockId()).lineState(PurchaseLineStateEnum.ON_CONSIGNMENT).purchaseId(billPurchaseLineTh.getPurchaseId()).isSettlement(WhetherEnum.NO).build());
                    break;
            }
        });

    }
}
