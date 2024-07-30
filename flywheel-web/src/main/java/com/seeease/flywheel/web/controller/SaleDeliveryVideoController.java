package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.sale.ISaleDeliveryVideoFacade;
import com.seeease.flywheel.sale.request.SaleDeliveryVideoRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tiro
 * @date 2023/9/14
 */
@Slf4j
@RestController
@RequestMapping("/saleDeliveryVideo")
public class SaleDeliveryVideoController {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleDeliveryVideoFacade saleDeliveryVideoFacade;

    /**
     * 存储
     *
     * @param request
     * @return
     */
    @PostMapping("/save")
    public SingleResponse save(@RequestBody SaleDeliveryVideoRequest request) {
        return SingleResponse.of(saleDeliveryVideoFacade.save(request));
    }
}
