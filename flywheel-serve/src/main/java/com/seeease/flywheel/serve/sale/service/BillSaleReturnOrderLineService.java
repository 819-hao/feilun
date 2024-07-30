package com.seeease.flywheel.serve.sale.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.sale.request.B3SaleReturnOrderAddRemarkRequest;
import com.seeease.flywheel.sale.request.B3SaleReturnOrderListRequest;
import com.seeease.flywheel.sale.request.SaleReturnOrderExportRequest;
import com.seeease.flywheel.sale.result.B3SaleReturnOrderListResult;
import com.seeease.flywheel.sale.result.SaleReturnOrderExportResult;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLine;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLineDetailsVO;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLineDto;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleReturnOrderLineStateEnum;

import java.util.List;

/**
 * @author edy
 * @description 针对表【bill_sale_return_order_line】的数据库操作Service
 * @createDate 2023-03-09 20:01:50
 */
public interface BillSaleReturnOrderLineService extends IService<BillSaleReturnOrderLine> {

    List<BillSaleReturnOrderLineDetailsVO> selectBySaleReturnId(Integer saleReturnId);

    void updateLineState(BillSaleReturnOrderLineDto dto, SaleReturnOrderLineStateEnum.TransitionEnum transitionEnum);

    List<Integer> selectStateByReturnId(Integer saleReturnId);

    Integer selectStateByReturnIdAndStockId(Integer saleReturnId, Integer stockId);

    /**
     * 查找3号楼标签的退货数据
     * @param shopIds
     * @param request
     * @return
     */
    Page<B3SaleReturnOrderListResult> b3Page(List<Integer> shopIds,List<Integer> b3ShopId,B3SaleReturnOrderListRequest request);

    /**
     * 3号楼添加备注
     * @param request
     */
    void b3AddRemark(B3SaleReturnOrderAddRemarkRequest request);

    List<BillSaleReturnOrderLine> saleReturnOrderLineQry(Integer saleReturnId,List<Integer> stockIdList,List<SaleOrderLineStateEnum> saleOrderLineStateEnums);

    void updateWhetherInvoiceBySerialNoListAndStockIdList(List<String> serialNoList, List<Integer> stockIdList, FinancialInvoiceStateEnum stateEnum);

    Page<SaleReturnOrderExportResult> exportOrderReturn(SaleReturnOrderExportRequest request);
}
