package com.seeease.flywheel.serve.sale.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.sale.request.SaleOrderBatchSettlementRequest;
import com.seeease.flywheel.sale.request.SaleOrderSettlementListRequest;
import com.seeease.flywheel.sale.result.SaleOrderBatchSettlementResult;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;
import com.seeease.flywheel.serve.sale.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;

import java.util.List;

/**
 * @author edy
 * @description 针对表【bill_sale_line】的数据库操作Service
 * @createDate 2023-03-06 10:38:19
 */
public interface BillSaleOrderLineService extends IService<BillSaleOrderLine> {

    List<BillSaleOrderLineDetailsVO> selectBySaleId(Integer id);

    Page<BillSaleOrderLineSettlementVO> querySettlementList(SaleOrderSettlementListRequest request);

    SaleOrderBatchSettlementResult batchSettlement(SaleOrderBatchSettlementRequest request);

    void updateLineState(BillSaleOrderLineDto lineDtoBuilder, SaleOrderLineStateEnum.TransitionEnum qualityTestingToCancelWhole);

    List<BillSaleOrderLineSettlementVO> listStockBySnAndState(List<String> snList, List<Integer> stateList, Integer customerId);

    int countStateBySaleId(Integer id);

    void updateWarrantyPeriod(Integer id);

    List<BillSaleOrderLine> selectBySaleIds(List<Integer> saleIds);

    void updateWhetherInvoiceBySerialNoListAndStockIdList(List<String> serialNoList, List<Integer> stockIdList, FinancialInvoiceStateEnum stateEnum);

    void updateWhetherInvoiceById(Integer lineId, FinancialInvoiceStateEnum inInvoice);

    void updateState(BillSaleOrderLine billSaleOrderLine);
}
