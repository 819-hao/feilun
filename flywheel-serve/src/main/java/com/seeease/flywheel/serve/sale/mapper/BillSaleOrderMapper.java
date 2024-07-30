package com.seeease.flywheel.serve.sale.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.sale.request.DouYinSaleOrderListRequest;
import com.seeease.flywheel.sale.request.SaleOrderAccuracyQueryRequest;
import com.seeease.flywheel.sale.request.SaleOrderListRequest;
import com.seeease.flywheel.sale.result.DouYinSaleOrderListResult;
import com.seeease.flywheel.sale.result.SaleOrderListForExportResult;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author edy
 * @description 针对表【bill_sale】的数据库操作Mapper
 * @createDate 2023-03-06 10:38:19
 * @Entity com.seeease.flywheel.serve.sale.entity.BillSale
 */
public interface BillSaleOrderMapper extends SeeeaseMapper<BillSaleOrder> {

    Page<BillSaleOrder> listByRequest(Page<Object> page, @Param("request") SaleOrderListRequest request);

    int queryByIdAndCustomerId(@Param("saleId") Integer saleId, @Param("customerId") Integer customerId);

    BillSaleOrder selectBySaleLineId(@Param("saleLineId") Integer saleLineId);

    int selectStateById(@Param("saleId") Integer saleId);

    Page<SaleOrderListForExportResult> export(Page<Object> page, @Param("request") SaleOrderListRequest request);


    List<BillSaleOrder> queryToCOrderByOffset(@Param("currentOffset") Integer currentOffset, @Param("limit") Integer limit);

    List<BillSaleOrder> queryToCOrderByFinisTime(@Param("saleTime") Date saleTime);

    List<BillSaleOrder> queryToCOrderByRequest(SaleOrderAccuracyQueryRequest request);

    Integer maxToCOrderByOffset(@Param("currentOffset") Integer currentOffset);

    void updateDouYinOrder(@Param("ids") List<Integer> ids, @Param("whetherUse") Integer i, @Param("usageTime") Date d, @Param("serialNo") String serialNo);

    void updateKuaiShouOrder(@Param("ids") List<Integer> ids, @Param("whetherUse") Integer i, @Param("usageTime") Date d, @Param("serialNo") String serialNo);

    List<DouYinSaleOrderListResult> queryDouYinSaleOrder(DouYinSaleOrderListRequest request);

    Integer selectDouYinOrderBySerialNo(@Param("serialNo") String serialNo);
}




