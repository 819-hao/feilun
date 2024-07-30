package com.seeease.flywheel.goods.request;

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
public class StockPurchaseUpdateImportRequest extends ImportRequest<StockPurchaseUpdateImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "表身号")
        private String stockSn;
        @ExcelReaderProperty(name = "采购价")
        private BigDecimal purchasePrice;
        @ExcelReaderProperty(name = "成交价")
        private BigDecimal clinchPrice;
    }
}
