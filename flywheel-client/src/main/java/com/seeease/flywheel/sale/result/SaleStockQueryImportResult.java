package com.seeease.flywheel.sale.result;

import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleStockQueryImportResult implements Serializable {
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


    private String purchaseSubjectName;

    private Integer purchaseSubjectId;


    /**
     * (不要为零ok？)总部采购价格
     */
    private BigDecimal purchasePrice;

    private Integer purchaseType;

    private String remarks;

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

    /**
     * 同行寄售价
     */
    private BigDecimal preClinchPrice;

    /**
     * 成交价
     */
    private BigDecimal clinchPrice;

}
