package com.seeease.flywheel.web.entity.tmall;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 天猫寄售订单-发货-通知xml对象
 *
 * @author Tiro
 * @date 2023/3/24
 */
@XmlRootElement(name = "request")
@Data
public class TMallConsignOrderNotify {
    @XmlTransient
    private String bizType;

    /**
     * 供应商id
     */
    @XmlTransient
    private String supplierId;

    /**
     * 供应商名称
     */
    @XmlTransient
    private String supplierName;

    /**
     * 平台来源[201 淘宝]
     */
    @XmlTransient
    private String orderSource;
    /**
     * 履约单号
     */
    @XmlTransient
    private String bizOrderCode;
    /**
     * 履约单⼦单
     */
    private TMallOrderItems orderItems;
    /**
     * 收件⽅信息
     */
    private TMallReceiverInfo receiverInfo;

    /**
     * 0:商家仓商家配,2:商家仓菜鸟配
     */
    @XmlTransient
    private String businessModel;
    @XmlTransient
    private String sellerId;
    @XmlTransient
    private String sellerNick;
    @XmlTransient
    private String orderCreateTime;
    /**
     * 运费⾦额(单位分)
     */
    @XmlTransient
    private String postFee;
    @XmlTransient
    private String sourcePlatformCode;
    /**
     * 交易金额，单位：分
     */
    @XmlTransient
    private Integer itemsValue;
    /**
     * 发货仓编码
     */
    @XmlTransient
    private String storeCode;
}
