package com.seeease.flywheel.qt.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 质检列表
 * @Date create in 2023/1/18 14:43
 */
@Data
public class QualityTestingListResult implements Serializable {

    private String serialNo;

    private Integer qtState;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    private String storeWorkSerialNo;

    private Integer id;

    private Integer stockId;
    private Integer whetherProtect;

    private Integer goodsId;

    private String brandName;

    private String seriesName;
    private Integer seriesType;

    private String model;

    private String stockSn;

    private Integer fixOnQt;

    private String finess;

    private String week;

    /**
     * 表带材质
     */
    private String strapMaterial;

    /**
     * 表节
     */
    private String watchSection;

    private String attachment;

    private Integer qtSource;

    /**
     * 埋点时间
     */
    private String taskArriveTime;

    /**
     * 主图图片
     */
    private String image;

    /**
     * 下拉项
     */
    private List<SelectItem> select;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectItem implements Serializable {

        private Integer key;

        private String name;
    }
}
