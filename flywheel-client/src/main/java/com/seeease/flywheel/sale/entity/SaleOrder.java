package com.seeease.flywheel.sale.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
public class SaleOrder implements Serializable {

    /**
     * 订单id
     */
    private Integer orderId;
    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 第三方订单编号
     */
    private String thirdOrderNo;
    /**
     * 客户名
     */
    private String customerName;
    /**
     * 联系方式
     */
    private String customerPhone;
    /**
     * 联系地址
     */
    private String customerAddress;

    /**
     * 付款方式
     */
    private String payModel;

    /**
     * 销售门店id
     */
    private Integer shopId;

    /**
     * 销售门店
     */
    private String shopName;

    /**
     * 第一销售人
     */
    private String firstSalesman;

    /**
     * 第二销售人
     */
    private String secondSalesman;

    /**
     * 第三销售人
     */
    private String thirdSalesman;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 完成时间
     */
    private Date finishTime;

    /**
     * 订单商品行信息
     */
    private List<SaleOrderLine> orderLines;

    /**
     * 销售渠道
     */
    private Integer saleChannel;
}
