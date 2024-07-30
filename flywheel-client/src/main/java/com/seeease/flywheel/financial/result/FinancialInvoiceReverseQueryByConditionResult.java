package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialInvoiceReverseQueryByConditionResult implements Serializable {

    private Integer id;

    /**
     *
     */
    private Integer stockId;
    private String stockSn;
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
     *
     */
    private BigDecimal originPrice;

    /**
     *
     */
    private String originSerialNo;

    /**
     * 开票号码
     */
    private String invoiceNumber;

    /**
     * 开票主体
     */
    private Integer invoiceSubject;
    private Integer shopId;
    private String invoiceSubjectName;
    private String shopName;
    /**
     *
     */
    private Integer state;

    /**
     *
     */
    private Integer fiId;

    /**
     *
     */
    private String fiSerialNo;

    /**
     * 创建人
     */
    private String createdBy;
    /**
     * 创建时间
     */
    private String createdTime;
}
