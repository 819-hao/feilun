package com.seeease.flywheel.recycle.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 回收的明细
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class MallRecycleOrderDetailResult implements Serializable {

    /**
     * id
     */
    private Integer id;

    /**
     * 单号
     */
    private String serialNo;


    /**
     * 原销售单号
     */
    private String saleSerialNo;

    /**
     * 原销售单号商品id
     */
    private Integer stockId;

    /**
     * 供应商表id
     */
    private Integer customerId;

    /**
     * 联系人
     */
    private Integer customerContactId;

    /**
     * 客户经理
     */
    private Integer employeeId;

    /**
     * 品牌id（要采购）
     */
    private Integer brandId;

    /**
     * 采购id
     */
    private Integer purchaseId;

    /**
     * 销售id
     */
    private Integer saleId;

    /**
     * 检测id
     */
    private Integer detectionId;

    /**
     * 年份
     */
    private String year;

    /**
     * 估价价格一次任意文本
     */
    private String valuationPrice;

    /**
     * 第一次估价备注
     */
    private String valuationRemark;

    /**
     * 第一次报价人
     */
    private Integer valuationPriceId;

    /**
     * 估价图片
     */
    private String valuationImage;

    /**
     * 估价价格二次
     */
    private String valuationPriceTwo;

    /**
     * 估价第二次备注
     */
    private String valuationPriceTwoRemark;

    /**
     * 第二次报价人
     */
    private Integer valuationPriceTwoId;

    /**
     * 估价时间
     */
    private Date valuationTime;

    /**
     * 回收协议
     */
    private String agreement;


    private Integer state;

    /**
     * 备注
     */
    private String remark;

    /**
     * 商城图片
     */
    private String shopImage;

    /**
     * 需求门店id
     */
    private Integer demandId;

    /**
     * 估价单主键
     */
    private String assessId;

    /**
     * 类型用来区分来源
     */
    private Integer type;

    /**
     * 区分大类回购还是回购
     */
    private Integer recycleType;

    /**
     * 三方关联单号
     */
    private String bizOrderCode;

    /**
     * 物流单号
     */
    private String expressNumber;

    /**
     * 退货物流单号
     */
    private String deliveryExpressNumber;

    /**
     * 差额
     */
    private BigDecimal balance;

    /**
     * 符号 -1 待打款 0 平账 1 待收款
     */
    private Integer symbol;

    /**
     * 型号id
     */
    private Integer goodsId;

    /**
     * 表带类型
     */
    private String strapMaterial;

    /**
     * 表节
     */
    private String watchSection;

    /**
     * 表径
     */
    private String watchSize;

    /**
     * 银行名称
     */
    private String accountName;

    /**
     * 银行卡号
     */
    private String bankAccount;

    /**
     * 开户银行地址
     */
    private String bank;

    /**
     * 银行客户名称
     */
    private String bankCustomerName;

    /**
     * 证件正面图片（个人采购）
     */
    private String frontIdentityCard;

    /**
     * 证件反面图片（个人采购）
     */
    private String reverseIdentityCard;

    /**
     * 回收聊天记录（个人采购）
     */
    private String recoveryPricingRecord;

    /**
     * 转让协议
     */
    private String agreementTransfer;

    /**
     * 采购表身号
     */
    private String stockSn;

    /**
     * 成色
     */
    private String finess;

    /**
     * 附件字符串
     */
    private String attachment;
    /**
     * 是否有保修卡 0 否
     */
    private Integer isCard;

    /**
     * 响应日期返回
     */
    private String warrantyDate;

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
     * 客户
     */
    private String customerName;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系人id
     */
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
     * 专属顾问名称
     */
    private String employeeName;

    private Object attachmentMap;

    private String originPurchaseSerialNo;

    /**
     * 身份证拼接
     */
    private String identityCard;

    private String otherPicture;

    /**
     * 回显数据
     */
    private Integer seriesId;

    /**
     * 最新回收价
     */
    private BigDecimal latestRecyclePrice;

    /**
     * 最新置换价
     */
    private BigDecimal latestReplacePrice;

    /**
     * 最新估价备注
     */
    private String latestValuationRemark;
    /**
     * 最新估价图片
     */
    private String latestValuationImage;

    /**
     *企业微信id
     */
    private String qwId;

    /**
     * 维修员名称
     */
    private String maintenanceMasterName;

    //表行信息
    private BuyBackForLineResult buyBackForLineResult;


    /**
     * 销售行信息
     */
    private SaleOrderDetailLineResult saleOrderDetailLineResult;

    /**
     * 快递信息
     */
    private BuyBackExpressResult expressResult;
}
