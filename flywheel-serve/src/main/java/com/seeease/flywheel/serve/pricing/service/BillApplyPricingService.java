package com.seeease.flywheel.serve.pricing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.pricing.request.ApplyPricingAuditorRequest;
import com.seeease.flywheel.pricing.result.ApplyPricingAuditorResult;
import com.seeease.flywheel.serve.pricing.entity.BillApplyPricing;

/**
 * @author Tiro
 * @description 针对表【bill_apply_pricing(调价申请)】的数据库操作Service
 * @createDate 2024-02-22 18:28:11
 */
public interface BillApplyPricingService extends IService<BillApplyPricing> {

    ApplyPricingAuditorResult auditor(ApplyPricingAuditorRequest request);
}
