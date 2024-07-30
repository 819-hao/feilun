package com.seeease.flywheel.goods.entity;

import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Data
public class StockBaseInfo implements Serializable {
    /**
     * 库存id
     */
    private Integer stockId;
    /**
     * 采购商品
     */
    private Integer goodsId;
    /**
     * 商品编号：XYW+8位阿拉伯数字
     */
    private String wno;
    /**
     * 成色
     */
    private String finess;
    /**
     * 总价
     */
    private BigDecimal totalPrice;
    /**
     * 附件
     */
    private String attachment;
    /**
     * 表身号
     */
    private String stockSn;
    /**
     * store_management 所在地 ID 急售商品所在地
     */
    private Integer locationId;
    /**
     * 商品归属
     */
    private Integer belongId;
    private String belongName;
    /**
     * 扫码图片地址
     */
    private String subjectUrl;
    /**
     * 商品位置
     */
    private String locationName;
    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;
    /**
     * 经营权
     */
    private Integer rightOfManagement;

    /**
     * 经营权名
     */
    private String rightOfManagementName;


    /**
     * 品牌id
     */
    private Integer brandId;
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
     * 公价
     */
    private BigDecimal pricePub;

    /**
     * 款式
     */
    private String sex;


    private String purchaseSubjectName;

    private Integer purchaseSubjectId;


    /**
     * (不要为零ok？)总部采购价格
     */
    private BigDecimal purchasePrice;

    private Integer purchaseType;

    private String remark;

    private String serialNo;

    private BigDecimal tobPrice;
    private BigDecimal tocPrice;
    /**
     * 是否有回顾政策 1:是 0:否
     */
    private Integer isRepurchasePolicy;

    private List<BuyBackPolicyInfo> buyBackPolicy;



    private Integer ccId;

    private Integer isUnderselling;
    private Integer sourceSubjectId;
    private String warrantyDate;
    /**
     * 关联单号
     */
    private String originSerialNo;

    private Integer lineId;

    private Integer stockStatus;

    /**
     * 型号主图
     */
    private String image;

    /**
     * 活动价格
     */
    private BigDecimal promotionConsignmentPrice;
    /**
     * 成交价
     */
    private BigDecimal clinchPrice;


    /**
     * 库存来源
     */
    private Integer stockSrc;

    /**
     * 门店库龄
     */
    private Integer storageAge;

    /**
     * 总库龄
     */
    private Integer totalStorageAge;
    /**
     * 表带材质
     */
    private String strapMaterial;

    /**
     * 保卡管理-是否可调拨保卡
     */
    private Integer guaranteeCardManage;

    private BigDecimal newSettlePrice;

    private BigDecimal tagPrice;

    private String level;

}
