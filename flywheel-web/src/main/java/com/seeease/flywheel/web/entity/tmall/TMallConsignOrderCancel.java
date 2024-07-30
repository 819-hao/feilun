package com.seeease.flywheel.web.entity.tmall;

/**
 * @author Tiro
 * @date 2023/3/24
 */

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 天猫寄售订单-取消-通知xml对象
 *
 * @author Tiro
 * @date 2023/3/24
 */
@XmlRootElement(name = "request")
@Data
public class TMallConsignOrderCancel {
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
     * 履约单号
     */
    @XmlTransient
    private String bizOrderCode;

    /**
     * 来源平台，TMGJZY:天猫国际直营
     */
    @XmlTransient
    private String sourcePlatformCode;

    /**
     * 1 -用户申请取消 9 -用户申请取消且已经同意退款。
     */
    @XmlTransient
    private Integer refundStatus;

    /**
     * 0:商家仓商家配,2:商家仓菜鸟配
     */
    @XmlTransient
    private String businessModel;
}
