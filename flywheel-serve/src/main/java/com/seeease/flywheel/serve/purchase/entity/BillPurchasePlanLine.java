package com.seeease.flywheel.serve.purchase.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

/**
 * @TableName bill_purchase_plan_line
 */
@TableName(value = "bill_purchase_plan_line", autoResultMap = true)
@Data
public class BillPurchasePlanLine extends BaseDomain {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private Integer planId;

    /**
     * 型号id
     */
    private Integer goodsId;

    /**
     * 采购数量
     */
    private Integer planNumber;

    /**
     * 公价
     */
    private BigDecimal pricePub;

    /**
     * 当前行情价
     */
    private BigDecimal currentPrice;

    /**
     * 20年行情价
     */
    private BigDecimal twoZeroFullPrice;

    /**
     * 22年行情价
     */
    private BigDecimal twoTwoFullPrice;
    /**
     * 建议采购价
     */
    private BigDecimal suggestedPurchasePrice;
    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 采购备注
     */
    private String remarks;

    private static final long serialVersionUID = 1L;
}