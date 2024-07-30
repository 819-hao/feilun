package com.seeease.flywheel.serve.financial.template.payment;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author Mr. Du
 * @Description 入库退货
 * @Date create in 2024/1/11 11:09
 */

public interface PaymentHTemplate extends PaymentTemplate {

    /**
     * 创建确认收款单&&创建应付单
     *
     * @param request
     */
    void createReceiptAndGeneratePayable(JSONObject request);

    /**
     * 通知核销 核销没有
     *
     * @param request
     */
    void listenerVerification(JSONObject request);
}
