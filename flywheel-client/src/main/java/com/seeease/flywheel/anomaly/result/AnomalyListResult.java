package com.seeease.flywheel.anomaly.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/3/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyListResult implements Serializable {

    private Integer id;

    private String serialNo;

    /**
     * 状态值
     */
    private Integer anomalyState;

    /**
     * 仓库单据
     */
    private String storeWorkSerialNo;

    private Integer goodsId;

    private Integer stockId;

    /**
     * 来源单据
     */
    private String originSerialNo;

    private String createdBy;

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 系列
     */
    private String seriesName;

    /**
     * 型号
     */
    private String model;


    private String stockSn;

    private String attachment;


    /**
     * 成色
     */
    private String finess;

    private String image;

    private String createdTime;

    private String finishTime;

    private String bqtExceptionReason;

    private String bqtSerialNo;

    private String fixSerialNo;

    private String remarks;

    private String unusualDesc;
}
