package com.seeease.flywheel.serve.financial.template;


import com.seeease.flywheel.serve.financial.entity.*;
import com.seeease.springframework.Tuple2;


import java.util.LinkedList;
import java.util.List;

/**
 * @author tiro
 * @date 2022/9/22
 */
public interface FinancialTemplate {

    /**
     * 销售调拨财务单(含掉入调出)
     * 1、物鱼对门店的【寄售调出单】
     * 2、门店对物鱼的【寄售调入单】
     */
    LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generateSaleAllocation(FinancialSalesDto dto);

    /**
     * 销售财务单(售出)
     * 1、门店对客户的【个人销售单】/ 销售方对客户的销售单
     */
    LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generateSale(FinancialSalesDto dto);

    /**
     * 销售服务费财务单(含收入支出)
     * 1、门店B对门店A的服务费支出单 / 销售方对门店A的服务费支出单
     * 2、门店A对门店B的服务费收入单 / 门店A对销售方的服务费收入单
     */
    LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generateSaleServiceFee(FinancialSalesDto dto);

    /**
     * 销售退货调拨财务单(含掉入调出)
     * 1、物鱼对门店的退货调入单
     * 2、门店对物鱼的退货调出单
     */
    LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generateSaleReturnAllocation(FinancialSalesReturnDto dto);

    /**
     * 销售退货财务单(售出)
     * 1、门店对客户的销售退货单 / 销售方对客户的销售退货单
     */
    LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generateSaleReturn(FinancialSalesReturnDto dto);

    /**
     * 销售退货服务费财务单(含收入支出)
     * 1、销售方对门店A的退回服务费支入单 / 门店B对门店A的退回服务费支入单
     * 2、门店A对销售方的退回服务费收出单 / 门店A对门店B的退回服务费收出单
     */
    LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generateSaleReturnServiceFee(FinancialSalesReturnDto dto);

    /**
     * 采购财务单据
     *
     * @param dto
     * @return
     */
    LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generatePurchase(FinancialPurchaseDto dto);

    /**
     * 采购财务单(含掉入调出)
     * 1、采购主体对物鱼的【寄售调出单】
     * 2、物鱼对采购主体的【寄售调入单】
     */
    LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generatePurchaseAllocation(FinancialSalesDto dto);

    /**
     * 采购财务单(含掉入调出)
     * 1、采购主体对物鱼的退货调入单
     * 2、物鱼对采购主体的退货调出单
     */
    LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generatePurchaseReturnAllocation(FinancialSalesReturnDto dto);

    /**
     * 采购退货财务单据
     *
     * @param dto
     * @return
     */
    LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generatePurchaseReturn(FinancialPurchaseReturnDto dto);

    /**
     * 采购 回购回收服务费
     *
     * @param dto
     * @return
     */
    LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generatePurchaseServiceFee(FinancialPurchaseDto dto);

    LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generatePurchaseMarginCover(FinancialPurchaseDto dto);
}
