package com.seeease.flywheel.serve.financial.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 应收记录与流水关系表
 *
 * @TableName account_rece_state_rel
 */
@Data
@TableName(value = "account_rece_state_rel", autoResultMap = true)
public class AccountReceStateRel extends BaseDomain {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 确认收款表主键id
     */
    private Integer accountReceiptConfirmId;

    /**
     * 流水表主键id
     */
    private Integer financialStatementId;
    /**
     * 流水表交易编码
     */
    private String financialStatementSerialNo;

    /**
     * 流水单剩余金额
     */
    private BigDecimal fundsReceived;

    /**
     * 流水单使用金额
     */
    private BigDecimal fundsUsed;

    /**
     * 是否匹配核销 0否 1是
     */
    private WhetherEnum whetherMatching;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}