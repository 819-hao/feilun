package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.purchase.IPurchaseReturnFacade;
import com.seeease.flywheel.purchase.request.PurchaseReturnDetailsRequest;
import com.seeease.flywheel.purchase.request.PurchaseReturnListRequest;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/14 13:49
 */
@Slf4j
@RestController
@RequestMapping("/purchaseReturn")
public class PurchaseReturnController {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseReturnFacade iPurchaseReturnFacade;


    @PostMapping("/list")
    public SingleResponse queryList(@RequestBody PurchaseReturnListRequest request) {
        request.setStoreId(UserContext.getUser().getStore().getId());
        return SingleResponse.of(iPurchaseReturnFacade.list(request));
    }

    @PostMapping("/details")
    public SingleResponse details(@RequestBody PurchaseReturnDetailsRequest request) {
        request.setStoreId(UserContext.getUser().getStore().getId());
        return SingleResponse.of(iPurchaseReturnFacade.details(request));
    }
}
