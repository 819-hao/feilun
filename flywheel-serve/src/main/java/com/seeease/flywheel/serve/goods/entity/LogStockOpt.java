package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 销售表
 * @TableName log_stock_opt
 */
@TableName(value ="log_stock_opt")
@Data
public class LogStockOpt extends BaseDomain implements Serializable {
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
     * 类型 1表身号 2附件
     */
    private Integer optMode;


    /**
     * 期初表身号
     */
    private String openingStockSn;

    /**
     * 期末表身号
     */
    private String closingStockSn;

    /**
     * 期初表附件
     */
    private String openingStockAttachment;

    /**
     * 期末表附件
     */
    private String closingStockAttachment;

    /**
     * 期初表其他
     */
    private String openingStockOther;

    /**
     * 期末表其他
     */
    private String closingStockOther;

    /**
     * 当前操作人所在门店
     */
    private Integer shopId;

    /**
     * 备注
     */
    private String remarks;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}