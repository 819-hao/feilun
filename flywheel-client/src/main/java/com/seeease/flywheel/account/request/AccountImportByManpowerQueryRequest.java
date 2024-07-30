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
 * @Description 人力
 * @Date create in 2023/7/18 15:40
 */
@Data
@Builder
@NoArgsConstructor
public class AccountImportByManpowerQueryRequest extends ImportRequest<AccountImportByManpowerQueryRequest.ImportDto> implements AccountImportRequest {

    @Override
    public Integer getPageType() {
        return 2;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "成本归类")
        private String accountGroup;

        @ExcelReaderProperty(name = "成本类型")
        private String accountType;

        @ExcelReaderProperty(name = "完成日期")
        private Date completeDate;

        @ExcelReaderProperty(name = "业务单元")
        private String shopName;

        @ExcelReaderProperty(name = "金额")
        private BigDecimal money;
    }


}
