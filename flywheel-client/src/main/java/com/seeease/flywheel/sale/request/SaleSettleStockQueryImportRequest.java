package com.seeease.flywheel.sale.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleSettleStockQueryImportRequest extends ImportRequest<SaleSettleStockQueryImportRequest.ImportDto> {

    /**
     * 供应商id
     */
    private Integer customerId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "表身号")
        private String stockSn;
        @ExcelReaderProperty(name = "成交价")
        private BigDecimal clinchPrice;
    }
}
