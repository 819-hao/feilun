package com.seeease.flywheel.pricing.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2024/2/23
 */
@Data
public class ApplyPricingListResult implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 申请批次号
     */
    private String serialNo;

    /**
     * 商品id
     */
    private Integer stockId;

    /**
     * 审核状态
     */
    private Integer applyStatus;

    /**
     * 申请门店id
     */
    private Integer applyShopId;

    /**
     * 申请门店名称
     */
    private String applyShopName;

    /**
     * 申请原因
     */
    private String applyReason;

    /**
     * 建议TOC价格
     */
    private BigDecimal suggestedTocPrice;

    /**
     * 审核人
     */
    private String auditor;

    /**
     * 审核时间
     */
    private String approvedTime;

    /**
     * 核定TOC价格
     */
    private BigDecimal approvedTocPrice;

    /**
     * 核定吊牌价
     */
    private BigDecimal approvedTagPrice;

    /**
     * 拒绝原因
     */
    private String rejectionReason;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 主图
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
     * 总价
     */
    private BigDecimal totalPrice;

    /**
     * tob价
     */
    private BigDecimal tobPrice;

    /**
     * toc价
     */
    private BigDecimal tocPrice;

    /**
     * 吊牌价
     */
    private BigDecimal tagPrice;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 成色
     */
    private String finess;

    /**
     * 附件
     */
    private String attachment;

    /**
     * 总库龄
     */
    private Integer totalStorageAge;

    /**
     * 库存来源
     */
    private Integer stockSrc;

    private String createdBy;

    private String createdTime;
}
