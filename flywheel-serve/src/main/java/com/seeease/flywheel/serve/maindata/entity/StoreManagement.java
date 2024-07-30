package com.seeease.flywheel.serve.maindata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 门店管理
 * @TableName store_management
 */
@TableName(value ="store_management")
@Data
public class StoreManagement implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 标签id
     */
    private Integer tagId;

    /**
     * 门店类型
     */
    private String type;

    /**
     * 门店配额
     */
    private BigDecimal quota;

    /**
     * 所在区域
     */
    private String area;

    /**
     * 店长id
     */
    private Integer principalId;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 
     */
    private String address;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 是否保留
     */
    private Integer delFlag;

    /**
     * 
     */
    private BigDecimal residualQuota;

    /**
     * 经纬度，逗号分隔，如：120.00001,30.00001
     */
    private String position;

    /**
     * 经营状态
     */
    private Integer doStatus;

    /**
     * 网店名称
     */
    private String mallStoreName;

    /**
     * 客户联系人
     */
    private Integer customerContactId;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}