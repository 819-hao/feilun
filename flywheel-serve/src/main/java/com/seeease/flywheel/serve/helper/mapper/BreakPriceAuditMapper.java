package com.seeease.flywheel.serve.helper.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.helper.request.BreakPriceAuditPageRequest;
import com.seeease.flywheel.helper.result.BreakPriceAuditPageResult;
import com.seeease.flywheel.serve.helper.entity.BreakPriceAudit;
import com.seeease.flywheel.serve.helper.entity.BusinessCustomerAudit;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

public interface BreakPriceAuditMapper extends SeeeaseMapper<BreakPriceAudit> {
    Page<BreakPriceAuditPageResult> pageOf(Page<Object> page,@Param("req") BreakPriceAuditPageRequest request);
}
