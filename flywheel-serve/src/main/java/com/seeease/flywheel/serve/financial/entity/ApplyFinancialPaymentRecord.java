package com.seeease.flywheel.serve.financial.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.util.Date;


/**
 * @TableName apply_financial_payment_record
 */
@TableName(value = "apply_financial_payment_record", autoResultMap = true)
@Data
public class ApplyFinancialPaymentRecord extends BaseDomain {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 申请财务打款id
     */
    private Integer applyFinancialPaymentId;

    /**
     *
     */
    private ApplyFinancialPaymentStateEnum state;

    /**
     * 拒绝原因
     */
    private String result;

    private String applicant;

    private Date applicantTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}