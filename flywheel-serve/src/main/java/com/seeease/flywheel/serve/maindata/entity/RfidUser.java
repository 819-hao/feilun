package com.seeease.flywheel.serve.maindata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName rfid_user
 */
@TableName(value = "rfid_user")
@Data
public class RfidUser extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId
    private Integer id;

    /**
     *
     */
    private Integer userId;

    /**
     *
     */
    private String userName;

    /**
     *
     */
    private String password;

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