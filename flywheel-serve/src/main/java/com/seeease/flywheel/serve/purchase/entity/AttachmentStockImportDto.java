package com.seeease.flywheel.serve.purchase.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2023/9/26
 */
@Data
public class AttachmentStockImportDto implements Serializable {
    /**
     * 客户id
     */
    private Integer customerId;
    /**
     * 联系人id
     */
    private Integer customerContactsId;
    /**
     * 供应商
     */
    private String customerName;
    /**
     * 采购价
     */
    private BigDecimal purchasePrice;
    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;
    /**
     * 型号id
     */
    private Integer goodsId;
    /**
     * 表身号
     */
    private String stockSn;
    /**
     * 成色
     */
    private String finess;

    /**
     * 颜色
     */
    private String colour;

    /**
     * 材质
     */
    private String material;

    /**
     * 尺寸
     */
    private String size;

    /**
     * 适用腕表型号
     */
    private String gwModel;

    /**
     * 备注
     */
    private String remarks;
}
