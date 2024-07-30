package com.seeease.flywheel.serve.goods.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2023/2/8
 */
@Data
public class StockExt {

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 商品状态
     */
    private Integer stockStatus;
    private Integer seriesType;

    /**
     * 保卡时间
     */
    private String warrantyDate;

    /**
     * 是否卡
     */
    private Integer isCard;

    /**
     * 附件详情
     */
    private String attachmentDetails;

    private String attachment;

    private String finess;

    private BigDecimal tobPrice;
    private BigDecimal tocPrice;
    private BigDecimal tagPrice;

    /**
     * 商品编码
     */
    private String wno;

    private String createdBy;

    private BigDecimal pricePub;

    private Integer stockSrc;

    private Integer goodsId;


    private String level;

    /**
     * 主图
     */
    private String image;

    /**
     * 品牌
     */
    private String brandName;

    private Integer brandId;

    /**
     * 系列
     */
    private String seriesName;

    /**
     * 型号
     */
    private String model;

}
