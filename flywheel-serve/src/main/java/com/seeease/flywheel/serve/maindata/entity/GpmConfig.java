package com.seeease.flywheel.serve.maindata.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 毛利率配置表
 * @TableName gpm_config
 */
@Data
public class GpmConfig implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * C-toC;B-toB
     */
    private String toTarget;

    /**
     * 开始时间
     */
    private Date startDateTime;

    /**
     * 结束时间
     */
    private Date endDateTime;

    /**
     * 目标毛利率，比如20.5%就是20.5
     */
    private BigDecimal gpmTarget;

    /**
     * 创建人
     */
    private String createBy;

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
    private Integer deleted;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}