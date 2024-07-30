package com.seeease.flywheel.sale.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 */
@Data
public class SaleOrderBatchSettlementRequest implements Serializable {


    private Integer customerId;

    private List<SaleOrderBatchSettlementLine> list;

    @Data
    public static class SaleOrderBatchSettlementLine implements Serializable {
        /**
         * 库存id
         */
        private Integer stockId;
        /**
         * 成交价
         */
        private BigDecimal clinchPrice;

        /**
         * 同行寄售预计成交价
         */
        private BigDecimal preClinchPrice;

        private Integer saleId;
    }

}
