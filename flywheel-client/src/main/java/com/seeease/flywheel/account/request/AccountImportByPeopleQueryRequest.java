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
public class AccountImportByPeopleQueryRequest extends ImportRequest<AccountImportByPeopleQueryRequest.ImportDto> implements AccountImportRequest {

    @Override
    public Integer getPageType() {
        return 3;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "完成日期")
        private Date completeDate;

        @ExcelReaderProperty(name = "业务单元")
        private String shopName;

        @ExcelReaderProperty(name = "人数")
        private BigDecimal peopleNumber;
    }


}
