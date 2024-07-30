package com.seeease.flywheel.serve.financial.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.financial.request.AuditLoggingQueryRequest;
import com.seeease.flywheel.financial.result.AuditLoggingPageResult;
import com.seeease.flywheel.serve.financial.entity.AuditLogging;
import com.seeease.flywheel.serve.financial.service.AuditLoggingService;
import com.seeease.flywheel.serve.financial.mapper.AuditLoggingMapper;
import org.springframework.stereotype.Service;

/**
 * @author edy
 * @description 针对表【audit_logging】的数据库操作Service实现
 * @createDate 2023-05-10 10:14:08
 */
@Service
public class AuditLoggingServiceImpl extends ServiceImpl<AuditLoggingMapper, AuditLogging>
        implements AuditLoggingService {

    @Override
    public Page<AuditLoggingPageResult> getPage(AuditLoggingQueryRequest request) {
        return this.baseMapper.getPage(new Page(request.getPage(), request.getLimit()), request);
    }
}




