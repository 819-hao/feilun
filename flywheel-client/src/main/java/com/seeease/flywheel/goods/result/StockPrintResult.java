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
 * @Date create in 2023/6/2 14:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPrintResult implements Serializable {

    /**
     * 库存id
     */
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
     * 表身号
     */

    private String stockSn;

    /**
     * 附件
     */

    private String attachment;

    /**
     * 公价
     */

    private BigDecimal pricePub;

    /**
     * 采购来源
     */
    private Integer purchaseSource;

    /**
     *
     */
    private String createdBy;

    private Integer goodsId;

    private String wno;

    private BigDecimal tagPrice;

    private Integer flowGrade;

    private String finess;
}
