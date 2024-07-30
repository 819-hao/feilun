package com.seeease.flywheel.serve.financial.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @TableName financial_invoice_reverse
 */
@TableName(value = "financial_invoice_reverse", autoResultMap = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FinancialInvoiceReverse extends BaseDomain {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private Integer stockId;

    /**
     *
     */
    private BigDecimal originPrice;

    /**
     *
     */
    private String originSerialNo;

    private Integer lineId;
    /**
     * 开票号码
     */
    private String invoiceNumber;

    /**
     * 开票主体
     */
    private Integer invoiceSubject;

    /**
     *
     */
    private Integer state;

    /**
     *
     */
    private Integer fiId;

    /**
     *
     */
    private String fiSerialNo;

    private Integer shopId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}