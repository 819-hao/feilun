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
 *
 */
@Data
@Builder
@NoArgsConstructor
public class FinancialStatementImportRequest extends ImportRequest<FinancialStatementImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "流水号")
        private String serialNo;

        @ExcelReaderProperty(name = "收款时间")
        private Date collectionTime;

        @ExcelReaderProperty(name = "流水归属")
        private String shopName;

        @ExcelReaderProperty(name = "收款主体")
        private String subjectName;

        @ExcelReaderProperty(name = "付款人")
        private String payer;

        @ExcelReaderProperty(name = "摘要")
        private String remarks;

        @ExcelReaderProperty(name = "实收金额")
        private BigDecimal fundsReceived;

        @ExcelReaderProperty(name = "手续费")
        private BigDecimal procedureFee;

        @ExcelReaderProperty(name = "收款金额")
        private BigDecimal receivableAmount;

        @ExcelReaderProperty(name = "待核销金额")
        private BigDecimal waitAuditPrice;

    }
}
