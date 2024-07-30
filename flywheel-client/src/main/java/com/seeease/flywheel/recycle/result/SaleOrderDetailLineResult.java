package com.seeease.flywheel.recycle.result;

import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Auther Gilbert
 * @Date 2023/9/8 19:02
 */
@Data
@Accessors(chain=true)
public class SaleOrderDetailLineResult implements Serializable {
    /**
     * 表id
     */
    private Integer stockId;
    /**
     * 表身号
     */
    private String stockSn;
    /**
     * 销售单号
     */
    private String serialNo;
    /**
     * 成色
     */
    private String finess;

    /**
     * 单行状态
     */
    private Integer saleLineState;

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

    /**
     * 型号id
     */
    private Integer goodsId;

    /**
     * 附件列表
     */
    private String attachment;

    /**
     * 经营权
     */
    private Integer rightOfManagement;
    private String rightOfManagementName;
    /**
     * 分成比例 null 代表不分成
     */
    private BigDecimal proportion;
    private BigDecimal totalPrice;

    /**
     *
     */
    private BigDecimal gmvPerformance;
    /**
     * 活动价格
     */
    private BigDecimal promotionConsignmentPrice;
    /**
     * 表带类型
     */
    private String strapMaterial;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 表节
     */
    private String watchSection;

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

    /**
     * 成本价
     */
    private BigDecimal consignmentPrice;

    /**
     * 成交价
     */
    private BigDecimal clinchPrice;

    /**
     * 状态
     */
    private Integer state;

    private String locationName;
    private Integer locationId;

    /**
     * 备注
     */
    private String remarks;
    private String buyBackPolicy;
    private BigDecimal marginPrice;//差额
    private Integer balanceDirection;//差额类型
    private List<BuyBackPolicyInfo> list;

    private Integer saleId;

    /**
     * 商品编码
     */
    private String wno;

    /**
     * 机芯类型
     */
    private String movement;

    /**
     * 寄售完成时间
     */
    private String consignmentSaleFinishTime;
    private String consignmentSettlementOperator;

    /**
     * 第三方子订单
     */
    private String subOrderCode;

    /**
     * 质保年限
     */
    private Integer warrantyPeriod;

    /**
     * 型号主图
     */
    private String image;
    /**
     * 创建时间(商城要用创建时间来默认显示物流)
     */
    private Date createdTime;
}
