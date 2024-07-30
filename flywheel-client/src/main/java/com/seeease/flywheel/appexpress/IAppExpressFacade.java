package com.seeease.flywheel.appexpress;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.appexpress.request.AppExpressSubmitRequest;
import com.seeease.flywheel.appexpress.result.AppExpressPageResult;
import com.seeease.flywheel.pricing.request.ApplyPricingAuditorRequest;
import com.seeease.flywheel.pricing.request.ApplyPricingCreateRequest;
import com.seeease.flywheel.pricing.request.ApplyPricingEditRequest;
import com.seeease.flywheel.pricing.request.ApplyPricingListRequest;
import com.seeease.flywheel.pricing.result.ApplyPricingAuditorResult;
import com.seeease.flywheel.pricing.result.ApplyPricingCreateResult;
import com.seeease.flywheel.pricing.result.ApplyPricingEditResult;
import com.seeease.flywheel.pricing.result.ApplyPricingListResult;
import com.seeease.springframework.SingleResponse;

/**
 *
 *
 * @author Tiro
 * @date 2024/2/23
 */
public interface IAppExpressFacade {


    PageResult<AppExpressPageResult> queryPage(String pageNum, String pageSize, String code);

    void submit(AppExpressSubmitRequest request);
}
