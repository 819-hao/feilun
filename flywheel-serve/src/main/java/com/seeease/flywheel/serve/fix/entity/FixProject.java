package com.seeease.flywheel.serve.fix.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 维修项目
 * @TableName fix_project
 */
@TableName(value ="fix_project")
@Data
public class FixProject extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 维修项目名称
     */
    private String name;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 删除逻辑
     */
    private Integer delFlag;

    /**
     * 预计维修时间
     */
    private String anticipatedMaintenanceTime;

    /**
     * 0 禁用 1 启用
     */
    private Integer state;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}