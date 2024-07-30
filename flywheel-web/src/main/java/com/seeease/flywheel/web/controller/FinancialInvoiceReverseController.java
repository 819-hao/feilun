package com.seeease.flywheel.web.controller;


import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.IFinancialInvoiceReverseFacade;
import com.seeease.flywheel.financial.request.FinancialInvoiceReverseCancelRequest;
import com.seeease.flywheel.financial.request.FinancialInvoiceReverseFlushingRequest;
import com.seeease.flywheel.financial.request.FinancialInvoiceReverseQueryByConditionRequest;
import com.seeease.flywheel.financial.result.FinancialInvoiceReverseFlushingCancelResult;
import com.seeease.flywheel.financial.result.FinancialInvoiceReverseFlushingCreateResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.FinancialInvoiceNotice;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
 * 红冲商品列表
 *
 * @author wbh
 * @date 2023/2/27
 */
@Slf4j
@RestController
@RequestMapping("/financialInvoiceReverse")
public class FinancialInvoiceReverseController {
    @DubboReference(check = false, version = "1.0.0")
    private IFinancialInvoiceReverseFacade facade;
    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    /**
     * pc去红冲
     *
     * @param request
     * @return
     */
    @PostMapping("/pc/flushing")
    public SingleResponse create(@RequestBody FinancialInvoiceReverseFlushingRequest request) {

        FinancialInvoiceReverseFlushingCreateResult result = facade.flushing(request);

        FinancialInvoiceNotice notice = new FinancialInvoiceNotice();
        notice.setId(result.getId());
        notice.setSerialNo(result.getSerialNo());
        notice.setCreatedBy(UserContext.getUser().getUserName());
        notice.setCreatedTime(new Date());
        notice.setState(FlywheelConstant.PENDING_INVOICE);
        notice.setShopId(FlywheelConstant._ZB_ID);
        notice.setToUserRoleKey(Lists.newArrayList(FlywheelConstant.HQ_FINANCE));
        wxCpMessageFacade.send(notice);

        return SingleResponse.of(result);
    }

    /**
     * 去红冲取消
     *
     * @param request
     * @return
     */
    @PostMapping("/pc/cancel")
    public SingleResponse cancel(@RequestBody FinancialInvoiceReverseCancelRequest request) {

        FinancialInvoiceReverseFlushingCancelResult result = facade.cancel(request);

        FinancialInvoiceNotice notice = new FinancialInvoiceNotice();
        notice.setId(result.getId());
        notice.setSerialNo(result.getSerialNo());
        notice.setCreatedBy(UserContext.getUser().getUserName());
        notice.setCreatedTime(new Date());
        notice.setState(FlywheelConstant.PENDING_INVOICE);
        notice.setShopId(FlywheelConstant._ZB_ID);
        notice.setToUserRoleKey(Lists.newArrayList(FlywheelConstant.HQ_FINANCE));
        wxCpMessageFacade.send(notice);

        return SingleResponse.of(result);
    }


    /**
     * pc端查询列表
     *
     * @param request
     * @return
     */
    @PostMapping("/pc/queryByCondition")
    public SingleResponse queryByCondition(@RequestBody FinancialInvoiceReverseQueryByConditionRequest request) {

        request.setState(Optional.ofNullable(request.getState())
                .filter(v -> v != -1)
                .orElse(null));

        return SingleResponse.of(facade.queryByCondition(request));
    }

    /**
     * 导出
     *
     * @param request
     * @return
     */
    @PostMapping("/pc/export")
    public SingleResponse export(@RequestBody FinancialInvoiceReverseQueryByConditionRequest request) {

        return SingleResponse.of(facade.export(request));
    }
}
