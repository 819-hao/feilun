package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.goods.enums.SeriesTypeEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.util.Date;

/**
 * 系列
 * @TableName series
 */
@TableName(value ="series")
@Data
public class Series extends BaseDomain {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 系列名称
     */
    private String name;

    /**
     * 品牌id
     */
    private Integer brandId;
    /**
     * 类型
     */
    private SeriesTypeEnum seriesType;

    /**
     * 俗称
     */
    private String vulgo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 
     */
    private String updateBy;

    /**
     * 
     */
    private String createBy;

    /**
     * 
     */
    private Integer delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}