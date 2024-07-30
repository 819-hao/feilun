package com.seeease.flywheel.account.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 16:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateRequest implements Serializable {

    private String accountGroup;

    private String accountType;

    private String completeDate;

    private String companyName;

    private BigDecimal money;
}
