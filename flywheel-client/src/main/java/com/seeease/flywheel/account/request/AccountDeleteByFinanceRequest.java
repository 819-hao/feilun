package com.seeease.flywheel.account.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/8/7 15:15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDeleteByFinanceRequest implements AccountImportRequest {
    @Override
    public Integer getPageType() {
        return 1;
    }

    private List<String> accountGroupList;

    private String accountType;

    private String completeDateStart;

    private String completeDateEnd;
}
