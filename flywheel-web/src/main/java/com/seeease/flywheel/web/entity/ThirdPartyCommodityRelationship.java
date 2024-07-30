package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName third_party_commodity_relationship
 */
@TableName(value ="third_party_commodity_relationship")
@Data
public class ThirdPartyCommodityRelationship implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 公司商品model值
     */
    private String goodsWatchModel;

    /**
     * 第三方商品id
     */
    private String thirdPartyGoodsId;

    /**
     * 是否有库存默认为0（0:有库存 1:无库存）
     */
    private Integer inventoryStatus;

    /**
     * 表身号还是型号（0:型号 1:表身号）
     */
    private Integer snModelStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}