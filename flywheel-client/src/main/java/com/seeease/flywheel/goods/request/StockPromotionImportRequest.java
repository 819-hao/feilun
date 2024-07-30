package com.seeease.flywheel.goods.request;

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
 *
 */
@Data
@Builder
@NoArgsConstructor
public class StockPromotionImportRequest extends ImportRequest<StockPromotionImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "表身号")
        private String stockSn;
        @ExcelReaderProperty(name = "活动价")
        private BigDecimal promotionPrice;
        @ExcelReaderProperty(name = "活动寄售价比例")
        private BigDecimal consignmentRatio;
        @ExcelReaderProperty(name = "活动开始时间")
        private Date startTime;
        @ExcelReaderProperty(name = "活动结束时间")
        private Date endTime;
    }
}
