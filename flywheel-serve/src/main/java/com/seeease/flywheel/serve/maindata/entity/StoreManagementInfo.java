package com.seeease.flywheel.serve.maindata.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/3/7
 */
@Data
public class StoreManagementInfo implements Serializable {
    private Integer id;
    private String name;
    /**
     * 客户id
     */
    private Integer customerId;
    /**
     * 客户联系人
     */
    private Integer customerContactId;
    /**
     * 门店简码
     */
    private String shortcodes;
}
