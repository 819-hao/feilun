package com.seeease.flywheel.serve.storework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 发货作业拦截表
 *
 * @TableName wms_work_intercept
 */
@TableName(value = "wms_work_intercept")
@Data
public class WmsWorkIntercept extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 归属门店id
     */
    private Integer belongingStoreId;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 状态：1-拦截，0-取消
     */
    private Integer interceptState;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}