package com.seeease.flywheel.purchase.request;

import com.seeease.flywheel.financial.result.ApplyFinancialPaymentDetailResult;
import com.seeease.flywheel.sale.result.SaleOrderDetailsResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 采购创建的基类
 *
 * @author Tiro
 * @date 2023/1/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseCreateRequest implements Serializable {

    /**
     * 采购类型
     */
    private Integer purchaseType;

    /**
     * 采购方式
     */
    private Integer purchaseMode;

    //**********************前置********************
    /**
     * 订金百分比
     */
    private BigDecimal depositPercentage;

    /**
     * 支付方式
     */
    private Integer paymentMethod;
    /**
     * 采购来源
     */
    private Integer purchaseSource;

    /**
     * 单号
     */
    private String serialNo;

    //**********************前置********************

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 客户联系人
     */
    private Integer customerContactId;

    /**
     * 采购主体id
     */
    private Integer purchaseSubjectId;

    /**
     * 流转主体id
     */
    private Integer viaSubjectId;

    /**
     * 需方id
     */
    private Integer demanderStoreId;

    /**
     * 总采购成本
     */
    private BigDecimal totalPurchasePrice;

    /**
     * 图片
     */
    private List<String> imgList;

    /**
     * 申请打款单
     */
    private String applyPaymentSerialNo;


    /**
     * 签订时间（个人寄售）
     */
    private String dealBeginTime;

    /**
     * 到期时间（个人寄售）
     */
    private String dealEndTime;

    /**
     * 寄售时间(天为单位)
     */
    private String consignmentTime;

    /**
     * 本次销售单号（个人置换，个人回购）
     */
    private String saleSerialNo;

    /**
     * 关联销售单号（个人回购）
     */
    private String originSaleSerialNo;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 单据详情
     */
    private List<BillPurchaseLineDto> details;

    /**
     * 正面身份证
     */
    private List<String> frontIdentityCard;
    /**
     * 批量图片上传url
     */
    private List<String> batchPictureUrl;
    /**
     * 反面身份证
     */
    private List<String> reverseIdentityCard;

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

    /**
     * 回收来源
     */
    private Integer recycleModel;


    /**
     * 本次销售价（个人回购，个人置换）
     */
    private BigDecimal salePrice;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillPurchaseLineDto implements Serializable {

        /**
         * 商品型号id
         */
        private Integer goodsId;
        private Integer seriesType;
        /**
         * 商品编码
         */
        private String wno;

        /**
         * 表身号
         */
        private String stockSn;

        /**
         * 成色
         */
        private String finess;

        /**
         * 附件列表
         */
        private List<Integer> attachmentList;

        /**
         * 采购价
         */
        private BigDecimal purchasePrice;

        /**
         * 回收价
         */
        private BigDecimal recyclePrice;

        /**
         * 寄售协议价
         */
        private BigDecimal dealPrice;

        /**
         * 本次销售价（个人回购，个人置换）
         */
        private BigDecimal salePrice;

        /**
         * 表节
         */
        private String watchSection;

        private String strapMaterial;

        /**
         * 版本,和表身号构成唯一索引
         */
        private Integer edition;

        /**
         * 商品级别
         */
        private String goodsLevel;

        /**
         * 销售等级
         */
        private Integer salesPriority;

        /**
         * 备注
         */
        private String remarks;

        private String warrantyDate;

        /**
         * 0 无 1 空白 1 有
         */
        private Integer isCard;

        /**
         * 采购附件详情
         */
        private Map<String, List<Integer>> attachmentMap;

        /**
         * 关联商品id
         */
        private Integer originStockId;

        /**
         * 实际维修费（个人寄售）
         */
        private BigDecimal fixPrice;

        /**
         * 预计维修费（个人寄售，个人回购）
         */
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
         * 寄售成交价（回购）寄售价（个人回购，寄售到门店价格）
         */
        private BigDecimal consignmentPrice;

        /**
         * 表带更换费（回购）
         */
        private BigDecimal watchbandReplacePrice;

        /**
         * 寄售成交价（回购）
         */
        private BigDecimal clinchPrice;

        /**
         * NULL 已结束
         * 0 不可结算
         * 1 可结算
         */
        private Integer isSettlement;


        private Integer whetherFix;

        /**
         * 选择的折扣
         */
        private String selectedDiscount;
        /**
         *  物鱼供货价
         */
        private BigDecimal wuyuPrice;

        /**
         *  兜底价
         */
        private BigDecimal wuyuBuyBackPrice;
    }

    /**
     * 当前门店必填 后端塞入
     */
    private Integer storeId;

    /**
     * 打款单相关内容 pre
     */
    private ApplyFinancialPaymentDetailResult applyFinancialPaymentDetailResult;

    /**
     * 本次销售单号
     */
    private SaleOrderDetailsResult saleOrderDetailsResult;

    /**
     * 关联销售单号
     */
    private SaleOrderDetailsResult originSaleOrderDetailsResult;

    //5。25需求新增客户账号
    /**
     * 开户行 开户地址
     */
    private String bank;

    /**
     * 银行名称 中国建行
     */
    private String accountName;

    /**
     * 银行账号 就是卡号
     */
    private String bankAccount;

    /**
     * 银行客户名称 首款人名称
     */
    private String bankCustomerName;

    /**
     * 实际采购人
     */
    private Integer purchaseId;

    /**
     * 商城使用。用来跳过前置打款和校验
     */
    private Boolean mallUser = Boolean.TRUE;
    /**
     * 前置打款 默认 null 针对于个人回收-置换
     * 个人回收必填
     * 同行采购选填
     * 0 否 1 是
     */
//    private Integer prePayment;

    /**
     * 采购任务ID
     */
    private Integer purchaseTaskId;

    private Integer recyclerId;
}
