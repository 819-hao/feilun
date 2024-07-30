package com.seeease.flywheel.serve.financial.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.financial.request.AuditLoggingDetailRequest;
import com.seeease.flywheel.financial.result.AuditLoggingDetailResult;
import com.seeease.flywheel.serve.financial.entity.AuditLoggingDetail;
import com.seeease.flywheel.serve.financial.service.AuditLoggingDetailService;
import com.seeease.flywheel.serve.financial.mapper.AuditLoggingDetailMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author edy
* @description 针对表【audit_logging_detail】的数据库操作Service实现
* @createDate 2023-05-10 10:14:08
*/
@Service
public class AuditLoggingDetailServiceImpl extends ServiceImpl<AuditLoggingDetailMapper, AuditLoggingDetail>
    implements AuditLoggingDetailService{

    @Override
    public List<AuditLoggingDetailResult> getOneByRequest(AuditLoggingDetailRequest request) {
        return this.baseMapper.getOneByRequest(request);
    }
}




