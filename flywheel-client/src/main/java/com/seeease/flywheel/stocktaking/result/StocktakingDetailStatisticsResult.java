package com.seeease.flywheel.stocktaking.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ Description   :
 * @ Author        :  西门 游
 * @ CreateDate    :  8/10/23
 * @ Version       :  1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakingDetailStatisticsResult implements Serializable {

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

}
