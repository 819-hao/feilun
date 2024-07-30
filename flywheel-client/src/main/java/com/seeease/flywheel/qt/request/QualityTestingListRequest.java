package com.seeease.flywheel.qt.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;


/**
 * @Author Mr. Du
 * @Description 质检列表
 * @Date create in 2023/1/18 14:43
 */
@Data
public class QualityTestingListRequest extends PageRequest implements Serializable {

    /**
     * 单号查询
     */
    private String serialNo;


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
     * 质检来源
     */
    private Integer qtSource;

    /**
     * 维修后质检
     */
    private Integer fixOnQt;

    private Integer qtState;

    private Integer refuseFix;

}
