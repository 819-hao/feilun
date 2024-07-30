package com.seeease.flywheel.serve.financial.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.FinancialStatementDetailsRequest;
import com.seeease.flywheel.financial.request.FinancialStatementMiniPageQueryRequest;
import com.seeease.flywheel.financial.request.FinancialStatementQueryAllRequest;
import com.seeease.flywheel.financial.result.FinancialStatementDetailsResult;
import com.seeease.flywheel.financial.result.FinancialStatementMiniPageQueryResult;
import com.seeease.flywheel.financial.result.FinancialStatementQueryAllResult;
import com.seeease.flywheel.serve.financial.entity.FinancialStatement;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author edy
* @description 针对表【financial_statement(财务流水记录)】的数据库操作Service
* @createDate 2023-09-01 10:16:08
*/
public interface FinancialStatementService extends IService<FinancialStatement> {

    Page<FinancialStatementQueryAllResult> queryAll(FinancialStatementQueryAllRequest request);

    void batchAudit(List<Integer> ids, String auditDescription, String userName);

    FinancialStatementDetailsResult detail(FinancialStatementDetailsRequest request);

    Page<FinancialStatementMiniPageQueryResult> miniPageQuery(FinancialStatementMiniPageQueryRequest request);
}
