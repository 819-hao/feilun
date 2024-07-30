package com.seeease.flywheel.goods.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Data
public class StockInfo implements Serializable {
    /**
     * 库存id
     */
    private Integer stockId;
    /**
     * 采购商品
     */
    private Integer goodsId;
    /**
     * 品牌
     */
    private String brandName;
    /**
     * 系列
     */
    private String seriesName;
    /**
     * 型号
     */
    private String model;
    /**
     * 商品编号：XYW+8位阿拉伯数字
     */
    private String wno;
    /**
     * 成色
     */
    private String finess;

    /**
     * 附件
     */
    private String attachment;
    /**
     * 表身号
     */
    private String stockSn;
    private String watchSize;
    private String boxNumber;

    /**
     * 品牌id
     */
    private Integer brandId;

    /**
     * 公价
     */
    private BigDecimal pricePub;
    private BigDecimal tagPrice;

    private Integer stockStatus;

}
