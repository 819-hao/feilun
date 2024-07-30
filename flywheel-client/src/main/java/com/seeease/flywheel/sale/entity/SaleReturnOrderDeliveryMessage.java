package com.seeease.flywheel.sale.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 销售退货客户发货消息
 *
 * @author Tiro
 * @date 2023/8/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleReturnOrderDeliveryMessage implements Serializable {


    /**
     * 飞轮订单号
     */
    private String serialNo;


    /**
     * 飞轮退货订单号
     */
    private String returnSerialNo;

    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;

    /**
     * 商品
     */
    List<Integer> stockIdList;

}
