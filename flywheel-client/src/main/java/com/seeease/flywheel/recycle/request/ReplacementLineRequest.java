package com.seeease.flywheel.recycle.request;

import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 置换行数据信息保存
 * @Auther Gilbert
 * @Date 2023/9/7 16:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain=true)
public class ReplacementLineRequest implements Serializable {
    /**
     * 第三方子订单
     */
    private String subOrderCode;
    /**
     * 抖音抽检码
     */
    private String spotCheckCode;

    /**
     *
     */
    private Integer scInfoId;
    /**
     * 预售型号
     */
    private String model;

    /**
     * 型号编码
     */
    private String modelCode;

    /**
     * 预售表身号
     */
    private String stockSn;

    /**
     * 型号id
     */
    private Integer goodsId;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 成本价
     */
    private BigDecimal consignmentPrice;

    /**
     * 成交价
     */
    private BigDecimal clinchPrice;

    /**
     * 经营权
     */
    private Integer rightOfManagement;

    /**
     *
     */
    private BigDecimal gmvPerformance;

    /**
     * 活动寄售价格
     */
    private BigDecimal promotionConsignmentPrice;

    /**
     * 表带类型
     */
    private String strapMaterial;

    private String finess;

    /**
     * 表节
     */
    private String watchSection;
    /**
     * 是否维修 是否收取表带更换费
     */
    private Integer whetherFix;
    /**
     * 成交价格大于两万 1200 小于是600
     */
    private BigDecimal strapReplacementPrice;

    /**
     * 是否回购 1:是
     */
    private Integer isCounterPurchase;

    /**
     * 是否有回顾政策 1:是
     */
    private Integer isRepurchasePolicy;

    /**
     * 回购政策快照
     */
    private List<BuyBackPolicyInfo> buyBackPolicy;

    /**
     * 回购url
     */
    private String repurchasePolicyUrl;

    /**
     * 同行寄售预计成交价
     */
    private BigDecimal preClinchPrice;

    /**
     * tob价格
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
    private BigDecimal totalPrice;
    /**
     * 质保年限
     */
    private Integer warrantyPeriod;
    /**
     * 公价
     */
    private BigDecimal pricePub;

    private Integer locationId;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 是否允许低价销售(默认0:不允许 2:允许低于B价销售 3:允许低于C价销售)
     */
    private Integer isUnderselling;

    /**
     * 锁库门店
     */
    private Integer lockDemand;

    /**
     * 滞后确认表身号
     */
    private boolean delayStockSn;
}
