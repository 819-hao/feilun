package com.seeease.flywheel.serve.purchase.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.seeease.flywheel.serve.base.BusinessBillStateEnum;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchasePaymentMethodEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseTypeEnum;
import com.seeease.flywheel.serve.purchase.enums.RecycleModeEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import com.seeease.seeeaseframework.mybatis.type.JsonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购单
 *
 * @TableName bill_purchase
 */
@TableName(value = "bill_purchase", autoResultMap = true)
@Data
public class BillPurchase extends BaseDomain implements TransitionStateEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 采购单号
     */
    private String serialNo;

    /**
     * 采购类型
     */
    private PurchaseTypeEnum purchaseType;

    /**
     * 采购方式
     */
    private PurchaseModeEnum purchaseMode;

    /**
     * 支付方式
     */
    private PurchasePaymentMethodEnum paymentMethod;

    /**
     * 订金百分比
     */
    private BigDecimal depositPercentage;

    /**
     * 采购来源
     */
    private BusinessBillTypeEnum purchaseSource;

    /**
     * 申请打款单（同行采购，同行寄售，个人寄售，个人回购）
     */
    private String applyPaymentSerialNo;

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
     * 采购单状态
     */
    @TransitionState
    private BusinessBillStateEnum purchaseState;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 本次销售单号（个人置换，个人回购）
     */
    private String saleSerialNo;

    /**
     * 本次销售价（个人回购，个人置换）
     */
    private BigDecimal salePrice;

    /**
     * 关联销售单号（个人回购）
     */
    private String originSaleSerialNo;

    /**
     * 到期时间（个人寄售）
     */
    private String dealEndTime;

    /**
     * 签订时间（个人寄售）
     */
    private String dealBeginTime;

    /**
     * 寄售时间(天为单位)
     */
    private String consignmentTime;

    /**
     * 回收来源（个人回收）
     */
    private RecycleModeEnum recycleModel;

    /**
     * 正面身份证
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> frontIdentityCard;
    /**
     * 反面身份证
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> reverseIdentityCard;

    /**
     * 转让协议
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> agreementTransfer;

    /**
     * 回收定价记录
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> recoveryPricingRecord;
    /**
     * 回购协议
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> buyBackTransfer;
    /**
     * 批量图片上传url
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> batchPictureUrl;
    /**
     * 商品图片
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> imgList;

    /**
     * 采购备注
     */
    private String remarks;

    /**
     * 采购数量
     */
    private Integer purchaseNumber;

    @TableField(value = "is_settlement", updateStrategy = FieldStrategy.IGNORED)
    private WhetherEnum isSettlement;

    /**
     * 门店id
     */
    private Integer storeId;

    /**
     * 门店标记
     */
    private WhetherEnum storeTag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;

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

    private Integer prePayment;

    private Integer recyclerId;
}