package com.seeease.flywheel.serve.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 单号生成工具
 *
 * @author Tiro
 * @date 2023/2/2
 */
public abstract class SerialNoGenerator extends SerialIncrUtil {

    @AllArgsConstructor
    @Getter
    enum Type {
        PURCHASE("CG", "采购单"),
        PURCHASE_RETURN("CGTH", "采购退货单"),
        PURCHASE_PLAN("CGJH", "采购计划"),
        FIX("WX", "维修单"),
        QT("ZJ", "质检单"),
        STORE_WORK_IN("RK", "入库单"),
        STORE_WORK_OUT("CK", "出库单"),
        APPLY_FINANCIAL_PAYMENT("SQCWDK", "申请财务打款"),
        ALLOCATE("DB", "调拨单"),
        TO_C_SALE_ORDER("TOCXS", "toC销售单"),
        TO_B_SALE_ORDER("TOBXS", "toB销售单"),
        TO_C_SALE_RETURN_ORDER("TOCXSTH", "toC销售单退货"),
        TO_B_SALE_RETURN_ORDER("TOBXSTH", "toB销售单退货"),
        PRICING("DJ", "定价"),
        PRICING_APPLY("DJSQ", "定价申请"),
        ANOMALY("YCCL", "异常处理"),
        APPLY_PURCHASE("CGXQ", "采购需求"),
        FINANCE_APPLY_PURCHASE("CWQR", "财务确认"),
        JS_DR("JSDR", "寄售调入"),
        JS_DC("JSDC", "寄售调出"),
        FW_ZC("FWZC", "服务费支出"),
        FW_SR("FWSR", "服务费收入"),
        PRE_PAID_AMOUNT("AYF", "预付单"),
        PRE_RECEIVE_AMOUNT("AYS", "预收单"),
        AMOUNT_PAYABLE("ZYF", "应付单"),
        AMOUNT_RECEIVABLE("ZYS", "应收单"),
        Stocktaking("PD", "盘点单"),
        RECYCLE("SFHS", "商城回收"),
        BUY_BACK("SFHG", "商城回购"),
        ACCOUNT_RECEIPT_CONFIRM("CWQRSK", "财务确认收款单"),
        BREAK_C_PRICE("PJSQ","破c价申请"),
        FINANCIAL_INVOICE("SQKP", "申请开票"),
        SCRAP_STOCK("BFD", "报废单"),

        FIX_SITE("WXZD", "维修站点"),
        FIX_SITE_TAG("ZDDJ", "站点等级");
        private String prefix;
        private String desc;
    }
    public static String generateScrapStockSerialNo() {
        return generateSerial(Type.SCRAP_STOCK.getPrefix());
    }

    /**
     * 破c价申请
     *
     * @return
     */
    public static String generateBreakCPriceSerialNo() {
        return generateSerial(Type.BREAK_C_PRICE.getPrefix());
    }

    /**
     * 申请财务打款
     *
     * @return
     */
    public static String generateApplyFinancialPaymentSerialNo() {
        return generateSerial(Type.APPLY_FINANCIAL_PAYMENT.getPrefix());
    }

    /**
     * 申请开票
     *
     * @return
     */
    public static String generateFinancialInvoiceSerialNo() {
        return generateSerial(Type.FINANCIAL_INVOICE.getPrefix());
    }

    /**
     * 应收应付单
     *
     * @return
     */
    public static String generatePrePaidAmountSerialNo() {
        return generateSerial(Type.PRE_PAID_AMOUNT.getPrefix());
    }

    public static String generatePreReceiveAmountSerialNo() {
        return generateSerial(Type.PRE_RECEIVE_AMOUNT.getPrefix());
    }

    public static String generateAmountPayableSerialNo() {
        return generateSerial(Type.AMOUNT_PAYABLE.getPrefix());
    }

    /**
     * 确认收款单
     *
     * @return
     */
    public static String generateAccountReceiptConfirmSerialNo() {
        return generateSerial(Type.ACCOUNT_RECEIPT_CONFIRM.getPrefix());
    }

    public static String generateAmountReceivableSerialNo() {
        return generateSerial(Type.AMOUNT_RECEIVABLE.getPrefix());
    }

    public static String generateToCSaleOrderSerialNo() {
        return generateSerial(Type.TO_C_SALE_ORDER.getPrefix());
    }

    public static String generateToBSaleOrderSerialNo() {
        return generateSerial(Type.TO_B_SALE_ORDER.getPrefix());
    }

    public static String generateToCSaleReturnOrderSerialNo() {
        return generateSerial(Type.TO_C_SALE_RETURN_ORDER.getPrefix());
    }

    public static String generateToBSaleReturnOrderSerialNo() {
        return generateSerial(Type.TO_B_SALE_RETURN_ORDER.getPrefix());
    }

    /**
     * 生成采购单号
     *
     * @return
     */
    public static String generatePurchaseSerialNo() {
        return generateSerial(Type.PURCHASE.getPrefix());
    }

    /**
     * 采购计划
     *
     * @return
     */
    public static String generatePurchasePlanSerialNo() {
        return generateSerial(Type.PURCHASE_PLAN.getPrefix());
    }

    /**
     * 生成采购退货单号
     *
     * @return
     */
    public static String generatePurchaseReturnSerialNo() {
        return generateSerial(Type.PURCHASE_RETURN.getPrefix());
    }

    /**
     * 生成维修单号
     *
     * @return
     */
    public static String generateFixSerialNo() {
        return generateSerial(Type.FIX.getPrefix());
    }


    /**
     * 生成质检单号
     *
     * @return
     */
    public static String generateQTSerialNo() {
        return generateSerial(Type.QT.getPrefix());
    }

    /**
     * 生成入库单号
     *
     * @return
     */
    public static String generateInStoreSerialNo() {
        return generateSerial(Type.STORE_WORK_IN.getPrefix());
    }

    /**
     * 生成入库单号
     *
     * @return
     */
    public static String generateOutStoreSerialNo() {
        return generateSerial(Type.STORE_WORK_OUT.getPrefix());
    }

    /**
     * 生成调拨单单号
     *
     * @return
     */
    public static String generateAllocateSerialNo() {
        return generateSerial(Type.ALLOCATE.getPrefix());
    }


    /**
     * 生成财务寄售调入
     *
     * @return
     */
    public static String generateJSDRSerialNo() {
        return generateSerial(Type.JS_DR.getPrefix());
    }


    /**
     * 生成财务寄售调出
     *
     * @return
     */
    public static String generateJSDCSerialNo() {
        return generateSerial(Type.JS_DC.getPrefix());
    }

    public static String generateFWZCSerialNo() {
        return generateSerial(Type.FW_ZC.getPrefix());
    }

    public static String generateFWSRSerialNo() {
        return generateSerial(Type.FW_SR.getPrefix());
    }

    public static String generatePricingSerialNo() {
        return generateSerial(Type.PRICING.getPrefix());
    }

    public static String generateApplyPricingSerialNo() {
        return generateSerial(Type.PRICING_APPLY.getPrefix());
    }

    public static String generateAnomalySerialNo() {
        return generateSerial(Type.ANOMALY.getPrefix());
    }

    public static String generateStocktakingSerialNo() {
        return generateSerial(Type.Stocktaking.getPrefix());
    }

    /**
     * 生成商城回收
     *
     * @return
     */
    public static String generateRecycleSerialNo() {
        return generateSerial(Type.RECYCLE.getPrefix());
    }

    /**
     * 生成商城回购
     *
     * @return
     */
    public static String generateBuyBackSerialNo() {
        return generateSerial(Type.BUY_BACK.getPrefix());
    }

    /**
     * 生成商城回购
     *
     * @return
     */
    public static String generatePurchaseTaskSerialNo() {
        return generateSerial(Type.APPLY_PURCHASE.getPrefix());
    }

    /**
     * 生成维修站点等级
     *
     * @return
     */
    public static String generateFixSiteTagSerialNo() {
        return generateSerial(Type.FIX_SITE_TAG.getPrefix());
    }

    /**
     * 生成维修站点
     *
     * @return
     */
    public static String generateFixSiteSerialNo() {
        return generateSerial(Type.FIX_SITE.getPrefix());
    }
}
