package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Tiro
 * @date 2023/3/30
 */

@Builder
@NoArgsConstructor
public class StoreQuotaImportRequest extends ImportRequest<StoreQuotaImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        /**
         *编号
         */
        @ExcelReaderProperty(name = "配货额度")
        private BigDecimal quota;
        /**
         *名称
         */
        @ExcelReaderProperty(name = "门店名称")
        private String tagName;
        /**
         *名称
         */
        @ExcelReaderProperty(name = "开始时间")
        private String start;
        /**
         *名称
         */
        @ExcelReaderProperty(name = "结束时间")
        private String end;


    }
}
