package com.seeease.flywheel.financial;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.*;

import java.util.List;

/**
 * @author wbh
 * @date 2023/2/27
 */
public interface IApplyFinancialPaymentFacade {


    /**
     * 小程序创建申请打款单
     *
     * @param request
     * @return
     */
    ApplyFinancialPaymentCreateResult create(ApplyFinancialPaymentAppletCreateRequest request);

    /**
     * 小程序重新审核
     *
     * @param request
     */
    ApplyFinancialPaymentUpdateResult update(ApplyFinancialPaymentUpdateRequest request);

    /**
     * 小程序查询列表
     *
     * @param request
     * @return
     */
    PageResult<ApplyFinancialPaymentPageResult> query(ApplyFinancialPaymentQueryRequest request);

    /**
     * 查询全量
     *
     * @param request
     * @return
     */
    PageResult<ApplyFinancialPaymentPageAllResult> queryAll(ApplyFinancialPaymentQueryAllRequest request);

    /**
     * 小程序审核操作
     *
     * @param request
     * @return
     */
    ApplyFinancialPaymentOperateResult operate(ApplyFinancialPaymentOperateRequest request);

    /**
     * 小程序申请打款详情
     *
     * @param request
     * @return
     */
    ApplyFinancialPaymentDetailResult detail(ApplyFinancialPaymentDetailRequest request);

    /**
     * 小程序审核记录
     *
     * @param request
     * @return
     */
    PageResult<ApplyFinancialPaymentRecordPageResult> approvedMemo(ApplyFinancialPaymentRecordRequest request);

    /**
     * 申请打款列表
     *
     * @param request
     * @return
     */
    PageResult<ApplyFinancialPaymentPageQueryByConditionResult> queryByCondition(ApplyFinancialPaymentQueryByConditionRequest request);

    /**
     * 导出
     *
     * @param request
     * @return
     */
    PageResult<ApplyFinancialPaymentPageQueryByConditionResult> export(ApplyFinancialPaymentQueryByConditionRequest request);

    /**
     * 取消
     *
     * @param request
     */
    void cancel(ApplyFinancialPaymentAppletCancelRequest request);

    /**
     * 付款商品详情
     *
     * @param request
     * @return
     */
    ApplyFinancialPaymentOrderDetailsResult orderDetails(ApplyFinancialPaymentOrderDetailRequest request);

    /**
     * 获取门店限制额度
     *
     * @return
     */
    String limitPrice(Integer shopId);

    ApplyFinancialPaymentObsoleteRecordResult obsolete(ApplyFinancialPaymentObsoleteRequest request);

    List<ApplyFinancialPaymentObsoleteRecordPageResult> obsoleteRecordPage(ApplyFinancialPaymentObsoleteRecordPageRequest request);

    List<String> checkoutStockSn(ApplyFinancialPaymentCheckoutStockSnRequest request);

    /**
     * 取消采购计划
     *
     * @param request
     */
    void cancelTask(ApplyFinancialPaymentOrderCancelTaskRequest request);
}
