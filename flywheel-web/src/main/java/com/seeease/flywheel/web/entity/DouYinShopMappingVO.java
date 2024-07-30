package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 抖音门店映射表
 *
 * @TableName douyin_shop_mapping
 */
@Data
public class DouYinShopMappingVO implements Serializable {

    /**
     * 飞轮门店id
     */
    private Integer shopId;

    /**
     * 订单创建人企业微信用户id
     */
    private String orderOwner;

    /**
     * 通知机器人
     */
    private String robot;

    /**
     * 门店id
     */
    private String shopName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}