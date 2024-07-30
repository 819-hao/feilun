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
 * @date 2023/8/8
 */
@Data
@Builder
@NoArgsConstructor
public class StockManageInfoImportRequest extends ImportRequest<StockManageInfoImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "表身号")
        private String stockSn;


        @ExcelReaderProperty(name = "盒号")
        private String boxNumber;


        @ExcelReaderProperty(name = "库位大区")
        private String storageRegion;

        @ExcelReaderProperty(name = "库位子区")
        private String storageSubsegment;
    }
}
