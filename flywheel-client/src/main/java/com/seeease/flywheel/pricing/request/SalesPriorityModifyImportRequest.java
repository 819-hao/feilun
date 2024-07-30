package com.seeease.flywheel.pricing.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/11/8
 */
@Data
@Builder
@NoArgsConstructor
public class SalesPriorityModifyImportRequest extends ImportRequest<SalesPriorityModifyImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {
        @ExcelReaderProperty(name = "品牌")
        private String brandName;

        @ExcelReaderProperty(name = "型号")
        private String model;

        @ExcelReaderProperty(name = "销售优先等级", readConverterExp = "1=仅B端销售,0=B/C可同销,2=仅C端销售")
        private String salesPriority;

        @ExcelReaderProperty(name = "自主经营类型")
        private String goodsLevel;
    }
}