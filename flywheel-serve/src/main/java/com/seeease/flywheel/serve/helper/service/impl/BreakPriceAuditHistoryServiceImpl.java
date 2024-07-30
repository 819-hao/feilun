package com.seeease.flywheel.serve.helper.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.helper.request.BreakPriceAuditPageRequest;
import com.seeease.flywheel.helper.result.BreakPriceAuditPageResult;
import com.seeease.flywheel.serve.helper.entity.BreakPriceAudit;
import com.seeease.flywheel.serve.helper.entity.BreakPriceAuditHistory;
import com.seeease.flywheel.serve.helper.mapper.BreakPriceAuditHistoryMapper;
import com.seeease.flywheel.serve.helper.mapper.BreakPriceAuditMapper;
import com.seeease.flywheel.serve.helper.service.BreakPriceAuditHistoryService;
import com.seeease.flywheel.serve.helper.service.BreakPriceAuditService;
import org.springframework.stereotype.Service;

@Service
public class BreakPriceAuditHistoryServiceImpl extends ServiceImpl<BreakPriceAuditHistoryMapper, BreakPriceAuditHistory>
        implements BreakPriceAuditHistoryService {

}
