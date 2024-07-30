package com.seeease.flywheel.goods.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/27 20:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPromotionListResult implements Serializable {
    private Integer id;

    /**
     *
     */
    private Integer stockId;

    /**
     *
     */
    private String stockSn;
    /**
     *
     */
    private Integer status;

    /**
     * 活动价格

     */
    private BigDecimal promotionPrice;

    /**
     * 活动寄售价格
     */
    private BigDecimal promotionConsignmentPrice;

    /**
     * 寄售比例
     */
    private BigDecimal consignmentRatio;

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

    private String finess;

    private Integer totalStorageAge;
    private Integer stockSrc;

    private String createdBy;
    private String createdTime;

    private String updatedBy;
    private String updatedTime;
    private String startTime;
    private String endTime;

    private String locationName;
    private String rightOfManagementName;
    private Integer stockStatus;
}
