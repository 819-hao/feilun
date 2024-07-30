package com.seeease.flywheel.serve.financial.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.*;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPayment;

import java.math.BigDecimal;

/**
 * @author edy
 * @description 针对表【apply_financial_payment】的数据库操作Service
 * @createDate 2023-02-27 16:06:51
 */
public interface ApplyFinancialPaymentService extends IService<ApplyFinancialPayment> {

    ApplyFinancialPaymentCreateResult create(ApplyFinancialPaymentCreateRequest request);

    ApplyFinancialPaymentUpdateResult update(ApplyFinancialPaymentUpdateRequest request);

    Page<ApplyFinancialPaymentPageResult> page(ApplyFinancialPaymentQueryRequest request);

    /**
     * 查询状态
     *
     * @param request
     * @return
     */
    Page<ApplyFinancialPaymentPageAllResult> queryAll(ApplyFinancialPaymentQueryAllRequest request);

    ApplyFinancialPaymentOperateResult operate(ApplyFinancialPaymentOperateRequest request);

    ApplyFinancialPaymentDetailResult detail(ApplyFinancialPaymentDetailRequest request);

    Page<ApplyFinancialPaymentRecordPageResult> approvedMemo(ApplyFinancialPaymentRecordRequest request);

    Page<ApplyFinancialPaymentPageQueryByConditionResult> queryByCondition(ApplyFinancialPaymentQueryByConditionRequest request);

    /**
     * 根据场景 申请打款单做相对应操作
     *
     * @param request
     */
    void cancel(ApplyFinancialPaymentCancelRequest request);

    BigDecimal usedPrice(Integer tagId);

    ApplyFinancialPaymentObsoleteRecordResult obsolete(ApplyFinancialPaymentObsoleteRequest request);

    /**
     * 取消采购计划
     *
     * @param request
     * @return
     */
    ApplyFinancialPayment cancelTask(ApplyFinancialPaymentOrderCancelTaskRequest request);
}
