package com.seeease.flywheel.customer.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wbh
 * @date 2023/3/1
 */
@Data
@Accessors(chain=true)
public class CustomerCreateRequest implements Serializable {

    private Integer type;

    private String customerName;

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
