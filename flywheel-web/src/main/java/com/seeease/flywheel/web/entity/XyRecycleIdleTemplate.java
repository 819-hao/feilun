package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 闲鱼估价问卷模版
 * @TableName xy_recycle_idle_template
 */
@TableName(value ="xy_recycle_idle_template")
@Data
public class XyRecycleIdleTemplate extends BaseDomain implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * spuId
     */
    private String spuId;

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 模版状态
     */
    private Long templateState;

    /**
     * 模版版本
     */
    private String templateRevision;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 乐观锁
     */
    private Integer revision;

    /**
     * 创建人id
     */
    private Integer createdId;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 修改人id
     */
    private Integer updatedId;

    /**
     * 修改人
     */
    private String updatedBy;

    /**
     * 修改时间
     */
    private Date updatedTime;

    /**
     * 删除标识
     */
    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}