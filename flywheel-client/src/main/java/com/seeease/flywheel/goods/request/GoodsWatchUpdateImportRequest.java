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
 * @date 2024/1/24
 */
@Data
@Builder
@NoArgsConstructor
public class GoodsWatchUpdateImportRequest extends ImportRequest<GoodsWatchUpdateImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "型号编码")
        private String modelCode;

        @ExcelReaderProperty(name = "系列")
        private String seriesName;

        @ExcelReaderProperty(name = "型号")
        private String model;

        @ExcelReaderProperty(name = "表径")
        private String watchSize;

        @ExcelReaderProperty(name = "适用人群")
        private String sex;
    }
}
