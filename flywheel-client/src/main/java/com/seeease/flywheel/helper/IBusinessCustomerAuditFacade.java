package com.seeease.flywheel.helper;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.helper.request.BusinessCustomerAuditRequest;
import com.seeease.flywheel.helper.request.BusinessCustomerAuditCreateRequest;
import com.seeease.flywheel.helper.request.BusinessCustomerListRequest;
import com.seeease.flywheel.helper.result.BusinessCustomerPageResult;

public interface IBusinessCustomerAuditFacade {
    /**
     * 创建
     * @param request
     */
    Integer submit(BusinessCustomerAuditCreateRequest request);

    /**
     * 分页查询
     * @param status
     * @param page
     * @param limit
     * @return
     */
    PageResult<BusinessCustomerPageResult> page(BusinessCustomerListRequest request);

    /**
     * 审核
     * @param request
     */
    void audit(BusinessCustomerAuditRequest request);
}
