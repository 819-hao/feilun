package com.seeease.flywheel.serve.financial.template.payment;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author Mr. Du
 * @Description 寄售未结算退货
 * @Date create in 2024/1/11 11:09
 */

public interface PaymentLTemplate extends PaymentTemplate {

    /**
     * 通知核销
     *
     * @param request
     */
    void listenerVerification(JSONObject request);
}
