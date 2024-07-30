package com.seeease.flywheel.web.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.seeease.flywheel.purchase.IPurchaseDemandFacade;
import com.seeease.flywheel.purchase.IPurchaseFacade;
import com.seeease.flywheel.purchase.IPurchaseQueryFacade;
import com.seeease.flywheel.purchase.request.*;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description 总部采购列表
 * @Date create in 2023/2/14 13:49
 */
@Slf4j
@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseFacade iPurchaseFacade;
    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseDemandFacade demandFacade;
    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseQueryFacade iPurchaseQueryFacade;

    @NacosValue(value = "${subjectPayment.thjs:1}", autoRefreshed = true)
    private List<Integer> paymentThjsList;


    /**
     * 采购需求任务列表
     *
     * @param request
     * @return
     */
    @PostMapping("/demand/list")
    public SingleResponse demandList(@RequestBody PurchaseDemandPageRequest request) {
        return SingleResponse.of(demandFacade.page(request));
    }

    /**
     * 采购需求确认
     *
     * @param request
     * @return
     */
    @PostMapping("/demand/confirm")
    public SingleResponse demandConfirm(@RequestBody PurchaseDemandConfirmRequest request) {
        demandFacade.confirm(request);
        return SingleResponse.buildSuccess();
    }


    /**
     * 采购需求取消
     *
     * @param request
     * @return
     */
    @PostMapping("/demand/cancel")
    public SingleResponse demandConfirm(@RequestBody PurchaseDemandCancelRequest request) {
        demandFacade.cancelHeadOrder(request);
        return SingleResponse.buildSuccess();
    }


    /**
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse queryList(@RequestBody PurchaseListRequest request) {
        request.setStoreId(UserContext.getUser().getStore().getId().intValue());
        return SingleResponse.of(iPurchaseFacade.list(request));
    }

    /**
     * 导出
     */
    @PostMapping("/export")
    public SingleResponse queryList(@RequestBody PurchaseExportRequest request) {
        request.setStoreId(UserContext.getUser().getStore().getId().intValue());
        return SingleResponse.of(iPurchaseFacade.export(request));
    }


    /**
     * 申请结算
     *
     * @param request
     * @return
     */
    @PostMapping("/applySettlement")
    public SingleResponse applySettlement(@RequestBody PurchaseApplySettlementRequest request) {
        request.setStoreId(UserContext.getUser().getStore().getId().intValue());

        iPurchaseFacade.applySettlement(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 批量结算 业务自己主动发起同行寄售
     *
     * @param request
     * @return
     */
    @PostMapping("/batchSettle")
    public SingleResponse batchSettle(@RequestBody PurchaseBatchSettleRequest request) {

        Assert.isTrue(paymentThjsList.contains(request.getSubjectId()), "打款账号不符合");
        iPurchaseFacade.batchSettle(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 转回收
     *
     * @param request
     * @return
     */
    @PostMapping("/changeRecycle")
    public SingleResponse changeRecycle(@RequestBody PurchaseChangeRecycleRequest request) {
        iPurchaseFacade.changeRecycle(request);
        return SingleResponse.buildSuccess();
    }


    /**
     * 延长时间
     *
     * @param request
     * @return
     */
    @PostMapping("/extendTime")
    public SingleResponse extendTime(@RequestBody PurchaseExtendTimeRequest request) {
        iPurchaseFacade.extendTime(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 个人回购关联销售数据
     *
     * @param request
     * @return
     */
    @PostMapping("/purchaseForSale")
    public SingleResponse purchaseForSale(@RequestBody PurchaseForSaleRequest request) {
        return SingleResponse.of(iPurchaseFacade.purchaseForSale(request));
    }


    @PostMapping("/queryBuyBack")
    public SingleResponse queryBuyBack(@RequestBody PurchaseBuyBackRequest request) {
        return SingleResponse.of(iPurchaseQueryFacade.queryBuyBack(request));
    }

    @PostMapping("/test")
    public SingleResponse test() {
//        iPurchaseQueryFacade.test();
        return SingleResponse.buildSuccess();
    }

    /**
     * 补差额
     * @return
     */
    @PostMapping("/marginCover")
    public SingleResponse marginCover(@RequestBody PurchaseMarginCoverRequest request) {
        iPurchaseFacade.marginCover(request);
        return SingleResponse.buildSuccess();
    }
}
