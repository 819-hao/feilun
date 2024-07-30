package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 采购计划导入结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePlanImportResult implements Serializable {
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
     * 采购数量
     */
    private Integer planNumber;

}
