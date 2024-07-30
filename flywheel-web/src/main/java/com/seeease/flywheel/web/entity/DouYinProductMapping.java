package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 抖音商品关系
 * @TableName douyin_product_mapping
 */
@TableName(value ="douyin_product_mapping")
@Data
public class DouYinProductMapping extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 抖音门店id
     */
    private Integer douYinShopId;

    /**
     * 门店id
     */
    private Integer shopId;

    /**
     * 抖音商品id
     */
    private String douYinProductId;

    /**
     * 抖音sku_id
     */
    private String douYinSkuId;

    /**
     * 型号编码
     */
    private String modelCode;

    /**
     * 型号id
     */
    private Integer goodsId;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 同步时间
     */
    private Date syncTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}