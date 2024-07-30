package com.seeease.flywheel.serve.financial.service;

import com.seeease.flywheel.financial.request.AuditLoggingDetailRequest;
import com.seeease.flywheel.financial.result.AuditLoggingDetailResult;
import com.seeease.flywheel.serve.financial.entity.AuditLoggingDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author edy
* @description 针对表【audit_logging_detail】的数据库操作Service
* @createDate 2023-05-10 10:14:08
*/
public interface AuditLoggingDetailService extends IService<AuditLoggingDetail> {

    List<AuditLoggingDetailResult> getOneByRequest(AuditLoggingDetailRequest request);
}
