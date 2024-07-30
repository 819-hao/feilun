package com.seeease.flywheel.storework.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/2/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkInStorageListResult implements Serializable {

    /**
     * 作业单id集合
     */
    private List<Integer> workIds;
    private List<StoreWorkCreateResult> storeWorkCreateResultList;

    private List<String> stockSnList;

    private List<PriceMessage> list;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceMessage implements Serializable {

        /**
         * 品牌
         */
        private String brandName;

        /**
         * 系列
         */
        private String seriesName;

        /**
         * 型号
         */
        private String model;

        private String lineMsg;
    }
}
