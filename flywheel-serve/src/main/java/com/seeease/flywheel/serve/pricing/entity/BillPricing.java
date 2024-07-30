package com.seeease.flywheel.serve.pricing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.pricing.enums.PricingStateEnum;
import com.seeease.flywheel.serve.purchase.enums.SalesPriorityEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订价记录
 *
 * @TableName bill_pricing
 */
@TableName(value = "bill_pricing")
@Data
public class BillPricing extends BaseDomain implements TransitionStateEntity, Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 编码
     */
    private String serialNo;

    /**
     * 定价状态
     */
    @TransitionState
    private PricingStateEnum pricingState;

    private Integer stockId;

    /**
     * 来源单据
     */
    private String originSerialNo;

    /**
     * 仓库单据
     */
    private String storeWorkSerialNo;

    /**
     * 加价
     */
    private BigDecimal addPrice;

    /**
     * 总价格
     */
    private BigDecimal allPrice;

    /**
     * tob 毛利率
     */
    private BigDecimal bMargin;

    /**
     * toc毛利率
     */
    private BigDecimal cMargin;

    /**
     * tob价格
     */
    private BigDecimal bPrice;

    /**
     * toc价格
     */
    private BigDecimal cPrice;

    /**
     * 活动价
     */
    private BigDecimal aPrice;

    /**
     * 吊牌价
     */
    private BigDecimal tPrice;

    /**
     * 库存来源
     */
    private BusinessBillTypeEnum pricingSource;

    /**
     * 采购价格
     */
    private BigDecimal purchasePrice;

    /**
     * 维修记录id
     */
    private BigDecimal fixPrice;

    /**
     * 维修天数
     */
    private Integer fixDay;

    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;

    /**
     * 商品级别
     */
    private String goodsLevel;

    private Integer autoPrice;

    private Boolean again;


    private BigDecimal wuyuBuyBackPrice;

    private Integer demandId;

    /**
     * 销售等级
     */
    private SalesPriorityEnum salesPriority;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;
}