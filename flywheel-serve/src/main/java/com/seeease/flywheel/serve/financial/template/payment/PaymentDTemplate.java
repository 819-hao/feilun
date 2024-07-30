package com.seeease.flywheel.serve.financial.template.payment;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author Mr. Du
 * @Description 同行采购(集采 / 寄售)||个人寄售
 * <p>
 * 集采->ok
 * 集采-质检 还差确认打款单核销
 * @Date create in 2024/1/11 11:09
 */

public interface PaymentDTemplate extends PaymentTemplate {

    /**
     * 创建应付单
     *
     * @param request
     */
    void generatePayable(JSONObject request);

    /**
     * 变更应收应付单未待核销
     *
     * @param request
     */
    void updatePayable(JSONObject request);

    /**
     * 通知核销
     *
     * @param request
     */
    void listenerVerification(JSONObject request);
}
