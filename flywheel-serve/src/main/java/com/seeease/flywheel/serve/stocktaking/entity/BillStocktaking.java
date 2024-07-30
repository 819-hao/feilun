package com.seeease.flywheel.serve.stocktaking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.stocktaking.enums.StocktakingSourceEnum;
import com.seeease.flywheel.serve.stocktaking.enums.StocktakingStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import java.io.Serializable;
import lombok.Data;

/**
 * 盘点单
 * @TableName bill_stocktaking
 */
@TableName(value ="bill_stocktaking")
@Data
public class BillStocktaking extends BaseDomain implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 盘点仓库id
     */
    private Integer storeId;

    /**
     * 盘点单号
     */
    private String serialNo;

    /**
     * 盘点状态:1-完成
     */
    private StocktakingStateEnum stocktakingState;

    /**
     * 盘点来源：1-rfid
     */
    private StocktakingSourceEnum stocktakingSource;

    /**
     * 盘点系统商品数量
     */
    private Integer quantity;

    /**
     * 匹配数量
     */
    private Integer matchQuantity;

    /**
     * 盘盈数量
     */
    private Integer profitQuantity;

    /**
     * 盘亏数量
     */
    private Integer lossQuantity;

    /**
     * 备注说明
     */
    private String remarks;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}