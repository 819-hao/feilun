package com.seeease.flywheel.serve.stocktaking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.stocktaking.enums.StocktakingLineStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 盘点详情
 *
 * @TableName bill_stocktaking_line
 */
@TableName(value = "bill_stocktaking_line")
@Data
public class BillStocktakingLine extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 盘点单id
     */
    private Integer stocktakingId;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 商品编码
     */
    private String wno;

    /**
     * 盘点行状态:1-盘盈，2-盘亏
     */
    private StocktakingLineStateEnum stocktakingLineState;

    /**
     * 备注说明
     */
    private String remarks;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}