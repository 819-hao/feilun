package com.seeease.flywheel.serve.purchase.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Tiro
 * @date 2023/2/6
 */
@Data
public class BillPurchaseLineDetailsVO implements Serializable {

    /**
     * 详情id
     */
    private Integer id;

    private String serialNo;

    /**
     * 型号id
     */
    private Integer goodsId;

    /**
     * 商品编码
     */
    private String wno;

    /**
     * 表身号
     */
    private String stockSn;
    /**
     * 老表身号
     */
    private String oldStockSn;
    /**
     * 成色
     */
    private String finess;

    /**
     * 附件列表
     */
    private String attachmentList;
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
     * 表节
     */
    private String watchSection;

    /**
     * 版本,和表身号构成唯一索引
     */
    private Integer edition;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 销售等级
     */
    private Integer salesPriority;

    /**
     * 商品级别
     */
    private String goodsLevel;
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
    private BigDecimal pricePub;

    /**
     * 机芯类型
     */
    private String movement;

    /**
     * 表径
     */
    private String watchSize;

    private Integer isCard;

    private String warrantyDate;

    private String attachment;

    private BigDecimal recyclePrice;

    private BigDecimal salePrice;

    /**
     * 差额
     */
    private BigDecimal difference;

    private Integer differenceType;

    private String remarks;

    private String strapMaterial;

    private BigDecimal dealPrice;

    private BigDecimal fixPrice;

    private BigDecimal planFixPrice;

    private BigDecimal oldPlanFixPrice;
    private BigDecimal oldPurchasePrice;
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
     * 寄售成交价（回购）寄售价（个人回购，寄售到门店价格）
     */
    private BigDecimal consignmentPrice;

    /**
     * 表带更换费（回购）
     */
    private BigDecimal watchbandReplacePrice;
    private BigDecimal oldWatchbandReplacePrice;

    /**
     * 寄售成交价（回购）
     */
    private BigDecimal clinchPrice;

    private Integer stockId;

    private String originPurchaseReturnSerialNo;

    private String returnFixRemarks;
    /**
     *  物鱼供货价
     */
    private BigDecimal wuyuPrice;

    /**
     *  兜底价
     */
    private BigDecimal wuyuBuyBackPrice;
}
