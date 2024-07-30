package com.seeease.flywheel.web.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 抖音商品关系
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DouYinProductMappingVO implements Serializable {
    /**
     * 主键id
     */
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
    private String syncTime;
}