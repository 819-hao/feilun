package com.seeease.flywheel.serve.financial.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.FinancialInvoiceQueryByConditionRequest;
import com.seeease.flywheel.financial.request.FinancialInvoiceQueryRequest;
import com.seeease.flywheel.financial.result.FinancialInvoicePageResult;
import com.seeease.flywheel.financial.result.FinancialInvoiceQueryByConditionResult;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author edy
 * @description 针对表【financial_invoice】的数据库操作Mapper
 * @createDate 2023-10-19 09:35:12
 * @Entity com.seeease.flywheel.serve.financial.entity.FinancialInvoice
 */
public interface FinancialInvoiceMapper extends BaseMapper<FinancialInvoice> {

    Page<FinancialInvoicePageResult> queryPage(Page page, @Param("request") FinancialInvoiceQueryRequest request);

    Page<FinancialInvoiceQueryByConditionResult> queryByCondition(Page page, @Param("request") FinancialInvoiceQueryByConditionRequest request);

    String queryInvoiceNumberBySerialNo(@Param("serialNo") String originalInvoiceSerialNo);
}




