package com.seeease.flywheel.customer.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/9/25
 */
@Data
public class CustomerContactsInfo implements Serializable {

    /**
     * 联系人id
     */
    private Integer id;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 地址
     */
    private String address;

    /**
     * 联系人姓名
     */
    private String name;

    /**
     * 联系人电话
     */
    private String phone;
}
