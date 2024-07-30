package com.seeease.flywheel.purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseDemandSaleOrderMqPushDto implements Serializable {
    /**
     * 商品id
     */
    private Integer stockId;
    /**
     * 销售金额
     */
    private BigDecimal salePrice;
    /**
     * 订购需求订单的订单编码
     */
    private String orderCode;
    /**
     * 飞轮销售订单
     */
    private String flSaleOrderCode;

    private String firstSalesman;
    private String secondSalesman;
    private String thirdSalesman;

    /**
     * 联系人姓名
     */
    private String contactName;
    /**
     * 联系人电话
     */
    private String contactPhone;
    /**
     * 联系人地址
     */
    private String contactAddress;

}
