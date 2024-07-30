package com.seeease.flywheel.financial.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialInvoiceQueryByConditionResult implements Serializable {

    private Integer id;

    /**
     *
     */
    private String serialNo;
    private String originalInvoiceSerialNo;

    /**
     * 开票金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 开票号码
     */
    private String invoiceNumber;

    /**
     * 开票时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date invoiceTime;

    /**
     * 开票人
     */
    private String invoiceUser;
    private String batchPictureUrl;

    /**
     *
     */
    private Integer totalNumber;

    /**
     * 开票主体
     */
    private Integer invoiceSubject;
    private String invoiceSubjectName;
    /**
     * 客户联系人id
     */
    private Integer customerContactsId;
    private Integer customerId;

    private String customerEmail;
    private String customerName;
    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 开票抬头
     */
    private String invoiceTitle;

    /**
     * 单位税号
     */
    private String unitTaxNumber;

    /**
     * 发票类型
     */
    private Integer invoiceType;

    /**
     * 开票类型
     */
    private Integer invoiceOrigin;

    /**
     *
     */
    private Integer state;
    private String remarks;
    /**
     * 申请人门店
     */
    private Integer shopId;
    private String shopName;

    /**
     * 创建人
     */
    private String createdBy;
    private String createdTime;
}
