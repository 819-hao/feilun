package com.seeease.flywheel.goods.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2024/3/19 14:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogStockOptListResult implements Serializable {

    private Integer id;

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
     * 库存id
     */
    private Integer stockId;

    /**
     * 类型 1表身号 2附件 3编辑其他
     */
    private Integer optMode;


    /**
     * 期初表身号
     */
    private String openingStockSn;

    /**
     * 期末表身号
     */
    private String closingStockSn;

    /**
     * 期初表附件
     */
    private String openingStockAttachment;

    /**
     * 期末表附件
     */
    private String closingStockAttachment;

    /**
     * 期初表其他
     */
    private String openingStockOther;

    /**
     * 期末表其他
     */
    private String closingStockOther;

    /**
     * 当前操作人所在门店
     */
    private Integer shopId;

    /**
     * 备注
     */
    private String remarks;


    private String updatedBy;

    private String updatedTime;
}
