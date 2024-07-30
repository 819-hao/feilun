package com.seeease.flywheel.serve.financial.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 付款/收款单 与商品 关联
 *
 * @TableName account_stock_relation
 */
@TableName(value = "account_stock_relation", autoResultMap = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountStockRelation extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 付款单id
     */
    private Integer afpId;

    /**
     * 确认打款单id
     */
    private Integer arcId;

    /**
     *
     */
    private Integer stockId;

    /**
     * 销售单的单号
     */
    private String originSerialNo;

    /**
     * 销售价/成交价
     */
    private BigDecimal originPrice;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}