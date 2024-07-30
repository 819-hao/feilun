package com.seeease.flywheel.serve.sale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;
import com.seeease.flywheel.serve.sale.convert.BuyBackPolicyMapperTypeHandler;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @TableName bill_sale_line
 */
@TableName(value = "bill_sale_order_line", autoResultMap = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BillSaleOrderLine extends BaseDomain implements TransitionStateEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private Integer saleId;

    /**
     * 第三方子订单
     */
    private String subOrderCode;

    /**
     * 型号id
     */
    private Integer goodsId;

    /**
     * 库存id
     */
    private Integer stockId;
    /**
     * 质保年限
     */
    private Integer warrantyPeriod;

    /**
     * 成本价
     */
    private BigDecimal consignmentPrice;

    /**
     * 成交价
     */
    private BigDecimal clinchPrice;
    private BigDecimal totalPrice;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 单行状态
     */
    @TransitionState
    private SaleOrderLineStateEnum saleLineState;

    /**
     * 经营权
     */
    private Integer rightOfManagement;

    /**
     * 分成比例 null 代表不分成
     */
    private BigDecimal proportion;

    /**
     *
     */
    private BigDecimal gmvPerformance;

    /**
     * 行情价
     */
    private BigDecimal marketsPrice;

    /**
     * 活动寄售价
     */
    private BigDecimal promotionConsignmentPrice;

    /**
     * 活动价格
     */
    private BigDecimal promotionPrice;

    /**
     * 表带类型
     */
    private String strapMaterial;

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
     * 回购政策快照
     */
    @TableField(typeHandler = BuyBackPolicyMapperTypeHandler.class)
    private List<BuyBackPolicyMapper> buyBackPolicy;

    /**
     * 回购url
     */
    private String repurchasePolicyUrl;
    private String remarks;

    /**
     * 同行寄售预计成交价
     */
    private BigDecimal preClinchPrice;
    private BigDecimal marginPrice;//差额
    private Integer balanceDirection;//差额类型
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
     * 公价
     */
    private BigDecimal pricePub;

    /**
     * 寄售完成时间
     */
    private Date consignmentSaleFinishTime;
    /**
     * 抖音抽检码
     */
    private String spotCheckCode;

    private String consignmentSettlementOperator;
    /**
     *
     */
    private Integer scInfoId;

    /**
     * 是否开票 0没开票 1开票中 2已开票
     */
    private FinancialInvoiceStateEnum whetherInvoice;
    /**
     * 最新结算价
     */
    private BigDecimal newSettlePrice;

    /**
     * 销售发货时间
     */
    private Date deliveryTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;
}