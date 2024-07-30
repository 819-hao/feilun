package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description 采购退货
 * @Date create in 2023/3/31 10:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseReturnStockQueryImportResult implements Serializable {

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
     * 商品id
     */
    private Integer stockId;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 采购价
     */
    private BigDecimal purchasePrice;

    /**
     * 采购退货价
     */
    private BigDecimal purchaseReturnPrice;

    /**
     * 备注
     */
    private String remark;

    /**
     * 附件文案
     */
    private String attachment;

    /**
     * 商品位置
     */
    private String locationName;

    private Integer locationId;

    /**
     * 采购主体
     */
    private String purchaseSubjectName;

    private Integer purchaseSubjectId;

    /**
     * 采购类型
     */
    private Integer purchaseType;
}
