package com.seeease.flywheel.web.infrastructure.service;

import com.seeease.flywheel.web.entity.tmall.TMallConsignOrderCancel;
import com.seeease.flywheel.web.entity.tmall.TMallConsignOrderNotify;
import com.seeease.flywheel.web.entity.tmall.TMallReverseOrderInStorageNotify;

/**
 * @author Tiro
 * @date 2023/3/24
 */
public interface TMallService {
    /**
     * 创建订单
     *
     * @param order
     */
    void createOrder(TMallConsignOrderNotify order);

    /**
     * 取消订单
     *
     * @param order
     */
    void cancelOrder(TMallConsignOrderCancel order);

    /**
     * 销退订单
     *
     * @param order
     */
    void reverseOrder(TMallReverseOrderInStorageNotify order);
}
