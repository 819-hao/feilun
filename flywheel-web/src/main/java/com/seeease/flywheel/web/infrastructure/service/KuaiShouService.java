package com.seeease.flywheel.web.infrastructure.service;

import com.seeease.flywheel.web.entity.KuaishouOrder;
import com.seeease.flywheel.web.entity.KuaishouOrderRefund;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/12/5 16:53
 */

public interface KuaiShouService {

    /**
     * 取消快手订单
     *
     * @param kuaishouOrder
     */
    void cancelOrder(KuaishouOrder kuaishouOrder);

    /**
     * 快手订单退货
     *
     * @param kuaishouOrderRefund
     * @return
     */
    String refundOrder(KuaishouOrderRefund kuaishouOrderRefund, KuaishouOrder kuaishouOrder);
}
