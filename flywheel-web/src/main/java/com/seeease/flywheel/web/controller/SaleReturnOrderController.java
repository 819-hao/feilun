package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.sale.ISaleReturnOrderFacade;
import com.seeease.flywheel.sale.request.*;
import com.seeease.flywheel.web.infrastructure.external.firework.WorkflowService;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/saleReturn")
public class SaleReturnOrderController {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleReturnOrderFacade facade;
    @Resource
    private WorkflowService workflowService;

    @PostMapping("/queryAll")
    public SingleResponse queryAll(@RequestBody SaleReturnOrderListRequest request) {

        return SingleResponse.of(facade.list(request));
    }

    @PostMapping("/details")
    public SingleResponse details(@RequestBody SaleReturnOrderDetailsRequest request) {

        return SingleResponse.of(facade.details(request));
    }

    /**
     * 3号楼退货添加备注
     *
     * @param request
     * @return
     */
    @PostMapping("b3/remark")
    public SingleResponse b3AddRemark(@RequestBody B3SaleReturnOrderAddRemarkRequest request) {
        Assert.notEmpty(request.getIds(), "备注不能为空");
        facade.b3AddRemark(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 申请退款
     *
     * @return
     */
    @PostMapping("/refund")
    public SingleResponse refundApply(@RequestBody SaleReturnOrderRefundRequest request) {

        facade.refund(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 错单退货
     *
     * @return
     */
    @PostMapping("/billErrRefund")
    public SingleResponse billErrRefund(@RequestBody SaleReturnOrderBillErrRefundRequest request) {

        //个人销售退货单id
        facade.billErrRefund(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 导出退货单
     * @param request
     * @return
     */
    @PostMapping("/exportOrderReturn")
    public SingleResponse exportOrderReturn(@RequestBody SaleReturnOrderExportRequest request) {
        return SingleResponse.of(facade.exportOrderReturn(request));
    }

}
