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
@TableName(value ="log_price_opt")
@Data
public class LogPriceOpt extends BaseDomain implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 处理类型
     */
    private Integer modeType;

    /**
     * 处理消息文案
     */
    private String modeMsg;

    /**
     * 寄售价
     */
    private BigDecimal consignPrice;

    /**
     * b价
     */
    private BigDecimal bPrice;

    /**
     * c价
     */
    private BigDecimal cPrice;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}