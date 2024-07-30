package com.seeease.flywheel.serve.anomaly.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.anomaly.enums.AnomalyStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 异常单
 * @TableName bill_anomaly
 */
@TableName(value ="bill_anomaly")
@Data
public class BillAnomaly extends BaseDomain implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 作业单id
     */
    private Integer workId;

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 仓库预作业单号
     */
    private String storeWorkSerialNo;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 质检单id
     */
    private Integer qtId;

    /**
     * 维修id
     */
    private Integer fixId;

    /**
     * 异常状态
     */
    private AnomalyStateEnum anomalyState;

    /**
     * 完成时间
     */
    private Date finishTime;

    /**
     * 埋点时间
     */
    private Date taskArriveTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}