package com.seeease.flywheel.pricing.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@Data
public class PricingCompletedResult implements Serializable {

    private String serialNo;

    /**
     * 定价商品
     */
    private Integer stockId;

    private PriceMessage priceMessage;

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

        /**
         * 拼接字符串
         */
        private String lineMsg;
    }
}
