package com.seeease.flywheel.web.controller;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.IAccountReceiptConfirmFacade;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmAddResult;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmConfirmReceiptResult;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmRejectedResult;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmUpdateResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.AccountReceiptConfirmNotice;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 确认收款相关接口
 */
@Slf4j
@RestController
@RequestMapping("/accountReceipt")
public class AccountReceiptConfirmController {

    @DubboReference(check = false, version = "1.0.0")
    private IAccountReceiptConfirmFacade iAccountReceiptConfirmFacade;
    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    /**
     * 小程序端分页查询
     *
     * @return
     */
    @PostMapping("/miniPageQry")
    public SingleResponse miniPageQry(@RequestBody AccountReceiptConfirmMiniPageRequest request) {
        log.info("miniPageQry function start and request = {}", JSON.toJSONString(request));

        Integer shopId = UserContext.getUser().getStore().getId();

        if (FlywheelConstant._ZB_ID != shopId) {
            request.setShopId(shopId);
        }

        return SingleResponse.of(iAccountReceiptConfirmFacade.accountReceiptConfirmPageQuery(request));
    }

    /**
     * 小程序详情
     *
     * @param request
     * @return
     */
    @PostMapping("/miniDetail")
    public SingleResponse detail(@RequestBody AccountReceiptConfirmDetailRequest request) {

        return SingleResponse.of(iAccountReceiptConfirmFacade.detail(request));
    }

    /**
     * 小程序创建
     *
     * @return
     */
    @PostMapping("/create")
    public SingleResponse create(@RequestBody AccountReceiptConfirmCreateRequest request) {
        log.info("miniPageQry function start and request = {}", JSON.toJSONString(request));

        AccountReceiptConfirmAddResult result = iAccountReceiptConfirmFacade.accountReceiptConfirmCreate(request);

        try {
            AccountReceiptConfirmNotice notice = new AccountReceiptConfirmNotice();
            notice.setId(result.getId());
            notice.setSerialNo(result.getSerialNo());
            notice.setCreatedBy(result.getCreatedBy());
            notice.setCreatedTime(result.getCreatedTime());
            notice.setState("待确认");
            notice.setShopId(FlywheelConstant._ZB_ID);
            notice.setToUserRoleKey(Lists.newArrayList(FlywheelConstant.HQ_FINANCE));
            notice.setScene("finance");
            wxCpMessageFacade.send(notice);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return SingleResponse.of(result);
    }


    /**
     * 修改确认收款
     *
     * @param request
     * @return
     */
    @PostMapping("/update")
    public SingleResponse update(@RequestBody AccountReceiptConfirmUpdateRequest request) {

        AccountReceiptConfirmUpdateResult result = iAccountReceiptConfirmFacade.accountReceiptConfirmUpdate(request);
        try {
            AccountReceiptConfirmNotice notice = new AccountReceiptConfirmNotice();
            notice.setId(result.getId());
            notice.setSerialNo(result.getSerialNo());
            notice.setCreatedBy(result.getCreatedBy());
            notice.setCreatedTime(result.getCreatedTime());
            notice.setState("待确认");
            notice.setShopId(FlywheelConstant._ZB_ID);
            notice.setToUserRoleKey(Lists.newArrayList(FlywheelConstant.HQ_FINANCE));
            notice.setScene("finance");
            wxCpMessageFacade.send(notice);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return SingleResponse.of(result);
    }

    /**
     * 驳回确认收款
     *
     * @param request
     * @return
     */
    @PostMapping("/rejected")
    public SingleResponse rejected(@RequestBody AccountReceiptConfirmRejectedRequest request) {

        AccountReceiptConfirmRejectedResult result = iAccountReceiptConfirmFacade.rejected(request);
        try {
            AccountReceiptConfirmNotice notice = new AccountReceiptConfirmNotice();
            notice.setId(result.getId());
            notice.setSerialNo(result.getSerialNo());
            notice.setCreatedBy(result.getCreatedBy());
            notice.setCreatedTime(result.getCreatedTime());
            notice.setState(result.getState());
            notice.setShopId(result.getShopId());
            notice.setToUserIdList(Lists.newArrayList(result.getCreatedId()));
            notice.setScene("client");
            wxCpMessageFacade.send(notice);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return SingleResponse.of(result);
    }

    /**
     * 取消确认收款
     *
     * @param request
     * @return
     */
    @PostMapping("/cancel")
    public SingleResponse cancel(@RequestBody AccountReceiptConfirmCancelRequest request) {
        iAccountReceiptConfirmFacade.cancel(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * PC端分页查询
     *
     * @return
     */
    @PostMapping("pageQry")
    public SingleResponse pageQry(@RequestBody AccountReceiptConfirmPageRequest request) {
        log.info("miniPageQry function start and request = {}", JSON.toJSONString(request));
        Integer shopId = UserContext.getUser().getStore().getId();

        if (FlywheelConstant._ZB_ID != shopId) {
            request.setStoreId(shopId);
        }
        return SingleResponse.of(iAccountReceiptConfirmFacade.accountReceiptConfirmPCPageQuery(request));
    }

    /**
     * 收款详情
     *
     * @return
     */
    @PostMapping("receiptDetail")
    public SingleResponse receiptDetail(@RequestBody AccountReceiptConfirmDetailRequest request) {
        log.info("receiptDetail function of  AccountReceiptConfirmController start and request = {}", JSON.toJSONString(request));


        return SingleResponse.of(iAccountReceiptConfirmFacade.accountReceiptConfirmDetail(request));
    }

    /**
     * 收款明细
     *
     * @param request
     * @return
     */
    @PostMapping("/collectionDetails")
    public SingleResponse collectionDetails(@RequestBody AccountReceiptConfirmCollectionDetailsRequest request) {

        return SingleResponse.of(iAccountReceiptConfirmFacade.collectionDetails(request));
    }

    /**
     * 收款商品
     *
     * @return
     */
    @PostMapping("/receiptGoods")
    public SingleResponse receiptGoods(@RequestBody AccountReceiptConfirmGoodsDetailRequest request) {
        log.info("receiptDetail function of  AccountReceiptConfirmController start and request = {}", JSON.toJSONString(request));

        return SingleResponse.of(iAccountReceiptConfirmFacade.accountReceiptConfirmGoodsDetail(request));
    }

    /**
     * 收款流水
     *
     * @return
     */
    @PostMapping("receiptFlowUpdate")
    public SingleResponse receiptFlowUpdate(@RequestBody AccountReceiptConfirmFlowUpdateRequest request) {
        log.info("receiptFlowUpdate function of AccountReceiptConfirmController start and request = {}", JSON.toJSONString(request));

        iAccountReceiptConfirmFacade.accountReceStateUpdate(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * (新)确认收款操作
     *
     * @param request
     * @return
     */
    @PostMapping("confirmReceipt")
    public SingleResponse confirmReceipt(@RequestBody AccountReceiptConfirmConfirmReceiptRequest request) {
        log.info("confirmReceipt function of AccountReceiptConfirmController start and request = {}", JSON.toJSONString(request));

        try {
            AccountReceiptConfirmConfirmReceiptResult result = iAccountReceiptConfirmFacade.confirmReceipt(request);
            AccountReceiptConfirmNotice notice = new AccountReceiptConfirmNotice();
            notice.setId(result.getId());
            notice.setSerialNo(result.getSerialNo());
            notice.setCreatedBy(result.getCreatedBy());
            notice.setCreatedTime(result.getCreatedTime());
            notice.setState(result.getState());
            notice.setShopId(result.getShopId());
            notice.setToUserIdList(Lists.newArrayList(result.getCreatedId()));
            notice.setScene("client");
            wxCpMessageFacade.send(notice);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return SingleResponse.buildSuccess();
    }

    /**
     * 校验交易流水
     *
     * @param request
     * @return
     */
    @PostMapping("checkConfirmReceipt")
    public SingleResponse checkConfirmReceipt(@RequestBody AccountReceiptConfirmConfirmReceiptRequest request) {
        log.info("checkConfirmReceipt function of AccountReceiptConfirmController start and request = {}", JSON.toJSONString(request));

        return SingleResponse.of(iAccountReceiptConfirmFacade.checkConfirmReceipt(request));
    }

    /**
     * 批量审核
     *
     * @param request
     * @return
     */
    @PostMapping("/batchAudit")
    public SingleResponse batchAudit(@RequestBody AccountReceiptConfirmBatchAuditRequest request) {
        iAccountReceiptConfirmFacade.batchAudit(request);
        return SingleResponse.buildSuccess();
    }
}
