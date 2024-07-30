package com.seeease.flywheel.web.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 闲鱼估价订单VO
 *
 * @TableName
 */
@Data
public class XyRecycleOrderVO implements Serializable {
    /**
     *
     */
    private Integer id;

    /**
     * 估价id
     */
    private String quoteId;

    /**
     * 估价订单状态
     */
    private Integer quoteOrderState;

    /**
     * spuId
     */
    private String spuId;

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 型号
     */
    private String model;

    /**
     * 投放业务
     */
    private String bizType;

    /**
     * 版本
     */
    private String version;

    /**
     * 渠道
     */
    private String channel;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 问卷内容
     */
    private String questionnaire;

    /**
     * 正面整体图
     */
    private String frontImages;

    /**
     * 表盘背面图
     */
    private String backImages;

    /**
     * 表扣细节图
     */
    private String claspImages;

    /**
     * 表带细节图
     */
    private String strapImages;

    /**
     * 瑕疵图
     */
    private List<String> flawImages;

    /**
     * 表镜尺寸
     */
    private String watchSize;

    /**
     * 机芯类型
     */
    private String movementType;

    /**
     * 附件
     */
    private String attachment;

    /**
     * 使用情况
     */
    private String usageStatus;

    /**
     * 下单时间
     */
    private String placeOrderTime;

    /**
     * 闲鱼订单id
     */
    private String bizOrderId;

    /**
     * 估价价格
     */
    private String apprizeAmount;

    /**
     * 闲鱼订单状态
     */
    private Integer orderStatus;

    /**
     * 取件类型 1：顺风 2：上门取件
     */
    private Integer shipType;

    /**
     * 卖家昵称
     */
    private String sellerNick;

    /**
     * 卖家真实姓名
     */
    private String sellerRealName;

    /**
     * 卖家电话
     */
    private String sellerPhone;

    /**
     * 卖家支付宝用户id
     */
    private String sellerAlipayUserId;

    /**
     * 卖家支付宝帐号
     */
    private String sellerAlipayAccount;

    /**
     * 取件时间
     */
    private String shipTime;

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
     * 街道
     */
    private String country;

    /**
     * 卖家地址
     */
    private String sellerAddress;

    /**
     * 物流单号
     */
    private String expressNumber;

    /**
     * 收货面单图
     */
    private String faceImages;

    /**
     * 收货实物图
     */
    private List<String> goodsImages;

    /**
     * 退货物流单号
     */
    private String refundExpressNumber;

    /**
     * 退货面单图
     */
    private String refundFaceImages;

    /**
     * 退货实物图
     */
    private List<String> refundGoodsImages;

    /**
     * 质检报告
     */
    private String qtReport;

    /**
     * 质检成色
     */
    private String qtFineness;

    /**
     * 质检编码
     */
    private String qtCode;

    /**
     * 质检外观检测
     */
    private List<String> qtFacade;

    /**
     * 质检细节检测
     */
    private List<String> qtDetail;

    /**
     * 质检附件
     */
    private List<String> qtAttachment;

    /**
     * 最终的估价价格，成交金额
     */
    private String finalApprizeAmount;

    /**
     * 打款金额
     */
    private String paymentPrice;

    /**
     * 打款流水号
     */
    private String paymentNo;

    /**
     * 打款时间
     */
    private String paymentTime;

    /**
     * 取消原因
     */
    private String closeReason;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建时间
     */
    private String createdTime;
    /**
     * 创建人
     */
    private String createdBy;

}