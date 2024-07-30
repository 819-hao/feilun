package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

/**
 * 报废单
 * @TableName bill_stock_scrap
 */
@TableName(value = "bill_stock_scrap")
@Data
public class BillStockScrap extends BaseDomain {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String serialNo;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 报废原因
     */
    private String scrapReason;

    /**
     * 
     */
    private String batchImagUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}