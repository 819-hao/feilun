package com.seeease.flywheel.qt.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 质检编辑
 * @Date create in 2023/2/4 10:11
 */
@Data
public class QualityTestingEditRequest implements Serializable {

    private Integer qualityTestingId;

    /**
     * 成色
     */
    private String finess;

    /**
     * 腕周
     */
    private String week;

    /**
     * 表带材质
     */
    private String strapMaterial;

    /**
     * 表节
     */
    private String watchSection;

    private Integer stockId;

    /**
     * 5.25
     * 是否是新表带
     */
    private Integer isNewStrap;

    private Integer qtSource;

    private String stockSn;

    private Integer goodsId;
    /**
     * 是否护理 whetherProtect
     */
    private Integer whetherProtect;

    private String model;
}
