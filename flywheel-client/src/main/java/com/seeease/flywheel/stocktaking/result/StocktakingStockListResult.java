package com.seeease.flywheel.stocktaking.result;

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
public class StocktakingStockListResult implements Serializable {

    /**
     * 库存
     */
    private List<StocktakingStockDTO> stockList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StocktakingStockDTO implements Serializable {
        /**
         * 库存id
         */
        private Integer stockId;
        /**
         * 商品编码
         */
        private String wno;
        /**
         * 表身号
         */
        private String stockSn;
        /**
         * 速记码
         */
        private String shelvesSimplifiedCode;

    }
}
