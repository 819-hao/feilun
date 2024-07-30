package com.seeease.flywheel.helper.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class MarketTrendsSearchResult implements Serializable {
    /**
     * 型号id
     */
    private Integer id;
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
     * 公价
     */
    private String pricePub;
    /**
     * 图片
     */
    private String image;
}
