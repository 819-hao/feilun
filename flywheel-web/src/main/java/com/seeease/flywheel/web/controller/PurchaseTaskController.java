package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.purchase.IPurchaseTaskFacade;
import com.seeease.flywheel.purchase.request.PurchaseTaskEditRequest;
import com.seeease.flywheel.purchase.request.PurchaseTaskPageRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Mr. Du
 * @Description 总部采购列表
 * @Date create in 2023/2/14 13:49
 */
@Slf4j
@RestController
@RequestMapping("/purchaseTask")
public class PurchaseTaskController {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseTaskFacade purchaseTaskFacade;

    /**
     * 变更
     *
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public SingleResponse edit(@RequestBody PurchaseTaskEditRequest request) {
        return SingleResponse.of(purchaseTaskFacade.edit(request));
    }

    /**
     * 分组
     *
     * @return
     */
    @PostMapping("/mark")
    public SingleResponse mark() {
        return SingleResponse.of(purchaseTaskFacade.groupBy());
    }

    /**
     * 导出
     *
     * @param request
     * @return
     */
    @PostMapping("/export")
    public SingleResponse export(@RequestBody PurchaseTaskPageRequest request) {
        return SingleResponse.of(purchaseTaskFacade.export(request));
    }
}
