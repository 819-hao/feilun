package com.seeease.flywheel.web.infrastructure.service;

import com.seeease.flywheel.web.entity.DouYinOrder;
import com.seeease.flywheel.web.entity.DouYinOrderLine;
import com.seeease.flywheel.web.entity.DouYinOrderRefund;
import com.seeease.flywheel.web.entity.DouYinShopMapping;
import com.seeease.flywheel.web.entity.douyin.DouYinRefundCreatedData;
import com.seeease.flywheel.web.entity.request.DouYinCustomerDecryptionRequest;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/4/27
 */
public interface DouYinService {
    /**
     * 创建抖音订单
     *
     * @param douYinOrder
     * @param lineList
     * @param shopMapping
     * @return
     */
    String create(DouYinOrder douYinOrder, List<DouYinOrderLine> lineList, DouYinShopMapping shopMapping);

    /**
     * 取消抖音订单
     *
     * @param douYinOrder
     */
    void cancelOrder(DouYinOrder douYinOrder);

    /**
     * 抖音订单退货
     *
     * @param douYinOrderRefund
     * @return
     */
    String refundOrder(DouYinOrderRefund douYinOrderRefund, DouYinOrder order);

    /**
     * 抖音订单客户重解密更新
     *
     * @param request
     */
    void customerDecryption(DouYinCustomerDecryptionRequest request);

    /**
     * 更新客户地址信息
     *
     * @param douYinOrder
     */
    void updateCustomerInfo(DouYinOrder douYinOrder);

    void saleIntercept(DouYinRefundCreatedData request);
}
