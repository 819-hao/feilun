package com.seeease.flywheel.goods.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Data
public class GoodsBaseInfo implements Serializable {
    /**
     * 商品id
     */
    private Integer goodsId;
    /**
     * 型号官方图
     */
    private String image;
    /**
     * 品牌
     */
    private String brandName;
    /**
     * 系列
     */
    private String seriesName;
    private Integer seriesType;
    /**
     * 型号
     */
    private String model;
    /**
     * 公价
     */
    private BigDecimal pricePub;
    /**
     * 单表当前行情价
     */
    private BigDecimal currentPrice;
    /**
     * 20全表行情价
     */
    private BigDecimal twoZeroFullPrice;
    /**
     * 22年全表价
     */
    private BigDecimal twoTwoFullPrice;

    /**
     * 库存数量
     */
    private Integer stockQuantity;
    /**
     * 所在库存数量
     */
    private Integer stockQuantityByLocation;
}
