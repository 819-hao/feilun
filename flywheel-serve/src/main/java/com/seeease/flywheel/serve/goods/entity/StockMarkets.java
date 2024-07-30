package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.type.JsonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 库存行情
 *
 * @TableName stock_markets
 */
@TableName(value = "stock_markets", autoResultMap = true)
@Data
public class StockMarkets extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private Integer stockId;

    /**
     * 行情价
     */
    private BigDecimal marketsPrice;

    /**
     * 行情图片
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> images;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}