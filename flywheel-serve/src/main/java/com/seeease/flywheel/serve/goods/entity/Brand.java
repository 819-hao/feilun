package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.goods.enums.BrandBusinessTypeEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 品牌
 * @TableName brand
 */
@TableName(value ="brand")
@Data
public class Brand extends BaseDomain {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 品牌名称
     */
    private String name;

    /**
     * 经营类型：1-综合小表、2-大表
     */
    private BrandBusinessTypeEnum businessType;

    /**
     * 
     */
    private Integer processGroupId;

    /**
     *  品牌类型品牌属性1高奢、2奢华、3豪华、4亲民、5时尚、6独立
     */
    private Integer attribute;

    /**
     * 简介
     */
    private String content;

    /**
     * logo
     */
    private String logo;

    /**
     * 品牌负责人名字
     */
    private String brandPrincipal;

    /**
     * 品牌负责人企业微信userid
     */
    private String brandPrincipalQwUserid;

    /**
     * 品牌负责人userId
     */
    private Long brandPrincipalUserId;

    /**
     * 库龄超90天，提醒次数
     */
    @TableField("remind_count_by_90")
    private Integer remindCountBy90;

    /**
     * 是否停止提醒：0-否；1-是
     */
    @TableField("remind_switch_by_90")
    private Integer remindSwitchBy90;

    /**
     * 库龄超180天，提醒次数
     */
    @TableField("remind_count_by_180")
    private Integer remindCountBy180;

    /**
     * 是否停止提醒：0-否；1-是
     */
    @TableField("remind_switch_by_180")
    private Integer remindSwitchBy180;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 删除逻辑
     */
    private Integer delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}