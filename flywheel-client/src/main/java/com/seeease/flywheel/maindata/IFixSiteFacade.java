package com.seeease.flywheel.maindata;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.fix.request.FixSiteCreateRequest;
import com.seeease.flywheel.fix.request.FixSiteDetailsRequest;
import com.seeease.flywheel.fix.request.FixSiteEditRequest;
import com.seeease.flywheel.fix.request.FixSiteListRequest;
import com.seeease.flywheel.fix.result.FixSiteCreateResult;
import com.seeease.flywheel.fix.result.FixSiteDetailsResult;
import com.seeease.flywheel.fix.result.FixSiteEditResult;
import com.seeease.flywheel.fix.result.FixSiteListResult;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/11/18 10:15
 */

public interface IFixSiteFacade {

    /**
     * 创建
     *
     * @param request
     * @return
     */
    FixSiteCreateResult create(FixSiteCreateRequest request);

    /**
     * 修改
     *
     * @param request
     * @return
     */
    FixSiteEditResult edit(FixSiteEditRequest request);

    /**
     * 详情
     *
     * @param request
     * @return
     */
    FixSiteDetailsResult details(FixSiteDetailsRequest request);

    /**
     * 列表
     *
     * @param request
     * @return
     */
    PageResult<FixSiteListResult> list(FixSiteListRequest request);
}
