package com.seeease.flywheel.storework.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkOutStorageSupplyStockRequest implements Serializable {

    /**
     * 源头单据单号
     */
    private String originSerialNo;
    /**
     * 商品行信息
     */
    private List<OutStorageSupplyStockDto> lineList;

    /**
     * 场景
     */
    private SupplyScenario scenario;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutStorageSupplyStockDto implements Serializable {

        /**
         * 作业id
         */
        private Integer id;

        /**
         * 表身号
         */
        private String stockSn;

    }

    public enum SupplyScenario {
        /**
         * 调拨场景
         */
        ALLOCATE,
        /**
         * 销售场景
         */
        SALE
    }
}
