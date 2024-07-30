package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 天猫回调通知
 *
 * @TableName tmall_callback_notify
 */
@TableName(value = "tmall_callback_notify")
@Data
public class TmallCallbackNotify implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 通知类型
     */
    private String method;

    /**
     * 天猫履约单号
     */
    private String bizOrderCode;

    /**
     * 消息体
     */
    private String body;

    /**
     * 处理结果:0-待处理，1-已处理，-1-失败
     */
    private Integer handleResult;

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