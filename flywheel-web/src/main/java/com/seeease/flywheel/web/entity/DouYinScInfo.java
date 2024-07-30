package com.seeease.flywheel.web.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

/**
 * @TableName douyin_sc_info
 */
@TableName(value = "douyin_sc_info", autoResultMap = true)
@Data
public class DouYinScInfo extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 质检机构ID
     */
    private String scId;

    /**
     * 质检机构名称
     */
    private String scName;

    /**
     * 机构联系电话
     */
    private String scPhone;

    /**
     * 机构详细地址
     */
    private String scAddress;

    /**
     * 机构地址_街道
     */
    private String scStreet;

    /**
     * 机构地址_区
     */
    private String scDistrict;

    /**
     * 机构地址_省
     */
    private String scProvince;

    /**
     * 机构地址_市
     */
    private String scCity;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}