package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 集采模版 req
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupSettleStockQueryImportRequest extends ImportRequest<GroupSettleStockQueryImportRequest.ImportDto> {
    private Integer testId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "表身号")
        private String stockSn;
        @ExcelReaderProperty(name = "结算价")
        private BigDecimal settlePrice;

        @ExcelReaderProperty(name = "关联单号")
        private String originSerialNo;
    }
}
