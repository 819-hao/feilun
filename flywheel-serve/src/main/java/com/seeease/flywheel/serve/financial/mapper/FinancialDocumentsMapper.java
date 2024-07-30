package com.seeease.flywheel.serve.financial.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.FinancialQueryAllRequest;
import com.seeease.flywheel.financial.result.FinancialExportResult;
import com.seeease.flywheel.financial.result.FinancialPageAllResult;
import com.seeease.flywheel.serve.financial.entity.FinancialDocuments;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author edy
 * @description 针对表【financial_documents(财务单据)】的数据库操作Mapper
 * @createDate 2023-03-27 09:52:56
 * @Entity com.seeease.flywheel.serve.financial.entity.FinancialDocuments
 */
public interface FinancialDocumentsMapper extends BaseMapper<FinancialDocuments> {

    Page<FinancialPageAllResult> selectByFinancialQueryAllRequest(Page page, @Param("request") FinancialQueryAllRequest request);

    List<FinancialExportResult> selectExcelByFinancialDocumentsQueryDto(@Param("request") FinancialQueryAllRequest request);
}




