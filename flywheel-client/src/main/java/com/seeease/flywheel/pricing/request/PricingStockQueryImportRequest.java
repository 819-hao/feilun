package com.seeease.flywheel.pricing.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description 定价导入模板
 * @Date create in 2023/3/31 09:43
 */
@Data
@Builder
@NoArgsConstructor
public class PricingStockQueryImportRequest extends ImportRequest<PricingStockQueryImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "表身号")
        private String stockSn;

        @ExcelReaderProperty(name = "2B价")
        private BigDecimal bPrice;

        @ExcelReaderProperty(name = "2C价")
        private BigDecimal cPrice;

        @ExcelReaderProperty(name = "销售优先等级", readConverterExp = "1=仅B端销售,0=B/C可同销,2=仅C端销售")
        private String salesPriority;

        @ExcelReaderProperty(name = "自主经营类型")
        private String goodsLevel;

        @ExcelReaderProperty(name = "寄售价")
        private BigDecimal consignmentPrice;

        private String remarks;
    }
}
