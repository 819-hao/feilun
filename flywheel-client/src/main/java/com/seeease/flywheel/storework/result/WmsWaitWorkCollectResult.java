package com.seeease.flywheel.storework.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/9/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WmsWaitWorkCollectResult implements Serializable {

    /**
     * 源头单据单号
     */
    private List<String> originSerialNoList;

    /**
     * 受限收集
     */
    private List<WmsWorkCapacityResult> restrictedCollect;


    @Data
    public static class WmsWorkCapacityResult implements Serializable {

        /**
         * 型号
         */
        private String model;

        /**
         * 已经存在的数量
         */
        private Integer inWorkQuantity;

        /**
         * 库存数量
         */
        private Integer stockQuantity;
    }

}
