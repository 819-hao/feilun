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
 * @author Tiro
 * @date 2023/3/30
 */

@Data
@Builder
@NoArgsConstructor
public class SaleStockQueryImportRequest extends ImportRequest<SaleStockQueryImportRequest.ImportDto> {


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "表身号")
        private String stockSn;

        @ExcelReaderProperty(name = "成交价")
        private BigDecimal clinchPrice;

        @ExcelReaderProperty(name = "同行寄售价")
        private BigDecimal preClinchPrice;

        @ExcelReaderProperty(name = "备注")
        private String remarks;

    }
}
