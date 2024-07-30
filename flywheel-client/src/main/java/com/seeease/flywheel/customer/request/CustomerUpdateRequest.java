package com.seeease.flywheel.customer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wbh
 * @date 2023/3/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateRequest implements Serializable {

    private Integer customerId;

    private Integer customerContactsId;

    private Integer type;
    /**
     * 用于customer 表
     */
    private String customerName;
    private String accountName;
    private String bank;
    private String bankAccount;
    private String identityCard;
    private String identityCardImage;
    /**
     * 用于customerContacts 表
     */
    private String name;

    private String phone;

    private String address;

    /**
     * 正面身份证
     */
    private String frontIdentityCard;
    /**
     * 反面身份证
     */
    private String reverseIdentityCard;
}
