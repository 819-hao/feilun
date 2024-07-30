package com.seeease.flywheel.account;

import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.account.request.AccountCreateRequest;
import com.seeease.flywheel.account.request.AccountDeleteRequest;
import com.seeease.flywheel.account.request.AccountImportRequest;
import com.seeease.flywheel.account.request.AccountQueryRequest;
import com.seeease.flywheel.account.result.AccountCreateResult;
import com.seeease.flywheel.account.result.AccountQueryImportResult;
import com.seeease.flywheel.account.result.AccountQueryResult;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 15:59
 */

public interface IAccountFacade {

    /**
     * 批量创建
     *
     * @param request
     * @return
     */
    List<AccountCreateResult> batchCreate(List<AccountCreateRequest> request);

    /**
     * 数据列表
     *
     * @param request
     * @return
     */
    PageResult<AccountQueryResult> list(AccountQueryRequest request);

    /**
     * 导入查询
     *
     * @param request
     * @return
     */
    ImportResult<AccountQueryImportResult> queryImport(AccountImportRequest request);

    /**
     * 伪删除 金蝶数据导入删除
     *
     * @param request
     */
    void delete(AccountImportRequest request);

    /**
     * 伪删除 飞轮数据删除
     * @param request
     */
    void delete(AccountDeleteRequest request);
}
