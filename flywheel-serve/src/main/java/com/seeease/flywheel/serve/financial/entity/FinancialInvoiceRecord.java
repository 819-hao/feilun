package com.seeease.flywheel.serve.financial.entity;

import java.util.Date;

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
 * 
 * @TableName financial_invoice_record
 */
@TableName(value = "financial_invoice_record", autoResultMap = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FinancialInvoiceRecord extends BaseDomain {
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
    private Integer state;

    /**
     * 申请人
     */
    private String applicant;

    /**
     * 申请时间
     */
    private Date applicantTime;

    /**
     * 处理结果
     */
    private String result;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}