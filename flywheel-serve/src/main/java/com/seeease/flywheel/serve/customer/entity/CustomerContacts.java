package com.seeease.flywheel.serve.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 供应商联系人

 * @TableName customer_contacts
 */
@TableName(value ="customer_contacts")
@Data
public class CustomerContacts implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 
     */
    private String openId;

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

    /**
     * 供应商名称联系人名称联系人电话json
     */
    private String cnmaeCcnamePhone;

    /**
     * 
     */
    private String areaIds;

    /**
     * 是否删除
     */
    private Integer deleted;

    /**
     * 
     */
    private String prop;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}