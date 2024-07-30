package com.seeease.flywheel.rfid.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfidWorkDetailResult implements Serializable {

    /**
     * id
     */
    private Integer workId;
    /**
     * 品牌
     */
    private String brandName;
    /**
     * 系列
     */
    private String seriesName;
    /**
     * 表身号
     */
    private String stockSn;
    /**
     * 型号
     */
    private String model;
    /**
     * 商品编码
     */
    private String wno;
    /**
     * 商品id
     */
    private Integer stockId;
    /**
     * 出库编号
     */
    private String serialNo;
    /**
     * 用户任务
     */
    private Object task;
    /**
     * 商品id
     */
    private Integer goodsId;
}
