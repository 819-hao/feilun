package com.seeease.flywheel.serve.financial.mapper;

import com.seeease.flywheel.serve.financial.entity.FinancialInvoiceStock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author edy
* @description 针对表【financial_invoice_stock】的数据库操作Mapper
* @createDate 2023-10-19 09:52:41
* @Entity com.seeease.flywheel.serve.financial.entity.FinancialInvoiceStock
*/
public interface FinancialInvoiceStockMapper extends BaseMapper<FinancialInvoiceStock> {

    void deleteByInvoiceId(@Param("id") Integer id);
}




