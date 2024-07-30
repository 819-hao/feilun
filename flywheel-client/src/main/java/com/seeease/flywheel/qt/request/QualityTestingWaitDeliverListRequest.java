package com.seeease.flywheel.qt.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;


/**
 * @Author Mr. Du
 * @Description 待转交列表
 * @Date create in 2023/1/18 14:43
 */
@Data
public class QualityTestingWaitDeliverListRequest extends PageRequest implements Serializable {


    /**
     * 表身号
     */
    private String stockSn;


    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 源头单号
     */
    private String originSerialNo;

    /**
     * 待转交 0 1 已转交
     */
    private Integer deliver;

}
