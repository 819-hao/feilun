package com.seeease.flywheel.serve.financial.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "financial_tx_history", autoResultMap = true)
@Data
public class FinancialTxHistory extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     *编号
     */
    private String serial;
    /**
     *名称
     */
    private String name;
    /**
     *纳税人识别号
     */
    private String tiCode;
    /**
     *地址
     */
    private String address;
    /**
     *电话
     */
    private String phone;
    /**
     *开户行'
     */
    private String accountBank;
    /**
     *账号
     */
    private String account;
    /**
     *销售方名称
     */
    private String sellerName;
    /**
     *身份证号码
     */
    private String idCard;
    /**
     *商品编码
     */
    private String sn;
    /**
     *商品名称规格
     */
    private String productTitle;
    /**
     *成色
     */
    private String fineness;
    /**
     *金额
     */
    private String amount;
    /**
     *合同编号
     */
    private String contractCode;
    /**
     *交易日期
     */
    private Date txTime;
    /**
     *物流公司
     */
    private String logisticsFirm;
    /**
     *单号
     */
    private String code;
    /**
     *支付通道
     */
    private String payChannel;
    /**
     *收款账号
     */
    private String receiveAccount;
    /**
     *流水号
     */
    private String txCode;
    /**
     *金额大写
     */
    private String amountUppercase;
    /**
     *采购员
     */
    private String buyer;
    /**
     *制证人员
     */
    private String certifier;
    /**
     *制证时间
     */
    private Date certifierTime;
    /**
     * 备注
     */
    private String remark;
    /**
     *销售方电话
     */
    private String sellerPhone;


}
