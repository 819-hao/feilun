package com.seeease.flywheel.purchase.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class PurchaseDemandCreateRequest implements Serializable {
    /**
     * 定金金额
     */
    private BigDecimal deposit;
    /**
     * 客户姓名
     */
    private String contactName;
    /**
     * 客户电话
     */
    private String contactPhone;
    /**
     * 客户地址
      */
    private String contactAddress;
    /**
     * 关联单号
     */
    private String serial;
}
