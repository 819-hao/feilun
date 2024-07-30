package com.seeease.flywheel.allocate.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Auther Gilbert
 * @Date 2023/11/7 14:29
 */
@Data
@Builder
@NoArgsConstructor
public class BorrowStockImportRequest extends ImportRequest<BorrowStockImportRequest.ImportDto> {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {
        /**
         * 表身号
         */
        @ExcelReaderProperty(name = "表身号")
        private String stockSn;
    }
}
