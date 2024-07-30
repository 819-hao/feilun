package com.seeease.flywheel.financial;

import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.*;

import java.util.List;

/**
 * @author wbh
 * @date 2023/2/27
 */
public interface IFinancialStatementFacade {

    /**
     * 查询全量
     *
     * @param request
     * @return
     */
    PageResult<FinancialStatementQueryAllResult> queryAll(FinancialStatementQueryAllRequest request);

    /**
     * 详情
     *
     * @param request
     * @return
     */
    FinancialStatementDetailsResult detail(FinancialStatementDetailsRequest request);

    /**
     * 导出
     *
     * @param request
     * @return
     */
    PageResult<FinancialStatementQueryAllResult> export(FinancialStatementQueryAllRequest request);

    /**
     * 导入
     *
     * @param request
     * @return
     */
    ImportResult<FinancialStatementImportResult> financialStatementImport(FinancialStatementImportRequest request);

    /**
     * 批量审核
     *
     * @param request
     */
    void batchAudit(FinancialStatementBatchAuditRequest request);

    /**
     * 企业微信---新建确认收款---搜索流水
     *
     * @param request
     * @return
     */
    PageResult<FinancialStatementMiniPageQueryResult> miniPageQuery(FinancialStatementMiniPageQueryRequest request);

    /**
     * 搜索未核销的流水
     *
     * @param request
     * @return
     */
    PageResult<FinancialStatementQueryAllResult> allNotAudit(FinancialStatementQueryAllRequest request);

    /**
     * 查询打款主体
     *
     * @param request
     * @return
     */
    PageResult<PurchaseSubjectNameResult> subjectCompanyQry(FinancialStatementSubjectNameQueryRequest request);

    List<FinancialStatementCompanyQueryResult> queryAllSubjectName();

    void create(FinancialStatementCreateRequest request);

    void matchingWriteOff(FinancialStatementMatchingWriteOffRequest request);

    /**
     * 汇付款流水
     *
     * @param request
     */
    Boolean saveHfTradeFlow(HfTradeFlowSaveRequest request);
}
