package com.seeease.flywheel.serve.financial.template.payment;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author Mr. Du
 * @Description 采购退货（质检）
 * @Date create in 2024/1/11 11:09
 */

public interface PaymentGTemplate extends PaymentTemplate {

    /**
     * 创建应付单
     *
     * @param request
     */
    void generatePayable(JSONObject request);

    /**
     * 创建确认收款单
     * 核销没有
     * @param request
     */
    void createReceipt(JSONObject request);

    /**
     * 通知核销
     *
     * @param request
     */
    void listenerVerification(JSONObject request);
}
