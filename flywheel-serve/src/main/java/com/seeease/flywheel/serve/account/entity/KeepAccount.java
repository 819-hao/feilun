package com.seeease.flywheel.serve.account.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.account.enums.PageTypeEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 记账
 *
 * @TableName keep_account
 */
@TableName(value = "keep_account")
@Data
public class KeepAccount extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 页面类型
     */
    private PageTypeEnum pageType;

    /**
     * 记账分类
     */
    private String accountGroup;

    /**
     * 记账类型
     */
    private String accountType;

    /**
     * 记账日期
     */
    private String completeDate;

    /**
     * 企业名称
     */
    private String companyName;
    /**
     *
     */
    private String shopName;

    /**
     * 金额
     */
    private BigDecimal money;

    private BigDecimal peopleNumber;

    /**
     * 摘要
     */
    private String digest;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}