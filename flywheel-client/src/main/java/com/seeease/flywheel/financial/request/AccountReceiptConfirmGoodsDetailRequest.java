package com.seeease.flywheel.financial.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 确认收款单---订单详情/商品详情
 */
@Data
public class AccountReceiptConfirmGoodsDetailRequest implements Serializable {

    /**
     * 确认收款单id
     */
    private Integer id;
}
