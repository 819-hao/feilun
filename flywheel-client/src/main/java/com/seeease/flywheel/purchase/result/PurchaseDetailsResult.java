package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author trio
 * @date 2023/1/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDetailsResult implements Serializable {

    /**
     * 采购id
     */
    private Integer id;

    /**
     * 采购类型
     */
    private Integer purchaseType;

    /**
     * 采购方式
     */
    private Integer purchaseMode;

    /**
     * 支付方式
     */
    private Integer paymentMethod;

    /**
     * 订金百分比
     */
    private BigDecimal depositPercentage;

    /**
     * 采购单号
     */
    private String serialNo;

    /**
     * 总采购成本
     */
    private BigDecimal totalPurchasePrice;

    /**
     * 采购单状态
     */
    private Integer purchaseState;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 图片
     */
    private List<String> imgList;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 采购主体
     */
    private String purchaseSubjectName;

    private Integer purchaseSubjectId;

    /**
     * 流转主体
     */
    private String viaSubjectName;

    private Integer viaSubjectId;

    /**
     * 需求方
     */
    private String demanderStoreName;

    private Integer demanderStoreId;

    /**
     * 客户
     */
    private String customerName;

    private Integer customerId;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 开户银行
     */
    private String bank;

    /**
     * 银行账户
     */
    private String bankAccount;

    /**
     * 联系人
     */
    private String contactName;

    private Integer contactId;

    /**
     * 联系地址
     */
    private String contactAddress;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 创建人
     */
    private String createdBy;


    /**
     * 签订时间（个人寄售）
     */
    private String dealBeginTime;

    /**
     * 到期时间（个人寄售）
     */
    private String dealEndTime;

    /**
     * 本次销售单号（个人置换，个人回购）
     */
    private String saleSerialNo;

    /**
     * 关联销售单号（个人回购）
     */
    private String originSaleSerialNo;

    /**
     * 正面身份证
     */
    private List<String> frontIdentityCard;
    /**
     * 反面身份证
     */
    private List<String> reverseIdentityCard;
    /**
     * 批量图片上传url
     */
    private List<String> batchPictureUrl;

    /**
     * 转让协议
     */
    private List<String> agreementTransfer;

    /**
     * 回收定价记录
     */
    private List<String> recoveryPricingRecord;
    /**
     * 回购协议
     */
    private List<String> buyBackTransfer;

    private Integer isSettlement;


    /**
     * 申请打款单
     */
    private String applyPaymentSerialNo;

    /**
     * 回收来源（个人回收）
     */
    private Integer recycleModel;


    /**
     * 差额
     */
    private BigDecimal difference;

    private BigDecimal salePrice;

    private Integer differenceType;

    /**
     * 实际采购人
     */
    private String purchaseBy;

    /**
     * 采购行信息
     */
    private List<PurchaseLineVO> lines;

    /**
     * 银行客户名称 首款人名称
     */
    private String bankCustomerName;

    /**
     * 身份证号
     */
    public String identityCard;

    @Data
    public static class PurchaseLineVO implements Serializable {
        /**
         * 详情id
         */
        private Integer id;

        /**
         * 采购单号
         */
        private String serialNo;

        private Integer stockId;

        /**
         * 商品编码
         */
        private String wno;

        /**
         * 表身号
         */
        private String stockSn;
        /**
         * 老的sn
         */
        private String oldStockSn;
        private String oldModel;
        /**
         * 成色
         */
        private String finess;

        /**
         * 附件列表
         */
//        private String attachmentList;
        private String oldAttachment;
        /**
         * 采购价
         */
        private BigDecimal purchasePrice;
        private BigDecimal oldPurchasePrice;

        /**
         * 采购单行状态
         */
        private Integer purchaseLineState;

        /**
         * 表节
         */
        private String watchSection;

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

        /**
         * 型号id
         */
        private Integer goodsId;

        private String attachment;


        private BigDecimal recyclePrice;

        private BigDecimal salePrice;

        private String remarks;

        private String strapMaterial;

        private BigDecimal dealPrice;

        private BigDecimal fixPrice;

        private BigDecimal planFixPrice;
        private BigDecimal oldPlanFixPrice;

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

        /**
         * 表带更换费（回购）
         */
        private BigDecimal watchbandReplacePrice;
        private BigDecimal oldWatchbandReplacePrice;

        /**
         * 寄售成交价（回购）
         */
        private BigDecimal clinchPrice;

        private String originPurchaseReturnSerialNo;

        private String warrantyDate;

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

    private Integer prePayment;


    private Integer recyclerId;


    private String recyclerName;

}
