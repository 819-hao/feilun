package com.seeease.flywheel.web.entity.tmall;

import lombok.Data;

import javax.xml.bind.annotation.XmlTransient;

/**
 * 天猫收件信息
 *
 * @author Tiro
 * @date 2023/3/24
 */
@Data
public class TMallReceiverInfo {

    /**
     * 收件⽅邮编
     */
    @XmlTransient
    private String receiverZipCode;
    /**
     * 收件⽅国家
     */
    @XmlTransient
    private String receiverCountry;

    /**
     * 收件⽅省份
     */
    @XmlTransient
    private String receiverProvince;
    /**
     * 收件⽅城市
     */
    @XmlTransient
    private String receiverCity;
    /**
     * 收件⽅区县
     */
    @XmlTransient
    private String receiverArea;

    /**
     * 收件⽅镇
     */
    @XmlTransient
    private String receiveTown;

    /**
     * 收件⽅地址
     */
    @XmlTransient
    private String receiverAddress;

    /**
     * 收件⼈名称
     */
    @XmlTransient
    private String receiverName;
    /**
     * 收件⼈⼿机
     */
    @XmlTransient
    private String receiverMobile;

    @XmlTransient
    private String oaidSourceCode;
    @XmlTransient
    private String oaid;
}