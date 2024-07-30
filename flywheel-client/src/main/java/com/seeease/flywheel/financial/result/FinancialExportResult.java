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
public class FinancialExportResult implements Serializable {


    /**
     * 订单号
     */
    private String serialNumber;

    /**
     * 关联单号
     */
    private String assocSerialNumber;

    /**
     * 三方订单
     */
    private String thirdNumber;
    /**
     * 销售人
     */
    private String salesMan;

    /**
     * 订单类型CGRK(0),CGTH(1),XSCK(2),XSTH(3)
     */
    private Integer orderType;
    private Integer saleMode;

    /**
     * 订单来源
     */
    private Integer orderOrigin;

    /**
     * 供应商id非联系人id！！！
     */
    private Integer customerId;

    /**
     * 订单数量
     */
    private Integer orderNumber;

    /**
     * 订单金额
     */
    private BigDecimal orderMoney;

    /**
     * 出库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date outStoreTime;

    /**
     * 是否分成
     */
    private Integer divideInto;

    /**
     * 销售渠道
     */
    private Integer clcId;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 备注
     */
    private String remark;


    /**
     * 客户
     */
    private String customerName;

    /**
     * 客户类型
     */
    private Integer customerType;


    /**
     * 订单归宿主体：谁销售的
     */
    private String belongSubjectName;


    //---------------------------详情-------------------------------
    /**
     * 用于查询
     */
    @JsonIgnore
    private Integer belongId;

    /**
     * 商品归属
     */
    private String belongName;


    @JsonIgnore
    private transient Integer locationId;

    /**
     * 所处仓库
     */
    private String locationName;

    /**
     * 活动寄售价
     */
    private BigDecimal promotionConsignmentPrice;
    private Integer stockId;
    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 采购价
     */
    private String purchasePrice;
    /**
     * 成交价
     */
    private String clinchPrice;
    /**
     * 退款金额
     */
    private String returnPrice;
    /**
     * 财务业绩
     */
    private BigDecimal financialPerformance;
    /**
     * 品牌营销费用
     */
    private BigDecimal brandMarketingExpenses;
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

    private String tobPrice;
    private String tocPrice;
    private String tagPrice;
    private String pricePub;
    private Integer stockSrc;
    private String fixPrice;
    /**
     * 寄售成交时间?
     */
    private Date soldTime;
    /**
     * 寄售价(门店采购价)
     */
    private String consignSalePrice;
    /**
     * 寄售价
     */
    private String jsClinchPrice;
    /**
     * 差额
     */
    private String marginPrice;
    /**
     * 转销比
     */
    private String ratio;

    /**
     * 回购服务费
     */
    private String buyBackServiceFee;
    /**
     * 采购主体
     */
    private String purchaseSubject;

    /**
     * 门店采购价
     */
    private String storePurchasePrice;
    private BigDecimal buyBackPrice;
    /**
     * 经营权
     */
    private Integer outletStore;

    /**
     * 销售位置
     */
    private Integer salesPosition;

    /**
     * 商品归属
     */
    private Integer goodsBelong;

    /**
     * 商品位置
     */
    private Integer goodsPosition;

    /**
     * 采购主体id
     */
    @JsonIgnore
    private Integer sourceSubjectId;
    private Integer demandId;
    private String demandStoreStr;
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


    /**
     * 服务费
     */
    private String serviceFee;


    /**
     * 商品编码
     */
    private String wno;


}
