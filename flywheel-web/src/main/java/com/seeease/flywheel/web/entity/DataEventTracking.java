package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import java.io.Serializable;
import lombok.Data;

/**
 * 数据埋点
 * @TableName data_event_tracking
 */
@TableName(value ="data_event_tracking")
@Data
public class DataEventTracking extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 埋点数据
     */
    private String trackingData;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}