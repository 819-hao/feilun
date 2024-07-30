package com.seeease.flywheel.pricing;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.pricing.request.ApplyPricingAuditorRequest;
import com.seeease.flywheel.pricing.request.ApplyPricingCreateRequest;
import com.seeease.flywheel.pricing.request.ApplyPricingEditRequest;
import com.seeease.flywheel.pricing.request.ApplyPricingListRequest;
import com.seeease.flywheel.pricing.result.ApplyPricingAuditorResult;
import com.seeease.flywheel.pricing.result.ApplyPricingCreateResult;
import com.seeease.flywheel.pricing.result.ApplyPricingEditResult;
import com.seeease.flywheel.pricing.result.ApplyPricingListResult;

/**
 * 调价申请服务
 *
 * @author Tiro
 * @date 2024/2/23
 */
public interface IApplyPricingFacade {

    /**
     * 调价申请列表
     *
     * @param request
     * @return
     */
    PageResult<ApplyPricingListResult> list(ApplyPricingListRequest request);

    /**
     * 调价申请创建
     *
     * @param request
     * @return
     */
    ApplyPricingCreateResult create(ApplyPricingCreateRequest request);

    /**
     * 编辑
     *
     * @param request
     * @return
     */
    ApplyPricingEditResult edit(ApplyPricingEditRequest request);

    /**
     * 调价申请审核
     *
     * @param request
     * @return
     */
    ApplyPricingAuditorResult auditor(ApplyPricingAuditorRequest request);


}
