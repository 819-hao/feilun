package com.seeease.flywheel.recycle.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Auther Gilbert
 * @Date 2023/9/4 16:04
 */
@Data
@Accessors(chain=true)
public class BuyBackForLineResult implements Serializable {
    private Integer stockId;

    /**
     * 采购单号
     */
    private String serialNo;


    private Integer originStockId;

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

    /**
     * 型号
     */
    private String model;

    private String finess;

    /**
     * 公价
     */
    private BigDecimal pricePub;


    /**
     * 表身号
     */
    private String stockSn;


    /**
     * 附件信息，
     */
    private String attachment;

    /**
     * 表节数
     */
    private String watchSection;

    /**
     * 表带材质
     */
    private String strapMaterial;

    /**
     * 回收
     */
    private BigDecimal referenceBuyBackRecyclePrice;

    /**
     * 置换
     */
    private BigDecimal referenceBuyBackInPrice;

    /**
     * 表带更换费
     */
    private BigDecimal watchbandReplacePrice;

    /**
     * 成交价
     */
    private BigDecimal clinchPrice;

    private String wno;

    /**
     * 置换折扣区间
     */
    private List<String> displaceDiscountRange;
    /**
     * 回收折扣区间
     */
    private List<String> recycleDiscountRange;
    /**
     * 老的sn
     */
    private String oldStockSn;

    /**
     * 附件列表
     */
//        private String attachmentList;
    private String oldAttachment;
    /**
     * 采购价
     */
    private BigDecimal purchasePrice;

    /**
     * 采购单行状态
     */
    private Integer purchaseLineState;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
//    private Date createdTime;

    /**
     * 销售等级
     */
    private Integer salesPriority;

    /**
     * 商品级别
     */
    private String goodsLevel;

    /**
     * 机芯类型
     */
    private String movement;

    /**
     * 表径
     */
    private String watchSize;

    private BigDecimal recyclePrice;

    private BigDecimal salePrice;

    private String remarks;

    private BigDecimal dealPrice;

    private BigDecimal fixPrice;

    private BigDecimal planFixPrice;


    /**
     * 回购服务费（个人回购）
     */
    private BigDecimal recycleServePrice;

    /**
     * 实际回购价（回购）
     */
    private BigDecimal buyBackPrice;

    /**
     * 参考回购价（回购）
     */
    private BigDecimal referenceBuyBackPrice;

    /**
     * 寄售价（个人回购，寄售到门店价格）
     */
    private BigDecimal consignmentPrice;


    private String originPurchaseReturnSerialNo;

    private String warrantyDate;

}
