package com.seeease.flywheel.recycle.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 回购查询返回
 */
@Data
@Accessors(chain=true)
public class BuyBackForSaleResult implements Serializable {

    //主键id
    private Integer id;
    /**
     * 商品id
     */
    private Integer stockId;
    /**
     * 单号
     */
    private String serial;
    /**
     * 客户经理名称
     */
    private String employeeName;
    /**
     *客户经理id
     */
    private Integer employeeId;
    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户电话
     */
    private String customerPhone;
    //供应商id
    private Integer customerId;
    /**
     * 客户id
     */
    private Integer customerContactId;
    //类型用来区分来源：回收还是置换
    private Integer type;

    //用来区分大类：回收还是回购
    private Integer recycleType;
    /**
     * 地址
     */
    private String address;
    /**
     *开户银行
     */
    private String bank;
    /**
     * 银行账号
     */
    private String bankAccount;
    /**
     * 银行名称
     */
    private String accountName;
    /**
     * 收款人名称
     */
    private String payee;
    /**
     * 快递信息
     */
    private String expressNo;
    /**
     * 身份证图片
     */
    private String identityCardImage;//身份证图片
    /**
     * 需求门店名称
     */
    private String demandName;

    /**
     * 需求门店id
     */
    private Integer demandId;
    /**
     * 商城图片
     */
    private String shopImage;
    /**
     * 商品主图
     */
    private String goodsImage;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 创建人
     */
    private String createdBy;
    /**
     * @see com.seeease.flywheel.serve.recycle.enums.RecycleStateEnum
     * 状态枚举
     */
    private Integer state;
    /**
     * 状态中文描述
     */
    private String statusDesc;
    /**
     *企业微信id
     */
    private String qwId;
    /**
     * 原销售价格
     */
    private BigDecimal originalClinchPrice;
    /**
     * -1 待打款 0 平账 1 待收款
     */
    private Integer symbol;

    /**
     * 差额值
     */
    private BigDecimal balance;
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
