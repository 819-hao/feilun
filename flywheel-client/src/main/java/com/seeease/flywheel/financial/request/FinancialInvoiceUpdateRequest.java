package com.seeease.flywheel.financial.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
public class FinancialInvoiceUpdateRequest implements Serializable {

    private Integer id;

    private Integer state;

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
    private String remarks;
}
