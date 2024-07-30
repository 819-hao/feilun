package com.seeease.flywheel.serve.goods.entity;

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
 * 保卡管理
 * @TableName stock_guarantee_card_manage
 */
@TableName(value ="stock_guarantee_card_manage")
@Data
public class StockGuaranteeCardManage extends BaseDomain implements Serializable {
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
     * 保卡信息
     */
    private String cardInfo;
    /**
     * 成本
     */
    private BigDecimal cost;
    /**
     * 调拨单号
     */
    private String allocateNo;

    /**
     * 调拨状态：0-未调拨，1-已调拨
     */
    private Integer allocateState;
    /**
     * 是否编辑 0否 1是
     */
    private Integer whetherEdit;

    /**
     * 调出时间
     */
    private Date outTime;

    /**
     * 备注
     */
    private String remarks;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}