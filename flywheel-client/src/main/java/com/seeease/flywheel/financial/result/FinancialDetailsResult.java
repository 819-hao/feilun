package com.seeease.flywheel.financial.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialDetailsResult implements Serializable {


    private Integer id;


    /**
     * $column.columnComment
     */
    private Integer financialDocumentsId;
//    维修成本： fixCost
    /**
     * 供应商/客户
     */
    private String customerName;
    /**
     * 供应商/客户类别
     */
    private Integer customerType;
    /**
     * 商品归属
     */
    private String belongName;

    @JsonIgnore
    private transient Integer belongId;
    /**
     * 所处仓库
     */
    private String locationName;

    @JsonIgnore
    private transient Integer locationId;


    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 采购价
     */
    private BigDecimal purchasePrice;
    /**
     * 成交价
     */
    private BigDecimal clinchPrice;
    /**
     * 退款金额
     */
    private BigDecimal returnPrice;
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
    private String modelName;

    private BigDecimal tobPrice;
    private BigDecimal tocPrice;
    private BigDecimal tagPrice;
    private BigDecimal pricePub;
    private Integer stockSrc;
    private BigDecimal fixPrice;
    /**
     * 活动寄售价
     */
    private BigDecimal promotionConsignmentPrice;
    /**
     * 财务业绩
     */
    private BigDecimal financialPerformance;
    /**
     * 品牌营销费用
     */
    private BigDecimal brandMarketingExpenses;
    /**
     * 寄售成交时间?
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date soldTime;
    /**
     * 寄售价(门店采购价)
     */
    private BigDecimal consignSalePrice;
    /**
     * 寄售价
     */
    private BigDecimal jsClinchPrice;
    /**
     * 差额
     */
    private BigDecimal marginPrice;
    /**
     * 转销比
     */
    private BigDecimal ratio;

    /**
     * 回购服务费
     */
    private BigDecimal buyBackServiceFee;
    /**
     * 采购主体
     */
    private String purchaseSubject;

    /**
     * 门店采购价
     */
    private BigDecimal storePurchasePrice;

    /**
     * 经营权
     */
    @JsonIgnore
    private Integer outletStore;

    /**
     * 销售位置
     */
    @JsonIgnore
    private Integer salesPosition;

    /**
     * 商品归属
     */
    @JsonIgnore
    private Integer goodsBelong;

    /**
     * 商品位置
     */
    @JsonIgnore
    private Integer goodsPosition;

    /**
     * 经营权
     */
    private String outletStoreStr;

    /**
     * 销售位置
     */
    private String salesPositionStr;

    /**
     * 商品归属
     */
    private String goodsBelongStr;

    /**
     * 商品位置
     */
    private String goodsPositionStr;

    private Integer demandId;
    /**
     * 需求门店
     */
    private String demandStoreStr;

    /**
     * 采购主体id
     */
    @JsonIgnore
    private Integer sourceSubjectId;

    /**
     * 服务费
     */
    private BigDecimal serviceFee;

    private Integer oldToNew;

    /**
     * 商品编码
     */
    private String wno;

    private String attachment;
    @JsonIgnore
    private Integer stockId;

    /**
     * 用于区分是否分成
     */
    private Integer divideInto;

    /**
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date saleTime;

    /**
     *
     */
    private BigDecimal buyBackPrice;

}
