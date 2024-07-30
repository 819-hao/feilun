package com.seeease.flywheel.serve.financial.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.FinancialStatementDetailsRequest;
import com.seeease.flywheel.financial.request.FinancialStatementMiniPageQueryRequest;
import com.seeease.flywheel.financial.request.FinancialStatementQueryAllRequest;
import com.seeease.flywheel.financial.result.FinancialStatementDetailsResult;
import com.seeease.flywheel.financial.result.FinancialStatementMiniPageQueryResult;
import com.seeease.flywheel.financial.result.FinancialStatementQueryAllResult;
import com.seeease.flywheel.serve.financial.entity.FinancialStatement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;


/**
 * @author edy
 * @description 针对表【financial_statement(财务流水记录)】的数据库操作Mapper
 * @createDate 2023-09-01 10:16:08
 * @Entity com.seeease.flywheel.serve.financial.entity.FinancialStatement
 */
public interface FinancialStatementMapper extends BaseMapper<FinancialStatement> {

    //前端没人做 只能这样改
    Page<FinancialStatementQueryAllResult> getPage(Page page, @Param("request") FinancialStatementQueryAllRequest request);

    FinancialStatementDetailsResult detail(@Param("request") FinancialStatementDetailsRequest request);

    Page<FinancialStatementMiniPageQueryResult> miniPageQuery(Page page, @Param("request") FinancialStatementMiniPageQueryRequest request);
}




