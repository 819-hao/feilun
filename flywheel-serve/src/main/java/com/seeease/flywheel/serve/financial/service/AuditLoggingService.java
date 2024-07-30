package com.seeease.flywheel.serve.financial.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.AuditLoggingQueryRequest;
import com.seeease.flywheel.financial.result.AuditLoggingPageResult;
import com.seeease.flywheel.serve.financial.entity.AuditLogging;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author edy
* @description 针对表【audit_logging】的数据库操作Service
* @createDate 2023-05-10 10:14:08
*/
public interface AuditLoggingService extends IService<AuditLogging> {

    Page<AuditLoggingPageResult> getPage(AuditLoggingQueryRequest request);
}
