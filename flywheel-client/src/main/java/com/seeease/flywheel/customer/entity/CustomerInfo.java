package com.seeease.flywheel.customer.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/9/25
 */
@Data
public class CustomerInfo implements Serializable {

    /**
     * 客户id
     */
    private Integer id;

    /**
     * 客户类型 1:个人 2.企业
     */
    private Integer type;

    /**
     * 公司名称
     */
    private String customerName;

    /**
     * 联系人列表
     */
    private List<CustomerContactsInfo> contactsInfoList;
}
