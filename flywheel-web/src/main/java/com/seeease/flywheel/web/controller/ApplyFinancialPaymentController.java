package com.seeease.flywheel.web.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.firework.facade.common.request.WaitTaskRequest;
import com.seeease.firework.facade.service.ITaskFacade;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.IApplyFinancialPaymentFacade;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentCreateResult;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentObsoleteRecordResult;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentOperateResult;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentUpdateResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.ApplyFinancialPaymentNotice;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import com.seeease.flywheel.web.entity.WorkflowStart;
import com.seeease.flywheel.web.infrastructure.mapper.WorkflowStartMapper;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Slf4j
@RestController
@RequestMapping("/applyFinancialPayment")
public class ApplyFinancialPaymentController {

    /**
     * 同行采购限制打款账号
     */
    @NacosValue(value = "${subjectPayment.th:1}", autoRefreshed = true)
    private List<Integer> paymentThList;

    /**
     * 个人回收限制打款账号
     */
    @NacosValue(value = "${subjectPayment.gr:1}", autoRefreshed = true)
    private List<Integer> paymentGrList;

    @DubboReference(check = false, version = "1.0.0")
    private IApplyFinancialPaymentFacade facade;

    @DubboReference(check = false, version = "1.0.0")
    private ITaskFacade taskFacade;
    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    @Resource
    private WorkflowStartMapper workflowStartMapper;

    /**
     * 小程序and pc查询列表
     *
     * @param request
     * @return
     */
    @PostMapping("/query")
    public SingleResponse query(@RequestBody ApplyFinancialPaymentQueryRequest request) {

        return SingleResponse.of(facade.query(request));
    }

    /**
     * 小程序创建申请打款单
     * PEER_PROCUREMENT(0, "同行采购"),
     * PERSONAL_RECYCLING(1, "个人回收"),
     *
     * @param request
     * @return
     */
    @PostMapping("/create")
    public SingleResponse create(@RequestBody ApplyFinancialPaymentAppletCreateRequest request) {

        switch (request.getTypePayment()) {
            case 0:
                if (!paymentThList.contains(request.getSubjectPayment())) {
                    throw new OperationRejectedException(OperationExceptionCodeEnum.RESTRICT_ERROR);
                }
                break;
            case 1:
                if (!paymentGrList.contains(request.getSubjectPayment())) {
                    throw new OperationRejectedException(OperationExceptionCodeEnum.RESTRICT_ERROR);
                }
                break;
            default:
                throw new OperationRejectedException(OperationExceptionCodeEnum.ENUM_ERROR);
        }
        request.setPayment(1);
        ApplyFinancialPaymentCreateResult result = facade.create(request);

        //通知财务审核 打款单
        ApplyFinancialPaymentNotice notice = new ApplyFinancialPaymentNotice();
        notice.setId(result.getId());
        notice.setSerialNo(result.getSerialNo());
        notice.setCreatedBy(UserContext.getUser().getUserName());
        notice.setCreatedTime(new Date());
        notice.setState(FlywheelConstant.PENDING_REVIEW);
        notice.setShopId(FlywheelConstant._ZB_ID);
        notice.setToUserRoleKey(Lists.newArrayList(FlywheelConstant.HQ_FINANCE));
        notice.setScene("/pages/finance/paymentApplyDetail?map=finance");
        wxCpMessageFacade.send(notice);

        return SingleResponse.buildSuccess();
    }

    /**
     * 小程序修改申请打款
     *
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public SingleResponse update(@RequestBody ApplyFinancialPaymentUpdateRequest request) {

        ApplyFinancialPaymentUpdateResult result = facade.update(request);

        ApplyFinancialPaymentNotice notice = new ApplyFinancialPaymentNotice();
        notice.setId(result.getId());
        notice.setSerialNo(result.getSerialNo());
        notice.setCreatedBy(UserContext.getUser().getUserName());
        notice.setCreatedTime(new Date());
        notice.setState(FlywheelConstant.PENDING_REVIEW);
        notice.setShopId(FlywheelConstant._ZB_ID);
        notice.setToUserRoleKey(Lists.newArrayList(FlywheelConstant.HQ_FINANCE));
        notice.setScene("/pages/finance/paymentApplyDetail?map=finance");

        return SingleResponse.buildSuccess();
    }


    /**
     * 小程序操作打款单
     * 财务 确认 驳回
     *
     * @param request
     * @return
     */
    @PostMapping("/operate")
    public SingleResponse operate(@RequestBody ApplyFinancialPaymentOperateRequest request) {

        ApplyFinancialPaymentOperateResult result = facade.operate(request);
        if (Arrays.asList(1, 2).contains(request.getState())) {
            //通知申请人打款结果
            ApplyFinancialPaymentNotice notice = new ApplyFinancialPaymentNotice();
            notice.setId(result.getId());
            notice.setSerialNo(result.getSerialNo());
            notice.setCreatedBy(result.getCreatedBy());
            notice.setCreatedTime(result.getCreatedTime());
            notice.setState(result.getState());
            notice.setShopId(result.getShopId());
            notice.setToUserIdList(Lists.newArrayList(result.getCreatedId()));
            notice.setScene("/pages/finance/paymentApplyDetail?map=client");
            wxCpMessageFacade.send(notice);
        }

        if (ObjectUtils.isNotEmpty(result.getPurchaseTaskVO()) && StringUtils.isNotBlank(result.getPurchaseTaskVO().getSerialNo())) {

            WorkflowStart workflowStart = workflowStartMapper.selectList(Wrappers.<WorkflowStart>lambdaQuery().eq(WorkflowStart::getBusinessKey, result.getPurchaseTaskVO().getSerialNo())).stream().findAny().orElse(null);

            if (ObjectUtils.isNotEmpty(workflowStart) && StringUtils.isNotBlank(workflowStart.getProcessInstanceId())) {
                taskFacade.waitTask(WaitTaskRequest.builder().processInstanceId(workflowStart.getProcessInstanceId()).activityId(TaskDefinitionKeyEnum.FINANCIAL_PAYMENT.getKey()).build());
            }
        }

        return SingleResponse.buildSuccess();
    }

    /**
     * 小程序详情
     *
     * @param request
     * @return
     */
    @PostMapping("/detail")
    public SingleResponse detail(@RequestBody ApplyFinancialPaymentDetailRequest request) {

        return SingleResponse.of(facade.detail(request));
    }

    /**
     * 小程序查询审核列表
     *
     * @param request
     * @return
     */
    @PostMapping("/approvedMemo")
    public SingleResponse approvedMemo(@RequestBody ApplyFinancialPaymentRecordRequest request) {

        return SingleResponse.of(facade.approvedMemo(request));
    }

    /**
     * 付款商品
     *
     * @param request
     * @return
     */
    @PostMapping("/orderDetails")
    public SingleResponse orderDetails(@RequestBody ApplyFinancialPaymentOrderDetailRequest request) {

        return SingleResponse.of(facade.orderDetails(request));
    }

    /**
     * 取消采购计划
     *
     * @param request
     * @return
     */
    @PostMapping("/cancelTask")
    public SingleResponse cancelTask(@RequestBody ApplyFinancialPaymentOrderCancelTaskRequest request) {
        facade.cancelTask(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 小程序取消申请打款
     *
     * @param request
     * @return
     */
    @PostMapping("/cancel")
    public SingleResponse cancel(@RequestBody ApplyFinancialPaymentAppletCancelRequest request) {
        facade.cancel(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 小程序作废申请打款
     *
     * @param request
     * @return
     */
    @PostMapping("/obsolete")
    public SingleResponse obsolete(@RequestBody ApplyFinancialPaymentObsoleteRequest request) {

        ApplyFinancialPaymentObsoleteRecordResult result = facade.obsolete(request);

        //通知申请人打款结果
        ApplyFinancialPaymentNotice notice = new ApplyFinancialPaymentNotice();
        notice.setId(result.getId());
        notice.setSerialNo(result.getSerialNo());
        notice.setCreatedBy(result.getCreatedBy());
        notice.setCreatedTime(result.getCreatedTime());
        notice.setState(result.getState());
        notice.setShopId(result.getShopId());
        notice.setToUserIdList(Lists.newArrayList(result.getCreatedId()));
        wxCpMessageFacade.send(notice);

        return SingleResponse.buildSuccess();
    }

    /**
     * pc 小程序查询作废单数据
     *
     * @param request
     * @return
     */
    @PostMapping("/obsoleteRecordPage")
    public SingleResponse obsoleteRecordPage(@RequestBody ApplyFinancialPaymentObsoleteRecordPageRequest request) {

        return SingleResponse.of(facade.obsoleteRecordPage(request));
    }

    /**
     * 获取门店限制额度
     * <a href="https://x25v72.axshare.com/#id=ht4iig&p=%E6%96%B0%E5%BB%BA&g=1"> 原型图<a/>
     *
     * @param
     * @return
     */
    @GetMapping("/limit/price")
    public SingleResponse<String> limitPrice(@RequestParam Integer id) {
        return SingleResponse.of(facade.limitPrice(id));
    }


    @PostMapping("/checkoutStockSn")
    public SingleResponse checkoutStockSn(@RequestBody ApplyFinancialPaymentCheckoutStockSnRequest request) {

        return SingleResponse.of(facade.checkoutStockSn(request));
    }

    /**
     * 申请打款单全部
     *
     * @param request
     * @return
     */
    @PostMapping("/queryAll")
    public SingleResponse all(@RequestBody ApplyFinancialPaymentQueryAllRequest request) {

        return SingleResponse.of(facade.queryAll(request));
    }

    /**
     * pc端查询列表
     *
     * @param request
     * @return
     */
    @PostMapping("/queryByCondition")
    public SingleResponse queryByCondition(@RequestBody ApplyFinancialPaymentQueryByConditionRequest request) {

        return SingleResponse.of(facade.queryByCondition(request));
    }

    @PostMapping("/export")
    public SingleResponse export(@RequestBody ApplyFinancialPaymentQueryByConditionRequest request) {

        return SingleResponse.of(facade.export(request));
    }

}
