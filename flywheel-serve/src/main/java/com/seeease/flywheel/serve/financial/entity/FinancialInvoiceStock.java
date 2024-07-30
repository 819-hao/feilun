package com.seeease.flywheel.serve.financial.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName financial_invoice_stock
 */
@TableName(value = "financial_invoice_stock", autoResultMap = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FinancialInvoiceStock extends BaseDomain {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 开票id
     */
    private Integer financialInvoiceId;

    /**
     *
     */
    private Integer stockId;
    private Integer lineId;
    private Integer forwardFiId;
    private WhetherEnum direction;

    /**
     *
     */
    private BigDecimal originPrice;

    /**
     *
     */
    private String originSerialNo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}