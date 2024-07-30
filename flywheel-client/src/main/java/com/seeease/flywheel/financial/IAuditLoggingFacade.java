package com.seeease.flywheel.financial;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.request.AuditLoggingDetailRequest;
import com.seeease.flywheel.financial.request.AuditLoggingQueryRequest;
import com.seeease.flywheel.financial.result.AuditLoggingDetailResult;
import com.seeease.flywheel.financial.result.AuditLoggingPageResult;

import java.util.List;

/**
 * @author wbh
 * @date 2023/5/10
 */
public interface IAuditLoggingFacade {

    /**
     * 审核记录列表
     * @param request
     * @return
     */
    PageResult<AuditLoggingPageResult> query(AuditLoggingQueryRequest request);

    /**
     * 审核详情
     * @param request
     * @return
     */
    List<AuditLoggingDetailResult> detail(AuditLoggingDetailRequest request);
}
