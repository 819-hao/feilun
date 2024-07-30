package com.seeease.flywheel.purchase.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author trio
 * @date 2023/1/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseListResult implements Serializable {
    /**
     * 采购id
     */
    private Integer id;

    /**
     * 采购类型
     */
    private Integer purchaseType;

    /**
     * 采购数量
     */
    private Integer purchaseNumber;

    /**
     * 采购方式
     */
    private Integer purchaseMode;

    /**
     * 支付方式
     */
    private Integer paymentMethod;

    /**
     * 订金百分比
     */
    private BigDecimal depositPercentage;

    /**
     * 采购单号
     */
    private String serialNo;

    /**
     * 客户
     */
    private String customerName;

    /**
     * 总采购成本
     */
    private BigDecimal totalPurchasePrice;

    /**
     * 采购单状态
     */
    private Integer purchaseState;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 图片
     */
    private List<String> imgList;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private String createdTime;

    @JsonIgnore
    private String applyPaymentSerialNo;

    @JsonIgnore
    private Integer purchaseId;

    @JsonIgnore
    private Integer customerContactId;
}
