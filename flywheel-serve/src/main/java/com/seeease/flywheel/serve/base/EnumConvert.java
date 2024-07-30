package com.seeease.flywheel.serve.base;

import com.seeease.flywheel.serve.account.enums.CompanyGroupEnum;
import com.seeease.flywheel.serve.account.enums.CompanyTypeEnum;
import com.seeease.flywheel.serve.account.enums.PageTypeEnum;
import com.seeease.flywheel.serve.account.enums.ShopGroupEnum;
import com.seeease.flywheel.serve.allocate.enums.AllocateLineStateEnum;
import com.seeease.flywheel.serve.allocate.enums.AllocateStateEnum;
import com.seeease.flywheel.serve.allocate.enums.AllocateTaskStateEnum;
import com.seeease.flywheel.serve.allocate.enums.AllocateTypeEnum;
import com.seeease.flywheel.serve.customer.enums.CustomerTypeEnum;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.fix.enums.*;
import com.seeease.flywheel.serve.goods.enums.SeriesTypeEnum;
import com.seeease.flywheel.serve.goods.enums.StockPromotionEnum;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.enums.StockUndersellingEnum;
import com.seeease.flywheel.serve.fix.enums.FixSourceEnum;
import com.seeease.flywheel.serve.fix.enums.FixStateEnum;
import com.seeease.flywheel.serve.fix.enums.FixTypeEnum;
import com.seeease.flywheel.serve.fix.enums.FlowGradeEnum;
import com.seeease.flywheel.serve.goods.enums.*;
import com.seeease.flywheel.serve.helper.enmus.BreakPriceAuditStatusEnum;
import com.seeease.flywheel.serve.maindata.enums.FixSiteEnum;
import com.seeease.flywheel.serve.pricing.enums.ApplyPricingStateEnum;
import com.seeease.flywheel.serve.pricing.enums.PricingNodeEnum;
import com.seeease.flywheel.serve.pricing.enums.PricingStateEnum;
import com.seeease.flywheel.serve.purchase.enums.*;
import com.seeease.flywheel.serve.qt.enums.QualityTestingConclusionEnum;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.flywheel.serve.recycle.enums.RecycleOrderTypeEnum;
import com.seeease.flywheel.serve.recycle.enums.RecycleStateEnum;
import com.seeease.flywheel.serve.sale.enums.*;
import com.seeease.flywheel.serve.sf.enums.ExpressOrderSourceEnum;
import com.seeease.flywheel.serve.sf.enums.ExpressOrderStateEnum;
import com.seeease.flywheel.serve.stocktaking.enums.StocktakingLineStateEnum;
import com.seeease.flywheel.serve.stocktaking.enums.StocktakingSourceEnum;
import com.seeease.flywheel.serve.stocktaking.enums.StocktakingStateEnum;
import com.seeease.flywheel.serve.storework.enums.*;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Tiro
 * @date 2023/2/3
 */
public interface EnumConvert {

    default Integer convert(BreakPriceAuditStatusEnum statusEnum) {
        return statusEnum.getValue();
    }


    default Integer convert(PurchaseDemandStatusEnum e) {
        return e.getValue();
    }

    default StockPromotionEnum convertStockPromotionEnum(Integer value) {
        return StockPromotionEnum.fromValue(value);
    }

    default Integer convert(StockPromotionEnum e) {
        return e.getValue();
    }

    default StockUndersellingEnum convertStockUndersellingEnum(Integer value) {
        return StockUndersellingEnum.fromValue(value);
    }

    default Integer convert(StockUndersellingEnum e) {
        return e.getValue();
    }

    default BusinessBillStateEnum convertBusinessBillStateEnum(Integer value) {
        return BusinessBillStateEnum.fromCode(value);
    }

    default Integer convert(BusinessBillStateEnum e) {
        return e.getValue();
    }

    default String formatDateString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    default Date formatStringToDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
    }

    // 去除首位空白字符
    default String trim(String str) {
        return StringUtils.trim(str);
    }

    default WhetherEnum convertWhetherEnum(Integer value) {
        return WhetherEnum.fromValue(value);
    }

    default Integer convert(WhetherEnum e) {
        return e.getValue();
    }

    //--------------------------------------PurchaseReturn--------------------------------------------------
    default Integer convert(PurchaseReturnLineStateEnum typeEnum) {
        return typeEnum.getValue();
    }

    default PurchaseReturnLineStateEnum convertPurchaseReturnLineStateEnum(Integer value) {
        return PurchaseReturnLineStateEnum.fromValue(value);
    }

    //------------------------------------------End---------------------------------------------------------
    //--------------------------------------Purchase--------------------------------------------------
    default Integer convert(PurchaseTypeEnum typeEnum) {
        return typeEnum.getValue();
    }

    default PurchaseTypeEnum convertPurchaseTypeEnum(Integer value) {
        return PurchaseTypeEnum.fromCode(value);
    }

    default Integer convert(PurchaseLineStateEnum typeEnum) {
        return typeEnum.getValue();
    }

    default PurchaseLineStateEnum convertPurchaseLineStateEnum(Integer value) {
        return PurchaseLineStateEnum.fromValue(value);
    }

    default Integer convert(PurchaseModeEnum purchaseSourceEnum) {
        return purchaseSourceEnum.getValue();
    }

    default PurchaseModeEnum convertPurchaseSourceEnum(Integer value) {
        return PurchaseModeEnum.fromCode(value);
    }

    default Integer convert(PurchasePaymentMethodEnum paymentMethodEnum) {
        return paymentMethodEnum.getValue();
    }

    default PurchasePaymentMethodEnum convertPaymentMethodEnum(Integer value) {
        return PurchasePaymentMethodEnum.fromCode(value);
    }

    default Integer convert(SalesPriorityEnum salesPriorityEnum) {
        return salesPriorityEnum.getValue();
    }

    default SalesPriorityEnum convertSalesPriorityEnum(Integer value) {
        return SalesPriorityEnum.fromCode(value);
    }

    default Integer convert(RecycleModeEnum recycleModeEnum) {
        return recycleModeEnum.getValue();
    }

    default RecycleModeEnum convertRecycleModeEnum(Integer value) {
        return RecycleModeEnum.fromCode(value);
    }


    //----------------------------------------------------------------------------------------
    default Integer convert(BusinessBillTypeEnum typeEnum) {
        return typeEnum.getValue();
    }

    default BusinessBillTypeEnum convertBusinessBillTypeEnum(Integer value) {
        return BusinessBillTypeEnum.fromValue(value);
    }


    //----------------------------------------------------------------------------------------
    default Integer convert(StoreWorkTypeEnum typeEnum) {
        return typeEnum.getValue();
    }

    default StoreWorkTypeEnum convertStoreWorkTypeEnum(Integer value) {
        return StoreWorkTypeEnum.fromCode(value);
    }

    default Integer convert(StoreWorkStateEnum typeEnum) {
        return typeEnum.getValue();
    }

    default StoreWorkStateEnum convertStoreWorkStateEnum(Integer value) {
        return StoreWorkStateEnum.fromCode(value);
    }

    default Integer convert(StoreWorkCommoditySituationEnum typeEnum) {
        return typeEnum.getValue();
    }

    default StoreWorkCommoditySituationEnum convertStoreWorkCommoditySituationEnum(Integer value) {
        return StoreWorkCommoditySituationEnum.fromCode(value);
    }

    default Integer convert(WmsWorkCollectWorkStateEnum stateEnum) {
        return stateEnum.getValue();
    }

    default WmsWorkCollectWorkStateEnum convertWmsWorkCollectWorkStateEnum(Integer value) {
        return WmsWorkCollectWorkStateEnum.fromCode(value);
    }

    //----------------------------------------------------------------------------------------

    //-------------------------------------FIX-----START---------------------------------------------------

    default Integer convert(FixStateEnum fixStateEnum) {
        return fixStateEnum.getValue();
    }

    default FixStateEnum convertFixStateEnum(Integer fixState) {
        return FixStateEnum.fromCode(fixState);
    }


    default Integer convert(FixSourceEnum fixSourceEnum) {
        return fixSourceEnum.getValue();
    }

    default FixSourceEnum convertFixSourceEnum(Integer fixSource) {
        return FixSourceEnum.fromCode(fixSource);
    }

    default Integer convert(FixTypeEnum fixTypeEnum) {
        return fixTypeEnum.getValue();
    }

    default FixTypeEnum convertFixTypeEnum(Integer fixType) {
        return FixTypeEnum.fromCode(fixType);
    }

    default Integer convert(FlowGradeEnum flowGradeEnum) {
        return flowGradeEnum.getValue();
    }

    default FlowGradeEnum convertFlowGradeEnum(Integer flowGrade) {
        return FlowGradeEnum.fromCode(flowGrade);
    }

    //-------------------------------------FIX-----END---------------------------------------------------


    //-------------------------------------FIX-----END---------------------------------------------------

    //-------------------------------------APPLY-----START-----------------------------------------------
    default ApplyFinancialPaymentStateEnum convertApplyFinancialPaymentStateEnum(Integer value) {
        return ApplyFinancialPaymentStateEnum.fromCode(value);
    }

    default Integer convert(ApplyFinancialPaymentStateEnum e) {
        return e.getValue();
    }

    default ApplyFinancialPaymentTypeEnum convertApplyFinancialPaymentTypeEnum(Integer value) {
        return ApplyFinancialPaymentTypeEnum.fromCode(value);
    }

    default Integer convert(ApplyFinancialPaymentTypeEnum e) {
        return e.getValue();
    }

    //-------------------------------------APPLY-----END-------------------------------------------------

    //-------------------------------------SALE-----START-----------------------------------------------
    default SaleOrderPaymentMethodEnum convertSaleOrderPaymentMethodEnum(Integer value) {
        return SaleOrderPaymentMethodEnum.fromCode(value);
    }

    default Integer convert(SaleOrderPaymentMethodEnum e) {
        return e.getValue();
    }

    default SaleOrderChannelEnum convertSaleOrderChannelEnum(Integer value) {
        return SaleOrderChannelEnum.fromCode(value);
    }

    default Integer convert(SaleOrderChannelEnum e) {
        return e.getValue();
    }

    default SaleOrderBuyCauseEnum convertSaleOrderBuyCauseEnum(Integer value) {
        return SaleOrderBuyCauseEnum.fromCode(value);
    }

    default Integer convert(SaleOrderBuyCauseEnum e) {
        return e.getValue();
    }

    default SaleOrderModeEnum convertSaleOrderModeEnum(Integer value) {
        return SaleOrderModeEnum.fromCode(value);
    }

    default Integer convert(SaleOrderModeEnum e) {
        return e.getValue();
    }

    default SaleOrderTypeEnum convertSaleOrderTypeEnum(Integer value) {
        return SaleOrderTypeEnum.fromCode(value);
    }

    default Integer convert(SaleOrderTypeEnum e) {
        return e.getValue();
    }

    default SaleOrderInspectionTypeEnum convertSaleOrderInspectionTypeEnum(Integer value) {
        return SaleOrderInspectionTypeEnum.fromCode(value);
    }

    default Integer convert(SaleOrderInspectionTypeEnum e) {
        return e.getValue();
    }

    default Integer convert(SaleOrderReturnFlagEnum e) {
        return e.getValue();
    }

    default SaleOrderReturnFlagEnum convertSaleOrderReturnFlagEnum(Integer value) {
        return SaleOrderReturnFlagEnum.fromCode(value);
    }

    //-------------------------------------SALE-----END-----------------------------------------------

    //-------------------------------------SALE_ORDER------START------------------------------------------
    default SaleReturnOrderTypeEnum convertSaleReturnOrderTypeEnum(Integer value) {
        return SaleReturnOrderTypeEnum.fromCode(value);
    }

    default Integer convert(SaleReturnOrderTypeEnum e) {
        return e.getValue();
    }

    default SaleOrderStateEnum convertSaleOrderStateEnum(Integer value) {
        return SaleOrderStateEnum.fromCode(value);
    }

    default Integer convert(SaleOrderStateEnum e) {
        return e.getValue();
    }

    //-------------------------------------SALE_ORDER------END------------------------------------------

    //-------------------------------------ALLOCATE-----START-----------------------------------------------

    default Integer convert(AllocateLineStateEnum flowGradeEnum) {
        return flowGradeEnum.getValue();
    }

    default AllocateLineStateEnum convertAllocateLineStateEnum(Integer flowGrade) {
        return AllocateLineStateEnum.fromCode(flowGrade);
    }

    default Integer convert(AllocateStateEnum flowGradeEnum) {
        return flowGradeEnum.getValue();
    }

    default AllocateStateEnum convertAllocateStateEnum(Integer flowGrade) {
        return AllocateStateEnum.fromCode(flowGrade);
    }

    default Integer convert(AllocateTypeEnum flowGradeEnum) {
        return flowGradeEnum.getValue();
    }

    default AllocateTypeEnum convertAllocateTypeEnum(Integer flowGrade) {
        return AllocateTypeEnum.fromCode(flowGrade);
    }

    default Integer convert(AllocateTaskStateEnum flowGradeEnum) {
        return flowGradeEnum.getValue();
    }

    default AllocateTaskStateEnum convertAllocateTaskStateEnum(Integer flowGrade) {
        return AllocateTaskStateEnum.fromCode(flowGrade);
    }


    //-------------------------------------ALLOCATE-----START-----------------------------------------------
    default Integer convert(QualityTestingConclusionEnum qualityTestingConclusionEnum) {
        return qualityTestingConclusionEnum.getValue();
    }

    default QualityTestingConclusionEnum convertQualityTestingConclusionEnum(Integer qtConclusion) {
        return QualityTestingConclusionEnum.fromCode(qtConclusion);
    }

    default Integer convert(QualityTestingStateEnum qualityTestingStateEnum) {
        return qualityTestingStateEnum.getValue();
    }

    default QualityTestingStateEnum convertQualityTestingStateEnum(Integer qtState) {
        return QualityTestingStateEnum.fromCode(qtState);
    }

    default Integer convert(PricingStateEnum pricingStateEnum) {
        return pricingStateEnum.getValue();
    }

    default PricingStateEnum convertPricingStateEnum(Integer pricingState) {
        return PricingStateEnum.fromCode(pricingState);
    }

    default Integer convert(PricingNodeEnum pricingNodeEnum) {
        return pricingNodeEnum.getValue();
    }

    default PricingNodeEnum convertPricingNodeEnum(Integer pricingNode) {
        return PricingNodeEnum.fromCode(pricingNode);
    }


    default Integer convert(CustomerTypeEnum typeEnum) {
        return typeEnum.getValue();
    }

    default CustomerTypeEnum convertCustomerTypeEnum(Integer value) {
        return CustomerTypeEnum.fromCode(value);
    }

    /**
     * 采购申请单 开始
     */

    default Integer convert(TaskPaymentModeEnum taskPaymentModeEnum) {
        return taskPaymentModeEnum.getValue();
    }

    default TaskPaymentModeEnum convertTaskPaymentModeEnum(Integer value) {
        return TaskPaymentModeEnum.fromCode(value);
    }

    default Integer convert(TaskApplyModeEnum taskApplyModeEnum) {
        return taskApplyModeEnum.getValue();
    }

    default TaskApplyModeEnum convertTaskApplyModeEnum(Integer value) {
        return TaskApplyModeEnum.fromCode(value);
    }


    /**
     * 采购申请单 结束
     */
    default Integer convert(StockStatusEnum stockStatusEnum) {
        return stockStatusEnum.getValue();
    }

    default StockStatusEnum convertStockStatusEnum(Integer value) {
        return StockStatusEnum.fromCode(value);
    }

    //-------------------------------------FINANCIAL-----START-----------------------------------------------

    default Integer convert(FinancialSalesMethodEnum financialSalesMethodEnum) {
        return financialSalesMethodEnum.getValue();
    }

    default FinancialSalesMethodEnum convertFinancialSalesMethodEnum(Integer flowGrade) {
        return FinancialSalesMethodEnum.fromCode(flowGrade);
    }

    default Integer convert(FinancialStatusEnum financialStatusEnum) {
        return financialStatusEnum.getValue();
    }

    default FinancialStatusEnum convertFinancialStatusEnum(Integer flowGrade) {
        return FinancialStatusEnum.fromCode(flowGrade);
    }

    default Integer convert(OriginTypeEnum originTypeEnum) {
        return originTypeEnum.getValue();
    }

    default OriginTypeEnum convertOriginTypeEnum(Integer flowGrade) {
        return OriginTypeEnum.fromCode(flowGrade);
    }

    default Integer convert(ReceiptPaymentTypeEnum receiptPaymentTypeEnum) {
        return receiptPaymentTypeEnum.getValue();
    }

    default ReceiptPaymentTypeEnum convertReceiptPaymentTypeEnum(Integer flowGrade) {
        return ReceiptPaymentTypeEnum.fromCode(flowGrade);
    }

    default Integer convert(FinancialClassificationEnum financialClassficationEnum) {
        return financialClassficationEnum.getValue();
    }

    default FinancialClassificationEnum convertFinancialClassificationEnum(Integer flowGrade) {
        return FinancialClassificationEnum.fromCode(flowGrade);
    }

    //-------------------------------------FINANCIAL-----END-----------------------------------------------

    default Integer convert(ExpressOrderStateEnum expressOrderStateEnum) {
        return expressOrderStateEnum.getValue();
    }

    default ExpressOrderStateEnum convertExpressOrderStateEnum(Integer expressOrderState) {
        return ExpressOrderStateEnum.fromCode(expressOrderState);
    }

    default Integer convert(ExpressOrderSourceEnum expressOrderSourceEnum) {
        return expressOrderSourceEnum.getValue();
    }

    default ExpressOrderSourceEnum convertExpressOrderSourceEnum(Integer expressOrderSource) {
        return ExpressOrderSourceEnum.fromCode(expressOrderSource);
    }

    default Integer convert(StoreWorkPrintOptionEnum storesWorkPrintOptionEnum) {
        return storesWorkPrintOptionEnum.getValue();
    }

    default StoreWorkPrintOptionEnum convertStoreWorkPrintOptionEnum(Integer storesWorkPrintOption) {
        return StoreWorkPrintOptionEnum.fromCode(storesWorkPrintOption);
    }

    default Integer convert(StocktakingLineStateEnum stateEnum) {
        return stateEnum.getValue();
    }

    default StocktakingLineStateEnum convertStocktakingLineStateEnum(Integer value) {
        return StocktakingLineStateEnum.fromCode(value);
    }

    default Integer convert(StocktakingSourceEnum sourceEnum) {
        return sourceEnum.getValue();
    }

    default StocktakingSourceEnum convertStocktakingSourceEnum(Integer value) {
        return StocktakingSourceEnum.fromCode(value);
    }

    default Integer convert(StocktakingStateEnum stateEnum) {
        return stateEnum.getValue();
    }

    default StocktakingStateEnum convertStocktakingStateEnum(Integer value) {
        return StocktakingStateEnum.fromCode(value);
    }

    //-------------------------------------Stocktaking-----END-----------------------------------------------

    default Integer convert(CompanyGroupEnum stateEnum) {
        return stateEnum.getValue();
    }

    default CompanyGroupEnum convertCompanyGroupEnum(Integer value) {
        return CompanyGroupEnum.fromCode(value);
    }

    default Integer convert(CompanyTypeEnum stateEnum) {
        return stateEnum.getValue();
    }

    default CompanyTypeEnum convertCompanyTypeEnum(Integer value) {
        return CompanyTypeEnum.fromCode(value);
    }

    default Integer convert(ShopGroupEnum stateEnum) {
        return stateEnum.getValue();
    }

    default ShopGroupEnum convertShopGroupEnum(Integer value) {
        return ShopGroupEnum.fromCode(value);
    }

    default Integer convert(PageTypeEnum stateEnum) {
        return stateEnum.getValue();
    }

    default PageTypeEnum convertPageTypeEnum(Integer value) {
        return PageTypeEnum.fromCode(value);
    }

    default Integer convert(StoreWorkReturnTypeEnum stateEnum) {
        return stateEnum.getValue();
    }

    default StoreWorkReturnTypeEnum convertStoreWorkReturnTypeEnum(Integer value) {
        return StoreWorkReturnTypeEnum.fromCode(value);
    }

    default Integer convert(RecycleStateEnum stateEnum) {
        return stateEnum.getValue();
    }

    default RecycleStateEnum convertPageRecycleStateEnum(Integer value) {
        return RecycleStateEnum.fromCode(value);
    }

    default Integer convert(RecycleOrderTypeEnum stateEnum) {
        return stateEnum.getValue();
    }

    default RecycleOrderTypeEnum convertRecycleOrderTypeEnum(Integer value) {
        return RecycleOrderTypeEnum.fromCode(value);
    }

    //-------------------------------------FinancialInvoice-----START-----------------------------------------------
    default Integer convert(InvoiceOriginEnum e) {
        return e.getValue();
    }

    default InvoiceOriginEnum convertInvoiceOriginEnum(Integer value) {
        return InvoiceOriginEnum.fromCode(value);
    }

    default Integer convert(InvoiceTypeEnum e) {
        return e.getValue();
    }

    default InvoiceTypeEnum convertInvoiceTypeEnum(Integer value) {
        return InvoiceTypeEnum.fromCode(value);
    }

    default Integer convert(FinancialInvoiceStateEnum e) {
        return e.getValue();
    }

    default FinancialInvoiceStateEnum convertFinancialInvoiceStateEnum(Integer value) {
        return FinancialInvoiceStateEnum.fromCode(value);
    }

    default Integer convert(FinancialInvoiceOrderTypeEnum e) {
        return e.getValue();
    }

    default FinancialInvoiceOrderTypeEnum convertFinancialInvoiceOrderTypeEnum(Integer value) {
        return FinancialInvoiceOrderTypeEnum.fromCode(value);
    }

    //-------------------------------------FinancialInvoice-----END-----------------------------------------------

    default Integer convert(TaskStateEnum stateEnum) {
        return stateEnum.getValue();
    }

    default TaskStateEnum convertTaskStateEnum(Integer value) {
        return TaskStateEnum.fromCode(value);
    }

    //-------------------------------------Series-----START-----------------------------------------------

    default Integer convert(SeriesTypeEnum sEnum) {
        return sEnum.getValue();
    }

    default SeriesTypeEnum convertSeriesTypeEnum(Integer value) {
        return SeriesTypeEnum.fromValue(value);
    }

    //-------------------------------------Series-----END-----------------------------------------------

    default Integer convert(ScrapStockStateEnum sEnum) {
        return sEnum.getValue();
    }

    default ScrapStockStateEnum convertScrapStockStateEnum(Integer value) {
        return ScrapStockStateEnum.fromValue(value);
    }

    default Integer convert(OrderTypeEnum sEnum) {
        return sEnum.getValue();
    }

    default OrderTypeEnum convertOrderTypeEnum(Integer value) {
        return OrderTypeEnum.fromValue(value);
    }

    default Integer convert(TagTypeEnum sEnum) {
        return sEnum.getValue();
    }

    default TagTypeEnum convertTagTypeEnum(Integer value) {
        return TagTypeEnum.fromValue(value);
    }

    default Integer convert(FixSiteEnum sEnum) {
        return sEnum.getValue();
    }

    default FixSiteEnum convertFixSiteEnum(Integer value) {
        return FixSiteEnum.fromValue(value);
    }

    default Integer convert(ApplyFinancialPaymentEnum sEnum) {
        return sEnum.getValue();
    }

    default ApplyFinancialPaymentEnum convertApplyFinancialPaymentEnum(Integer value) {
        return ApplyFinancialPaymentEnum.fromCode(value);
    }

    default ApplyPricingStateEnum convertApplyPricingStateEnum(Integer value){
        return ApplyPricingStateEnum.fromCode(value);
    }

    default Integer convert(ApplyPricingStateEnum stateEnum){
        return stateEnum.getValue();
    }
}


