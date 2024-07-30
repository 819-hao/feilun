package com.seeease.flywheel.stocktaking.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/6/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakingSubmitRequest implements Serializable {

    /**
     * 盘点单号
     */
    String serialNo;
    /**
     * 盘点库存数量
     */
    private Integer quantity;
    /**
     * 无误商品
     */
    private List<String> matchList;
    /**
     * 盘盈商品
     */
    private List<String> profitList;
    /**
     * 盘亏商品
     */
    private List<String> lossList;

    /**
     * 备注说明
     */
    private String remarks;

    /**
     * 盘点仓库id
     */
    private Integer storeId;
}
