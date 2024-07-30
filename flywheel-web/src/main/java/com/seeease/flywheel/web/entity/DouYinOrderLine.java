package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.type.JsonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 抖音订单行
 *
 * @TableName douyin_order_line
 */
@TableName(value = "douyin_order_line" ,autoResultMap = true)
@Data
public class DouYinOrderLine extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 抖音子订单号
     */
    private String douYinSubOrderId;

    /**
     * 行状态
     */
    private Long lineState;

    /**
     * 商品数量
     */
    private Long itemNum;

    /**
     * 产品id
     */
    private String productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 型号
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> goodsModel;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 商品金额
     */
    private BigDecimal orderAmount;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 直播主播id（达人）
     */
    private Long authorId;

    /**
     * 直播间id，有值则代表订单来自直播间
     */
    private Long roomId;

    /**
     * 视频id，有值则代表订单来自短视频
     */
    private String videoId;

    /**
     * 抖音抽检码
     */
    private String spotCheckCode;
    /**
     * 机构详细地址
     */
    private String scAddress;

    /**
     * 机构地址_街道
     */
    private String scStreet;

    /**
     * 机构地址_区
     */
    private String scDistrict;

    /**
     * 机构地址_省
     */
    private String scProvince;

    /**
     * 机构地址_市
     */
    private String scCity;

    /**
     * 机构联系电话
     */
    private String scPhone;

    /**
     * 质检机构名称
     */
    private String scName;

    /**
     * 质检机构ID
     */
    private String scId;
    /**
     *
     */
    private Integer scInfoId;
    /**
     * 型号唯一编码
     */
    private String modelCode;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}