package com.seeease.flywheel.web.controller;

import com.seeease.springframework.SingleResponse;
import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.WuyuPricingPageRequest;
import com.seeease.flywheel.pricing.request.WuyuPricingRequest;
import com.seeease.flywheel.wx.IWxFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description 微信鉴权api
 * @Date create in 2023/5/12 18:31
 */
@RestController
@RequestMapping("wuyu")
@Slf4j
public class WuyuController {
    @DubboReference(check = false, version = "1.0.0")
    private IPricingFacade pricingFacade;


    @PostMapping("pricing")
    public SingleResponse wuyuPricing(@RequestBody WuyuPricingRequest request){
        pricingFacade.wuyuPricing(request.getList());
        return SingleResponse.of(true);
    }
    @PostMapping("pricing/page")
    public SingleResponse wuyuPricingPage(@RequestBody WuyuPricingPageRequest request){
        return SingleResponse.of(pricingFacade.page(request));
    }
}
