package com.seeease.flywheel.serve.financial.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.FinancialInvoiceRecordRequest;
import com.seeease.flywheel.financial.result.FinancialInvoiceRecordPageResult;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoiceRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author edy
 * @description 针对表【financial_invoice_record】的数据库操作Mapper
 * @createDate 2023-10-19 09:35:12
 * @Entity com.seeease.flywheel.serve.financial.entity.FinancialInvoiceRecord
 */
public interface FinancialInvoiceRecordMapper extends BaseMapper<FinancialInvoiceRecord> {

    Page<FinancialInvoiceRecordPageResult> getPage(Page page, @Param("request") FinancialInvoiceRecordRequest request);
}




