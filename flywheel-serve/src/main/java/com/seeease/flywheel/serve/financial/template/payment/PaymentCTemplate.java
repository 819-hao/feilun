package com.seeease.flywheel.serve.financial.template.payment;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author Mr. Du
 * @Description 同行采购(备货 / 定金)||个人回收(仅回收)
 *
 * @Date create in 2024/1/11 11:09
 */

public interface PaymentCTemplate extends PaymentTemplate {

    /**
     * 创建预付单
     *
     * @param request
     */
    void generatePrepaid(JSONObject request);

    /**
     * 通知核销
     *
     * @param request
     */
    void listenerVerification(JSONObject request);
}
