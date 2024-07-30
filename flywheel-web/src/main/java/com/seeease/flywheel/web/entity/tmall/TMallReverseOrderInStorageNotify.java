package com.seeease.flywheel.web.entity.tmall;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 天猫寄售销退订单-入库-通知xml对象
 *
 * @author Tiro
 * @date 2023/3/24
 */
@XmlRootElement(name = "request")
@Data
public class TMallReverseOrderInStorageNotify {

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
     * 逆向单号
     */
    @XmlTransient
    private String bizOrderCode;

    /**
     * 正向单号，对应销售通知单号
     */
    @XmlTransient
    private String forwardOrderCode;


    /**
     * 退回收件人信息(商家)
     */
    private TMallReceiverInfo receiverInfo;

    /**
     * 退回订单货品信息列表
     */
    private TMallOrderItems orderItems;

    /**
     * 退回寄件人信息(消费者)
     */
    private TMallSenderInfo senderInfo;

    /**
     * 0:商家仓商家配,2:商家仓菜鸟配
     */
    @XmlTransient
    private String businessModel;

    /**
     * 外部业务号
     */
    @XmlTransient
    private String outBizId;
    /**
     * 快递公司code
     */
    @XmlTransient
    private String tmsServiceCode;
    /**
     * 运单号
     */
    @XmlTransient
    private String tmsOrderCode;
    /**
     * 来源平台,TMGJZY:天猫国际直营
     */
    @XmlTransient
    private String sourcePlatformCode;
    /**
     * 退回仓编码
     */
    @XmlTransient
    private String storeCode;

}
