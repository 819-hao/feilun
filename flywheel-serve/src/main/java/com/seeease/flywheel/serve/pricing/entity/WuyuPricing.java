package com.seeease.flywheel.serve.pricing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 价格变动
 * @TableName log_price_opt
 */
@TableName(value ="wuyu_pricing")
@Data
public class WuyuPricing extends BaseDomain implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 表身号
     */
    private String stockSn;
    /**
     * 原因
     */
    private String reason;
    /**
     * 采购价
     */
    private BigDecimal purchasePrice;
    /**
     * 新采购价
     */
    private BigDecimal newPurchasePrice;
    /**
     * 物鱼供货价
     */
    private BigDecimal wuyuPrice;
    /**
     * 新物鱼供货价
     */
    private BigDecimal newWuyuPrice;
    /**
     * 兜底价
     */
    private BigDecimal wuyuBuyBackPrice;
    /**
     * 新物鱼兜底价
     */
    private BigDecimal newWuyuBuyBackPrice;


}