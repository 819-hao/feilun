package com.seeease.flywheel.financial;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.request.AccountsPayableAccountingBatchAuditRequest;
import com.seeease.flywheel.financial.request.AccountsPayableAccountingCreateAfpRequest;
import com.seeease.flywheel.financial.request.AccountsPayableAccountingQueryRequest;
import com.seeease.flywheel.financial.result.AccountsPayableAccountingPageResult;

/**
 * @author wbh
 * @date 2023/5/10
 */
public interface IAccountsPayableAccountingFacade {
    /**
     * 查询应收应付列表
     * @param request
     * @return
     */
    PageResult<AccountsPayableAccountingPageResult> query(AccountsPayableAccountingQueryRequest request);

    /**
     * 批量审核
     * @param request
     */
    void batchAudit(AccountsPayableAccountingBatchAuditRequest request);

    /**
     * 导出
     * @param request
     * @return
     */
    PageResult<AccountsPayableAccountingPageResult> export(AccountsPayableAccountingQueryRequest request);

    /**
     * 创建申请打款单
     * @param request
     */
    void createAfp(AccountsPayableAccountingCreateAfpRequest request);
}
