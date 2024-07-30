package com.seeease.flywheel.serve.douyin.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

/**
 * 
 * @TableName dou_yin_refund_created
 */
@TableName(value ="dou_yin_refund_created")
@Data
public class DouYinRefundCreated extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 抖音门店id
     */
    private Long douYinShopId;

    /**
     * 售后单ID
     */
    private String aftersaleId;

    /**
     * 抖音正向订单id
     */
    private String orderId;

    /**
     * 抖音正向子订单id
     */
    private String orderSubId;

    /**
     * 售后状态：6-售后申请；7-售后退货中；8-【补寄\维修返回：售后待商家发货】；11-售后已发货；12-售后成功；13-【换货\补寄\维修返回：售后商家已发货，待用户收货】； 14-【换货\补寄\维修返回：售后用户已收货】 ；27-拒绝售后申请；28-售后失败；29-售后退货拒绝；51-订单取消成功；53-逆向交易已完成；
     */
    private Long aftersaleStatus;

    /**
     * 售后类型： 0-售后退货退款；1-售后仅退款；2-发货前退款；3-换货；6-价保；7-补寄；8-维修
     */
    private Long aftersaleType;

    /**
     * 申请退款的金额（含运费）
     */
    private BigDecimal refundAmount;

    /**
     * 售后申请时间
     */
    private Date applyTime;
    /**
     * 售后申请时间
     */
    private Date modifyTime;
    /**
     * 售后原因
     */
    private Long reasonCode;

    private static final long serialVersionUID = 1L;
}