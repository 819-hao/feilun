package com.seeease.flywheel.serve.helper.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.helper.request.BreakPriceAuditPageRequest;
import com.seeease.flywheel.helper.request.BusinessCustomerAuditCreateRequest;
import com.seeease.flywheel.helper.request.BusinessCustomerAuditRequest;
import com.seeease.flywheel.helper.result.BreakPriceAuditPageResult;
import com.seeease.flywheel.serve.helper.convert.BusinessCustomerAuditConvert;
import com.seeease.flywheel.serve.helper.enmus.BusinessCustomerAuditStatusEnum;
import com.seeease.flywheel.serve.helper.entity.BreakPriceAudit;
import com.seeease.flywheel.serve.helper.entity.BusinessCustomerAudit;
import com.seeease.flywheel.serve.helper.mapper.AppBusinessCustomerAuditMapper;
import com.seeease.flywheel.serve.helper.mapper.BreakPriceAuditMapper;
import com.seeease.flywheel.serve.helper.service.BreakPriceAuditService;
import com.seeease.flywheel.serve.helper.service.BusinessCustomerAuditService;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import org.springframework.stereotype.Service;

@Service
public class BreakPriceAuditServiceImpl extends ServiceImpl<BreakPriceAuditMapper, BreakPriceAudit>
        implements BreakPriceAuditService {

    @Override
    public Page<BreakPriceAuditPageResult> pageOf(BreakPriceAuditPageRequest request) {
        return getBaseMapper().pageOf(Page.of(request.getPage(),request.getLimit()),request);
    }
}
