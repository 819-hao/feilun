package com.seeease.flywheel.sale.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
public class SaleOrderLine implements Serializable {
    /**
     * 库存id
     */
    private Integer stockId;


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
     * 机芯类型
     */
    private String movement;

    /**
     * 成色
     */
    private String finess;


    /**
     * 附件
     */
    private String attachment;

    /**
     * 公价
     */
    private BigDecimal pricePub;

    /**
     * toc价
     */
    private BigDecimal tocPrice;

    /**
     * 吊牌价
     */
    private BigDecimal tagPrice;

    /**
     * 成交价
     */
    private BigDecimal clinchPrice;

    /**
     * 成交价格大于两万 1200 小于是600
     */
    private BigDecimal strapReplacementPrice;

    /**
     * 是否有回购政策
     */
    private boolean whitBuyBackPolicy;
    /**
     * 回购政策
     */
    List<BuyBackPolicyInfo> buyBackPolicyList;

    /**
     * 回购url
     */
    private String repurchasePolicyUrl;

    /**
     * 快递单号
     */
    private String expressNumber;

}
