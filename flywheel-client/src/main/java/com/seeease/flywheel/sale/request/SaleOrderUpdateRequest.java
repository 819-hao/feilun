package com.seeease.flywheel.sale.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/6 16:40
 */
@Data
public class SaleOrderUpdateRequest implements Serializable {

    /**
     * 单据详情
     */
    private List<BillSaleOrderLineDto> details;


    @Data
    public static class BillSaleOrderLineDto implements Serializable {

        private Integer id;

        private Integer isCounterPurchase;

        private Integer isRepurchasePolicy;

        private List<BuyBackPolicyMapper> buyBackPolicy;

        @Data
        public static class BuyBackPolicyMapper implements Serializable {
            /**
             * {"buyBackTime":"12","discount":9,"priceThreshold":20000,"replacementDiscounts":0.5,"type":1}
             */
            private Integer buyBackTime;

            /**
             *
             */
            private BigDecimal discount;

            private Integer priceThreshold;

            /**
             *
             */
            private BigDecimal replacementDiscounts;

            private Integer type;
        }
    }
}
