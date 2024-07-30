package com.seeease.flywheel.customer.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wbh
 * @date 2023/3/1
 */
@Data
public class BankCreateRequest implements Serializable {

    private Integer customerId;

    private String bankName;

    private String bankAccount;

    private String bankCard;

}