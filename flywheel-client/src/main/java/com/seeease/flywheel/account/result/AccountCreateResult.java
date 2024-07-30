package com.seeease.flywheel.account.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 15:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateResult implements Serializable {

    private Integer accountGroup;

    private Integer accountType;

    private String completeDate;

    private Integer companyId;

    private BigDecimal money;
    
}
