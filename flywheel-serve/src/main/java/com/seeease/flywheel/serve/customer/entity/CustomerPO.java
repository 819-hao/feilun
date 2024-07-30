package com.seeease.flywheel.serve.customer.entity;

import lombok.Data;

/**
 * @author wbh
 * @date 2023/2/4
 */
@Data
public class CustomerPO {
    private String customerName;

    private String accountName;

    private String bank;

    private String bankAccount;

    private Integer customerContactId;

    private String contactName;

    private String contactPhone;

    private String contactAddress;

    private Integer customerId;
}
