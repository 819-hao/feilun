package com.seeease.flywheel.express.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单发货通知
 *
 * @author Tiro
 * @date 2023/4/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeilunInvoiceMaycurUpdateMessage implements Serializable {
    /**
     * 开票抬头
     */
    private String invoiceTitle;
    /**
     * 开票主体 开票公司
     */
    private String invoiceSubject;

    /**
     * 发票类型
     * ZP(1, "专票"),
     * PP(2, "普票"),
     */
    private Integer invoiceType;

    /**
     * 开票类型
     * GR(1, "个人"),
     * QY(2, "企业"),
     */
    private Integer invoiceOrigin;
    /**
     * 单位税号
     */
    private String unitTaxNumber;

    /**
     * 飞轮开票号
     */
    private String serialNo;

    /**
     *
     */
    private String remarks;

    /**
     * 付款方式
     */

    private Integer paymentMethod;

}
