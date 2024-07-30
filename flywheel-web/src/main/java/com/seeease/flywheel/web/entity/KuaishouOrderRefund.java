package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 快手退货订单
 * @TableName kuaishou_order_refund
 */
@TableName(value ="kuaishou_order_refund")
@Data
public class KuaishouOrderRefund extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 抖音门店id
     */
    private Long kuaiShouShopId;

    /**
     * 快手门店名称（sellerNick）
     */
    private String kuaiShouShopName;

    /**
     * 抖音退款订单id
     */
    private String refundOrderId;

    /**
     * 抖音正向订单id
     */
    private String orderId;

    /**
     * 抖音正向子订单id
     */
    private String orderSubId;

    /**
     * 飞轮退货订单号
     */
    private String returnSerialNo;

    /**
     * 售后状态
     */
    private Integer refundStatus;

    /**
     * 售后类型
     */
    private Integer refundType;

    /**
     * 申请退款的金额（含运费）
     */
    private BigDecimal refundAmount;

    /**
     * 退款同意时间
     */
    private Date refundAgreedTime;

    /**
     * 售后原因
     */
    private Integer reasonCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}