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
public class AccountQueryResult implements Serializable {

    private String accountGroup;

    private String accountType;

    private String completeDate;

    private String companyName;

    private BigDecimal money;

    private String createdTime;

    private BigDecimal peopleNumber;

    private String shopName;

    private String digest;

    private Integer id;
}
