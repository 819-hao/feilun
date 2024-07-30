package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 抖音门店映射表
 *
 * @TableName douyin_shop_mapping
 */
@TableName(value = "douyin_shop_mapping")
@Data
public class DouYinShopMapping extends BaseDomain implements Serializable {
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
     * 达人id
     */
    private Long authorId;

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
     * 是否手动创建
     */
    private Integer manualCreation;

    /**
     * 是否需要解密
     */
    private Integer needDecrypt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}