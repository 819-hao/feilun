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
public class StocktakingStoreListResult implements Serializable {

    /**
     * 仓库列表
     */
    private List<StocktakingStoreDTO> storeList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StocktakingStoreDTO implements Serializable {
        /**
         * 门店id
         */
        private Integer shopId;

        /**
         * 仓库id
         */
        private Integer storeId;
        /**
         * 仓库名称
         */
        private String storeName;
    }
}
