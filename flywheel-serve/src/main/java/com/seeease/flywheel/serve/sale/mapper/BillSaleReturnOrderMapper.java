package com.seeease.flywheel.serve.sale.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.sale.request.SaleReturnOrderListRequest;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrder;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author edy
 * @description 针对表【bill_sale_return_order】的数据库操作Mapper
 * @createDate 2023-03-09 20:01:50
 * @Entity com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrder
 */
public interface BillSaleReturnOrderMapper extends SeeeaseMapper<BillSaleReturnOrder> {

    Page<BillSaleReturnOrder> listByRequest(Page<Object> page, @Param("request") SaleReturnOrderListRequest request);

    int selectStateById(@Param("id") Integer id);

    Integer selectDouYinOrderBySerialNo(@Param("serialNo") String serialNo);
}




