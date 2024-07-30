package com.seeease.flywheel.serve.financial.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 财务单据详情
 * @TableName financial_documents_detail
 */
@TableName(value = "financial_documents_detail", autoResultMap = true)
@Data
public class FinancialDocumentsDetail implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer financialDocumentsId;

    /**
     * 
     */
    private String brandName;

    /**
     * 
     */
    private String seriesName;

    /**
     * 
     */
    private String modelName;

    /**
     * 
     */
    private BigDecimal tocPrice;

    /**
     * 
     */
    private BigDecimal tobPrice;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 商品编号
     */
    private String wno;

    /**
     * 寄售价
     */
    private BigDecimal consignSalePrice;

    /**
     * 采购价
     */
    private BigDecimal purchasePrice;

    /**
     * 成交价
     */
    private BigDecimal clinchPrice;

    /**
     * 维修价
     */
    private BigDecimal fixPrice;

    /**
     * 当列表类型是回购的时候 这块就是回购价
     */
    private BigDecimal returnPrice;

    /**
     * 公价
     */
    private BigDecimal pricePub;

    /**
     * 吊牌价
     */
    private BigDecimal tagPrice;

    /**
     * 差额
     */
    private BigDecimal marginPrice;

    /**
     * 
     */
    private BigDecimal ratio;
    /**
     * 财务业绩
     */
    private BigDecimal financialPerformance;
    /**
     * 品牌营销费用
     */
    private BigDecimal brandMarketingExpenses;
    /**
     * 采购主体
     */
    private Integer stockSrc;

    /**
     * 老的归属
     */
    private Integer belongId;

    /**
     * 老的位置
     */
    private Integer locationId;

    /**
     * 经营权
     */
    private Integer outletStore;

    /**
     * 商品位置
     */
    private Integer goodsPosition;

    /**
     * 门店采购价
     */
    private BigDecimal storePurchasePrice;

    /**
     * 销售位置
     */
    private Integer salesPosition;

    /**
     * 商品归属
     */
    private Integer goodsBelong;

    /**
     * 寄售成交价
     */
    private BigDecimal jsClinchPrice;

    /**
     * 用于区分是否分成
     */
    private Integer divideInto;

    /**
     * 来源主体id
     */
    private Integer sourceSubjectId;

    /**
     * 
     */
    private Date saleTime;

    /**
     * 服务费
     */
    private BigDecimal serviceFee;

    /**
     * 用于表示 是否从老财务 变更过来 
     */
    private Integer oldToNew;

    /**
     * 回购服务费
     */
    private BigDecimal buyBackServiceFee;

    /**
     * 
     */
    private BigDecimal buyBackPrice;

    /**
     * 需求门店
     */
    private Integer demandId;
    /**
     * 活动寄售价
     */
    private BigDecimal promotionConsignmentPrice;

    /**
     * 
     */
    private Integer stockId;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}