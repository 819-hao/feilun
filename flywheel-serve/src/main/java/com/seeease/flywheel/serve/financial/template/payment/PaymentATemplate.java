package com.seeease.flywheel.serve.financial.template.payment;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author Mr. Du
 * @Description
 *
 * @Date create in 2024/1/11 11:09
 */

public interface PaymentATemplate extends PaymentTemplate {

    /**
     * 创建确认收款单
     *
     * @param request
     */
    void createReceipt(JSONObject request);
}
