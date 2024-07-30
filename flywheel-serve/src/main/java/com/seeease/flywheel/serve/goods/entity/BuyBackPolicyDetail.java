package com.seeease.flywheel.serve.goods.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 回购政策详情表
 * @TableName buy_back_policy_detail
 */
@TableName(value ="buy_back_policy_detail")
@Data
public class BuyBackPolicyDetail implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer bbpId;

    /**
     * 回购时间
     */
    private String buyBackTime;

    /**
     * 折扣
     */
    private BigDecimal discount;

    /**
     * 
     */
    private Integer deleted;

    /**
     * 0 代表小于等于  1代表大于
     */
    private Integer type;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}