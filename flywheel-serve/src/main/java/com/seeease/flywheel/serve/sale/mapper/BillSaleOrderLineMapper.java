package com.seeease.flywheel.serve.sale.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.sale.request.SaleOrderSettlementListRequest;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLineDetailsVO;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLineSettlementVO;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author edy
 * @description 针对表【bill_sale_line】的数据库操作Mapper
 * @createDate 2023-03-06 10:38:19
 * @Entity com.seeease.flywheel.serve.sale.entity.BillSaleLine
 */
public interface BillSaleOrderLineMapper extends SeeeaseMapper<BillSaleOrderLine> {

    List<BillSaleOrderLineDetailsVO> selectBySaleId(@Param("saleId") Integer id);

    List<Integer> selectStateBySaleId(@Param("saleId") Integer saleId);

    Page<BillSaleOrderLineSettlementVO> querySettlementList(Page<Object> page, @Param("request") SaleOrderSettlementListRequest request);

    BigDecimal selectLastClinchPriceByStockId(@Param("stockId") Integer stockId);

    List<BillSaleOrderLineSettlementVO> listStockBySnAndState(@Param("snList") List<String> snList, @Param("stateList") List<Integer> stateList, @Param("customerId") Integer customerId);

    int countStateBySaleId(@Param("saleId") Integer id);

    void updateWarrantyPeriod(@Param("id") Integer id);

    List<BillSaleOrderLine> listByStockIds(@Param("stockIds") List<Integer> stockIds);

    Integer selectSaleModeById(@Param("saleLineId") Integer saleLineId);

    void updateWhetherInvoiceBySerialNoListAndStockIdList(@Param("serialNoList") List<String> serialNoList,
                                                          @Param("stockIdList") List<Integer> stockIdList,
                                                          @Param("state") Integer state);

    void updateWhetherInvoiceById(@Param("id") Integer id, @Param("stateEnum") Integer stateEnum);
}




