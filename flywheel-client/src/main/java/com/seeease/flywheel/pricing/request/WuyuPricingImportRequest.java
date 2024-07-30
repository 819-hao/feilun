package com.seeease.flywheel.pricing.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.flywheel.sale.request.SaleReturnStockQueryImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@Data
@Builder
@NoArgsConstructor
public class WuyuPricingImportRequest extends ImportRequest<WuyuPricingImportRequest.ImportDto> {



    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "表身号")
        private String stockSn;
        @ExcelReaderProperty(name = "修改原因")
        private String reason;
        @ExcelReaderProperty(name = "新采购价")
        private String newPurchasePrice;
//        @ExcelReaderProperty(name = "新物鱼供货价")
//        private String newWuyuPrice;
        @ExcelReaderProperty(name = "新兜底价")
        private String newWuyuBuyBackPrice;
    }


}
