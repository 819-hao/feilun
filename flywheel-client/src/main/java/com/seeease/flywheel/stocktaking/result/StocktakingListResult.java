package com.seeease.flywheel.stocktaking.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Tiro
 * @date 2023/6/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakingListResult implements Serializable {

    /**
     * 盘点ID
     */
    private Integer id;

    /**
     * 盘点仓库id
     */
    private Integer storeId;

    /**
     * 盘点仓库
     */
    private String storeName;

    /**
     * 盘点单号
     */
    private String serialNo;

    /**
     * 盘点状态:1-完成
     */
    private Integer stocktakingState;

    /**
     * 盘点来源：1-rfid
     */
    private Integer stocktakingSource;

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

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private String createdTime;
}
