package com.seeease.flywheel.purchase.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 采购计划导入模版
 */
@Data
@Builder
@NoArgsConstructor
public class PurchasePlanImportRequest extends ImportRequest<PurchasePlanImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "品牌")
        private String brandName;

        @ExcelReaderProperty(name = "型号")
        private String model;

        @ExcelReaderProperty(name = "需求计划数量")
        private Integer planNumber;
    }
}
