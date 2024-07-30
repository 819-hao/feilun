package com.seeease.flywheel.serve.financial.template.payment;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author Mr. Du
 * @Description 个人回购（回收/置换）
 * 个人回购（入库核销OK）
 * 打款核销 待定
 * @Date create in 2024/1/11 11:09
 */

public interface PaymentFTemplate extends PaymentTemplate {

    /**
     * 创建应付单
     *
     * @param request
     */
    void generatePayable(JSONObject request);

    /**
     * 通知核销
     *
     * @param request
     */
    void listenerVerification(JSONObject request);
}
