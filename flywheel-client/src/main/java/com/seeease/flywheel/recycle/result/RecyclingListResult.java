package com.seeease.flywheel.recycle.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 回收和回购实体
 *
 * @Auther Gilbert
 * @Date 2023/8/30 20:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecyclingListResult implements Serializable {

    private Integer id;
    //批次号
    private String serial;
    //商品id
    private Integer stockId;
    //品牌id
    private Integer brandId;
    /**
     * 采购id
     */
    private Integer purchaseId;
    //如果是回购。原飞轮销售单号
    private String saleSerialNo;
    //供应商id
    private Integer customerId;
    //客户id
    private Integer customerContactId;
    //客户经理id
    private Integer employeeId;
    //第一次估价
    private String valuationPrice;
    //第一次估价备注
    private String valuationRemark;
    //第一次报价人id
    private Integer valuationPriceId;
    //估价图片
    private String valuationImage;
    //第二次估价
    private String valuationPriceTwo;
    //第二次估价备注
    private String valuationPriceTwoRemark;
    //第二次报价人id
    private Integer valuationPriceTwoId;

    /**
     * 上传图片
     */
    private String valuationImageTwo;

    /**
     * 二次估价时间
     */
    private String valuationTimeTwo;
    //估价时间
    private String valuationTime;

    //状态
    private String state;
    //给商城用的状态
    private Integer mallState;
    //商城图片
    private String shopImage;
    //需求门店id
    private Integer demandId;
    //类型用来区分来源：回收还是置换
    private Integer type;
    //用来区分大类：回收还是回购
    private Integer recycleType;
    //三方关联单号
    private String bizOrderCode;
    //表身号
    private String stockSn;
    /**
     * 需求门店名称
     */
    private String demandName;

    /**
     * 客户经理名称
     */
    private String employeeName;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户电话
     */
    private String customerPhone;
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
     * 成色
     */
    private String finess;
    //状态
    private String statusDesc;
    /**
     * 创建时间
     */
    private String createdTime;
    /**
     * 是否有保修卡 0 否
     */
    private Integer isCard;

    /**
     * 响应日期返回
     */
    private String warrantyDate;

    /**
     * 附件
     */
    private Object attachmentLabel;

    private Object dictChildList;
    /**
     * 公价
     */
    private BigDecimal pricePub;
    /**
     * 差额
     */
    private BigDecimal balance;

    /**
     * 符号 -1 待打款 0 平账 1 待收款
     */
    private Integer symbol;


    /**
     * 主图
     */
    private String image;

    /**
     * 采购行信息
     */
    private BuyBackForLineResult buyBackForLineResult;

    /**
     * 销售行信息
     */
    private SaleOrderDetailLineResult saleOrderDetailLineResult;

    /**
     * 快递信息
     */
    private BuyBackExpressResult expressResult;

    /**
     * 附件字符串
     */
    private String attachment;

    /**
     * 最新回收价
     */
    private BigDecimal latestRecyclePrice;

    /**
     * 最新置换价
     */
    private BigDecimal latestReplacePrice;

    /**
     * 最新报价时间
     */
    private String latestValuationTime;

    private String attachmentList;

    private String remark;

    private String lineState;

}
