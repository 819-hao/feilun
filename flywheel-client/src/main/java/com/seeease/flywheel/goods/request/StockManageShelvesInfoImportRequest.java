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
 * @date 2023/9/4
 */
@Data
public class StockManageShelvesInfoImportRequest extends ImportRequest<StockManageShelvesInfoImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "品牌")
        private String brandName;

        @ExcelReaderProperty(name = "型号")
        private String model;

        @ExcelReaderProperty(name = "货位流转码")
        private String shelvesSimplifiedCode;

    }
}