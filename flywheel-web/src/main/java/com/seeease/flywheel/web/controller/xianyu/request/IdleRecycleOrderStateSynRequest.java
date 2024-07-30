package com.seeease.flywheel.web.controller.xianyu.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/10/19
 */
@Data
public class IdleRecycleOrderStateSynRequest implements Serializable {

    /**
     * 估价id
     */
    private String apprize_id;
    /**
     * 订单id
     */
    private String biz_order_id;
    /**
     * 订单状态
     */
    private Integer order_status;

    /**
     * 卖家支付宝id
     */
    private String seller_alipay_user_id;
    /**
     * 卖家手机号
     */
    private String seller_phone;
    /**
     * 卖家真实姓名
     */
    private String seller_real_name;
    /**
     * 卖家昵称
     */
    private String seller_nick;

    /**
     * 取件类型 1：顺风 2：上门取件
     */
    private Integer ship_type;

    /**
     * 取件时间
     */
    private String ship_time;

    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String area;
    /**
     * 取件地址
     */
    private String seller_address;

    /**
     * 估价金额
     */
    private String apprize_amount;

    /**
     * 用户评论
     */
    private String rate_content;

    /**
     * 取消原因
     */
    private String close_reason;
}
