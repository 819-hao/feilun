package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/7/26
 */
@Data
@Builder
@NoArgsConstructor
public class DouYinProductMappingImportRequest extends ImportRequest<DouYinProductMappingImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "抖音门店ID")
        private Integer douYinShopId;
        /**
         * 抖音商品id
         */
        @ExcelReaderProperty(name = "商品ID")
        private String douYinProductId;

        /**
         * 抖音sku_id
         */
        @ExcelReaderProperty(name = "规格ID（SKUID）")
        private String douYinSkuId;

        /**
         * 型号编码
         */
        @ExcelReaderProperty(name = "商家编码")
        private String modelCode;
    }
}
