package com.seeease.flywheel.serve.helper.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.seeease.flywheel.helper.request.BusinessCustomerAuditCreateRequest;
import com.seeease.flywheel.helper.request.BusinessCustomerAuditRequest;
import com.seeease.flywheel.helper.request.BusinessCustomerListRequest;
import com.seeease.flywheel.helper.result.BusinessCustomerPageResult;
import com.seeease.flywheel.serve.helper.entity.BusinessCustomerAudit;
import org.apache.poi.ss.formula.functions.T;

public interface BusinessCustomerAuditService extends IService<BusinessCustomerAudit> {
    Integer submit(BusinessCustomerAuditCreateRequest request);

    void audit(BusinessCustomerAuditRequest request);


    Page<BusinessCustomerPageResult> pageOf(BusinessCustomerListRequest request,
                                            boolean admin,
                                            Integer userid);
}
