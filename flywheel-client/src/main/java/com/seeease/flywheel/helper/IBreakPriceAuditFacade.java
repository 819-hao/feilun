package com.seeease.flywheel.helper;


import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.helper.request.BreakPriceAuditPageRequest;
import com.seeease.flywheel.helper.request.BreakPriceAuditSubmitRequest;
import com.seeease.flywheel.helper.request.BreakPriceAuditRequest;
import com.seeease.flywheel.helper.result.BreakPriceAuditHistoryResult;
import com.seeease.flywheel.helper.result.BreakPriceAuditPageResult;

import java.util.List;

/**
 * 破c申请
 */
public interface IBreakPriceAuditFacade {

    Integer submit (BreakPriceAuditSubmitRequest request);

    void audit(BreakPriceAuditRequest request);

    PageResult<BreakPriceAuditPageResult> pageOf(BreakPriceAuditPageRequest request);

    List<BreakPriceAuditHistoryResult> history(Integer auditId);

}
