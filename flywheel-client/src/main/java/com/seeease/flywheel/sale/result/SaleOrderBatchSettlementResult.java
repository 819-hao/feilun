package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderBatchSettlementResult implements Serializable {

    private List<SaleOrderBatchSettlementLine> list;

    @Data
    public static class SaleOrderBatchSettlementLine implements Serializable {
        /**
         * 库存id
         */
        private Integer stockId;

        /**
         * 单据编号
         */
        private String serialNo;

    }

}
