package com.seeease.flywheel.purchase.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Data
public class PurchaseDemandPageResult implements Serializable {

    private Integer id;
    /**
     * 需求成色
     */
    private String fineness;

    /**
     * 需求门店
     */
    private String shopName;
    /**
     * 需求附件
     */
    private String attachment;
    /**
     * 定金金额
     */
    private BigDecimal deposit;
    /**
     * 预计销售价
     */
    private BigDecimal sellPrice;
    /**
     * 联系人id
     */
    private Integer customerContactId;
    /**
     * 客户id
     */
    private String customerId;
    /**
     * 客户姓名
     */
    private String contactName;
    /**
     * 客户电话
     */
    private String contactPhone;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 更新人
     */
    private String updatedBy;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedTime;
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
     * 客户地址
     */
    private String contactAddress;
    /**
     * 商场订单号
     */
    private String serial;

}
