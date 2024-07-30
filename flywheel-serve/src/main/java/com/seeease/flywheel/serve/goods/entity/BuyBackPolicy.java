package com.seeease.flywheel.serve.goods.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 回购政策表
 * @TableName buy_back_policy
 */
@TableName(value ="buy_back_policy")
@Data
public class BuyBackPolicy implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 成色
     */
    private String fineness;

    /**
     * 价格分界线
     */
    private BigDecimal priceThreshold;

    /**
     * 置换折扣
     */
    private BigDecimal replacementDiscounts;

    /**
     * 供应商们
     */
    private String suppliers;

    /**
     * 保卡时间
     */
    private String insuranceCardTime;

    /**
     * 库存来源
     */
    private String repertoryResource;

    /**
     * 最后修改人
     */
    private String modifier;

    /**
     * 
     */
    private Integer modifierId;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 回购政策类型 0 新表  1二手表
     */
    private Integer type;

    /**
     * 采购主体
     */
    private String procuringEntity;

    /**
     * 
     */
    private Integer totalCostToPublicPrice;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}