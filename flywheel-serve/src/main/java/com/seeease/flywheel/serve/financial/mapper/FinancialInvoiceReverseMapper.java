package com.seeease.flywheel.serve.financial.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.FinancialInvoiceReverseQueryByConditionRequest;
import com.seeease.flywheel.financial.result.FinancialInvoiceReverseQueryByConditionResult;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoiceReverse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author edy
* @description 针对表【financial_invoice_reverse】的数据库操作Mapper
* @createDate 2023-11-30 09:49:04
* @Entity com.seeease.flywheel.serve.financial.entity.FinancialInvoiceReverse
*/
public interface FinancialInvoiceReverseMapper extends BaseMapper<FinancialInvoiceReverse> {

    Page<FinancialInvoiceReverseQueryByConditionResult> queryByCondition(Page page,@Param("request") FinancialInvoiceReverseQueryByConditionRequest request);
}




