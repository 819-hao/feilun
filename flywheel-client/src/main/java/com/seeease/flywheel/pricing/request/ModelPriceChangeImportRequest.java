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
public class ModelPriceChangeImportRequest extends ImportRequest<ModelPriceChangeImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "品牌")
        private String brandName;

        @ExcelReaderProperty(name = "系列")
        private String seriesName;

        @ExcelReaderProperty(name = "型号")
        private String model;
        /**
         * tob价
         */
        @ExcelReaderProperty(name = "B价")
        private BigDecimal tobPrice;

        /**
         * toc价
         */
        @ExcelReaderProperty(name = "C价")
        private BigDecimal tocPrice;


    }
}
