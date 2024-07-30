package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 汇付交易流水
 *
 * @TableName hf_trade_flow
 */
@TableName(value = "hf_trade_flow")
@Data
public class HfTradeFlow extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 扫码交易单号(支付宝、微信交易号)
     */
    private String tradeNo;

    /**
     * 交易金额
     */
    private BigDecimal ordAmt;

    /**
     * 支付方式
     */
    private String mobilePayType;

    /**
     * 商户号
     */
    private String memberId;

    /**
     * 商户名称
     */
    private String merName;

    /**
     * 交易时间
     */
    private Date transDateTime;

    /**
     * 终端号
     */
    private String deviceId;

    /**
     *
     */
    private String termOrdId;

    private String ordId;

    private Integer flowState;



    /**
     * 备注
     */
    private String remarks;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}