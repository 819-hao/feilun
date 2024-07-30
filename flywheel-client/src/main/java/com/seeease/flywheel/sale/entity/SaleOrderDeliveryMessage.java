package com.seeease.flywheel.sale.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 订单发货通知
 *
 * @author Tiro
 * @date 2023/4/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderDeliveryMessage implements Serializable {

    /**
     * 飞轮订单号
     */
    private String serialNo;

    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;

    /**
     * 商品
     */
    List<Integer> stockIdList;
    /**
     * 快递单号
     */
    private String expressNumber;
}
