package com.seeease.flywheel.serve.sale.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.sale.request.B3SaleReturnOrderListRequest;
import com.seeease.flywheel.sale.request.SaleReturnOrderExportRequest;
import com.seeease.flywheel.sale.result.B3SaleReturnOrderListResult;
import com.seeease.flywheel.sale.result.SaleReturnOrderExportResult;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLine;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLineDetailsVO;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author edy
 * @description 针对表【bill_sale_return_order_line】的数据库操作Mapper
 * @createDate 2023-03-09 20:01:50
 * @Entity com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLine
 */
public interface BillSaleReturnOrderLineMapper extends SeeeaseMapper<BillSaleReturnOrderLine> {

    List<BillSaleReturnOrderLineDetailsVO> selectBySaleReturnId(@Param("saleReturnId") Integer saleReturnId);

    List<Integer> selectStateByReturnId(@Param("saleReturnId") Integer id);

    Integer selectStateByReturnIdAndStockId(@Param("saleReturnId") Integer saleReturnId, @Param("stockId") Integer stockId);

    Page<B3SaleReturnOrderListResult> b3Page(Page<Object> page, @Param("shopIds") List<Integer> shopIds,
                                             @Param("b3ShopId") List<Integer> b3ShopId,
                                             @Param("req") B3SaleReturnOrderListRequest request);

    void updateWhetherInvoiceBySerialNoListAndStockIdList(@Param("serialNoList") List<String> serialNoList,
                                                          @Param("stockIdList") List<Integer> stockIdList,
                                                          @Param("state") Integer state);

    Page<SaleReturnOrderExportResult> exportOrderReturn(Page<Object> of,@Param("request") SaleReturnOrderExportRequest request);
}




