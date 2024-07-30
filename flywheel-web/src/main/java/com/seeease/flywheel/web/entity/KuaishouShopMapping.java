package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 快手门店映射表
 * @TableName kuaishou_shop_mapping
 */
@TableName(value ="kuaishou_shop_mapping")
@Data
public class KuaishouShopMapping extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 快手门店id
     */
    private Long kuaiShouShopId;

    /**
     * 飞轮门店id
     */
    private Integer shopId;

    /**
     * 订单创建人企业微信用户id
     */
    private String orderOwner;

    /**
     * 达人id
     */
    private Long authorId;

    /**
     * 
     */
    private Integer manualCreation;

    /**
     * 是否需要解密
     */
    private Integer needDecrypt;

    /**
     * 
     */
    private String robot;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}