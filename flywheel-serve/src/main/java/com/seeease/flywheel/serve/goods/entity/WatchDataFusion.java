package com.seeease.flywheel.serve.goods.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品手表基础数据融合
 *
 * @author wbh
 * @date 2023/2/3
 */
@Data
public class WatchDataFusion implements Serializable {

    private Integer stockId;

    private Integer goodsId;
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
    private Integer seriesType;
    /**
     * 型号
     */
    private String model;

    /**
     * 型号编码
     */
    private String modelCode;

    /**
     * 公价
     */
    private BigDecimal pricePub;
    /**
     * 当前行情价
     */
    private BigDecimal currentPrice;

    /**
     * 20年行情价
     */
    private BigDecimal twoZeroFullPrice;

    /**
     * 22年行情价
     */
    private BigDecimal twoTwoFullPrice;
    private BigDecimal purchasePrice;

    /**
     * 机芯类型
     */
    private String movement;

    /**
     * 表径
     */
    private String watchSize;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 表带号
     */
    private String strap;

    /**
     * 附件信息，
     */
    private String attachment;

    private String remark;

    /**
     * 表节数
     */
    private String watchSection;

    /**
     * 表带材质
     */
    private String strapMaterial;

    private String finess;

    private String wno;

    private BigDecimal fixPrice;

    private Integer brandId;

    private Integer seriesId;

    private Integer totalStorageAge;
    private Integer stockSrc;

    private Integer locationId;
    private Integer rightOfManagement;
    private Integer stockStatus;

    /**
     * 款式
     */
    private String sex;

    /**
     * 表盘形状
     */
    private String shape;
    /**
     * 表带颜色
     */
    private String braceletColor;
    /**
     * 表扣类型
     */
    private String buckleType;
    /**
     * 表壳材质
     */
    private String watchcaseMaterial;
    /**
     * 防水深度
     */
    private String depth;

    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;

    private Integer belongId;
    private BigDecimal wuyuBuyBackPrice;
    private BigDecimal wuyuPrice;
    private BigDecimal newSettlePrice;
    private Integer demandId;
}
