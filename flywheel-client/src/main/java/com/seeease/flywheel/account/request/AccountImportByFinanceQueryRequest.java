package com.seeease.flywheel.account.request;

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
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 15:40
 */
@Data
@Builder
@NoArgsConstructor
public class AccountImportByFinanceQueryRequest extends ImportRequest<AccountImportByFinanceQueryRequest.ImportDto> implements AccountImportRequest {

    @Override
    public Integer getPageType() {
        return 1;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "费用归类")
        private String accountGroup;

        @ExcelReaderProperty(name = "费用类型")
        private String accountType;

        @ExcelReaderProperty(name = "完成日期")
        private Date completeDate;

        @ExcelReaderProperty(name = "付款主体")
        private String companyName;

        @ExcelReaderProperty(name = "金额")
        private BigDecimal money;

        @ExcelReaderProperty(name = "摘要")
        private String digest;

    }


}
