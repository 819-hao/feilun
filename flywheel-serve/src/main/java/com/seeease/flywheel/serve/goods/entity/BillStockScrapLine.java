package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 报废单详情
 * @TableName bill_stock_scrap_line
 */
@TableName(value = "bill_stock_scrap_line")
@Data
public class BillStockScrapLine extends BaseDomain {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer scrapId;

    /**
     * 
     */
    private Integer stockId;

    /**
     * purchase_subject id 结算主体 买断归属
     */
    private Integer belongId;

    /**
     * store_management 所在地 ID 急售商品所在地
     */
    private Integer locationId;

    /**
     * 成色 1。N级/全新、2.S级/99新未使用、3.SA级/98新未使用、4.A级/95新、5.AB级/9新、6.8新及以下
     */
    private String finess;

    /**
     * (不要为零ok？)总部采购价格 
     */
    private BigDecimal purchasePrice;

    /**
     * 总价
     */
    private BigDecimal totalPrice;

    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;

    /**
     * 附件
     */
    private String attachment;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}