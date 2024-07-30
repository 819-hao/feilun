package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.goods.enums.StockPromotionEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName stock_promotion
 */
@TableName(value ="stock_promotion_history")
@Data
@Accessors(chain = true)
public class StockPromotionHistory extends BaseDomain {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer stockId;
    /**
     * 
     */
    private String stockSn;

    /**
     * 活动价格
     */
    private BigDecimal promotionPrice;

    /**
     * 活动寄售价格
     */
    private BigDecimal promotionConsignmentPrice;

    /**
     * 寄售比例
     */
    private BigDecimal consignmentRatio;

    private Date startTime;

    private Date endTime;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}