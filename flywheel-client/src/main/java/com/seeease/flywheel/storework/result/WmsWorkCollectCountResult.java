package com.seeease.flywheel.storework.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/9/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WmsWorkCollectCountResult implements Serializable {
    /**
     * 主图
     */
    private String image;

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

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 速记码
     */
    private String shelvesSimplifiedCode;
}
