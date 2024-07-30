package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.purchase.IPurchasePlanFacade;
import com.seeease.flywheel.purchase.request.*;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 采购计划
 *
 * @Author Mr. Du
 * @Description 总部采购列表
 * @Date create in 2023/2/14 13:49
 */
@Slf4j
@RestController
@RequestMapping("/purchasePlan")
public class PurchasePlanController {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchasePlanFacade iPurchasePlanFacade;

    /**
     * 列表查询
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse queryList(@RequestBody PurchasePlanListRequest request) {
        return SingleResponse.of(iPurchasePlanFacade.list(request));
    }

    /**
     * 导出
     * @param request
     * @return
     */
    @PostMapping("/export")
    public SingleResponse export(@RequestBody PurchasePlanListRequest request) {
        return SingleResponse.of(iPurchasePlanFacade.export(request));
    }

    /**
     * 新建
     * @param request
     * @return
     */
    @PostMapping("/create")
    public SingleResponse create(@RequestBody PurchasePlanCreateRequest request) {
        return SingleResponse.of(iPurchasePlanFacade.create(request));
    }

    /**
     * 修改采购计划
     * @param request
     * @return
     */
    @PostMapping("/update")
    public SingleResponse update(@RequestBody PurchasePlanUpdateRequest request) {
        iPurchasePlanFacade.update(request);
        return SingleResponse.buildSuccess();
    }
    /**
     * 详情
     * @param request
     * @return
     */
    @PostMapping("/details")
    public SingleResponse details(@RequestBody PurchasePlanDetailsRequest request) {
        return SingleResponse.of(iPurchasePlanFacade.details(request));
    }
}
