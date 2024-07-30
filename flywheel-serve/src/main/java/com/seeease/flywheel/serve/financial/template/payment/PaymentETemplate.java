package com.seeease.flywheel.serve.financial.template.payment;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author Mr. Du
 * @Description 个人回收（置换）
 * 入库核销（ok）
 * 打款核销待定
 * @Date create in 2024/1/11 11:09
 */

public interface PaymentETemplate extends PaymentTemplate {

    /**
     * 创建申请打款单&&创建应付单
     *
     * @param request
     */
    void createPaymentAndGeneratePayable(JSONObject request);

    /**
     * 通知核销
     *
     * @param request
     */
    void listenerVerification(JSONObject request);
}
