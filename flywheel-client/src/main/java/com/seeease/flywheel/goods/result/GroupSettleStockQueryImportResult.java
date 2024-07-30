package com.seeease.flywheel.goods.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 集采模版 res
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupSettleStockQueryImportResult implements Serializable {
    /**
     * 库存id
     */
    private Integer stockId;
    /**
     * 采购商品
     */
    private Integer goodsId;
    /**
     * 商品编号：XYW+8位阿拉伯数字
     */
    private String wno;
    /**
     * 成色
     */
    private String finess;
    /**
     * 总价
     */
    private BigDecimal totalPrice;
    /**
     * 附件
     */
    private String attachment;
    /**
     * 表身号
     */
    private String stockSn;
    /**
     * store_management 所在地 ID 急售商品所在地
     */
    private Integer locationId;
    /**
     * 商品位置
     */
    private String locationName;
    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;
    /**
     * 经营权
     */
    private Integer rightOfManagement;

    /**
     * 经营权名
     */
    private String rightOfManagementName;


    /**
     * 品牌id
     */
    private Integer brandId;
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


    private String purchaseSubjectName;

    private Integer purchaseSubjectId;

    /**
     * (不要为零ok？)总部采购价格
     */
    private BigDecimal purchasePrice;

    private Integer purchaseType;

    private String remark;

    private String serialNo;

    private BigDecimal tobPrice;
    private BigDecimal tocPrice;

    private Integer ccId;

    private Integer isUnderselling;
    private Integer sourceSubjectId;
    private String warrantyDate;
    /**
     * 关联单号
     */
    private String originSerialNo;

    private Integer stockStatus;

    /**
     * 型号主图
     */
    private String image;

    /**
     * 库存来源
     */
    private Integer stockSrc;

    /**
     * 成交价
     */
    private BigDecimal settlePrice;

}
