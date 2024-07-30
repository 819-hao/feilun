package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.pricing.IApplyPricingFacade;
import com.seeease.flywheel.pricing.request.ApplyPricingAuditorRequest;
import com.seeease.flywheel.pricing.request.ApplyPricingCreateRequest;
import com.seeease.flywheel.pricing.request.ApplyPricingEditRequest;
import com.seeease.flywheel.pricing.request.ApplyPricingListRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tiro
 * @date 2024/2/23
 */
@Slf4j
@RestController
@RequestMapping("/applyPricing")
public class ApplyPricingController {

    @DubboReference(check = false, version = "1.0.0")
    private IApplyPricingFacade applyPricingFacade;

    /**
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse list(@RequestBody ApplyPricingListRequest request) {
        return SingleResponse.of(applyPricingFacade.list(request));
    }

    /**
     * @param request
     * @return
     */
    @PostMapping("/create")
    public SingleResponse create(@RequestBody ApplyPricingCreateRequest request) {
        return SingleResponse.of(applyPricingFacade.create(request));
    }

    /**
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public SingleResponse edit(@RequestBody ApplyPricingEditRequest request) {
        return SingleResponse.of(applyPricingFacade.edit(request));

    }

    /**
     * @param request
     * @return
     */
    @PostMapping("/auditor")
    public SingleResponse auditor(@RequestBody ApplyPricingAuditorRequest request) {
        return SingleResponse.of(applyPricingFacade.auditor(request));
    }
}
