package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialInvoiceStockQueryImportRequest extends ImportRequest<FinancialInvoiceStockQueryImportRequest.ImportDto> {

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
        @ExcelReaderProperty(name = "商品归属")
        private String belongName;
    }
}
