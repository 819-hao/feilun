package com.seeease.flywheel.serve.maindata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 采购主体
 * @TableName purchase_subject
 */
@TableName(value ="purchase_subject")
@Data
public class PurchaseSubject implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 主体名称
     */
    private String name;

    /**
     * 打款主体
     */
    private String subjectCompany;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 删除逻辑
     */
    private Integer delFlag;

    /**
     * 是否经过物语平台 1是 0否
     */
    private Integer throughThePlatform;

    /**
     * 寄售加点 小数
     */
    private Integer consignmentPoint;

    /**
     * 是新表还是二手表
     */
    private Integer watchStatus;

    /**
     * 仓库id
     */
    private Long storeId;

    /**
     * 是否流转采购主体
     */
    private Integer isFlow;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}