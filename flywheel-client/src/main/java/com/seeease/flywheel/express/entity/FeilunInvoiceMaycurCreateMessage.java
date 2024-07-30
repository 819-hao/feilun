package com.seeease.flywheel.express.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
public class FeilunInvoiceMaycurCreateMessage implements Serializable {
    /**
     * 开票抬头
     */
    private String invoiceTitle;
    /**
     * 开票主体 开票公司
     */
    private String invoiceSubject;
    /**
     * 申请人——企业微信的拼音
     */
    private String invoiceUser;
    /**
     * 销售平台—可空
     */
    private String saleChannel;
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
     * 开户银行 —可空
     */
    private String bank;

    /**
     * 银行账户 —可空
     */
    private String bankAccount;
    /**
     * 电话 ——空
     */
    private String phone;
    /**
     * 地址 —可空
     */
    private String address;
    /**
     * 购买日期——可空
     */
    private Date buyTime;
    /**
     * 购买人姓名—customerName
     */
    private String customerName;
    /**
     * 收票邮箱—可空
     */
    private String customerEmail;
    /**
     * 飞轮开票号
     */
    private String serialNo;

    /**
     * 付款方式
     */

    private Integer paymentMethod;

    /**
     *
     */
    private String remarks;
    /**
     * 单据详情
     */
    private List<LineDto> lines;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineDto implements Serializable {

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
         * 表身号
         */
        private String stockSn;

        /**
         * 开票金额
         */
        private BigDecimal invoiceAmount;

        /**
         * 库存id
         */
        private Integer stockId;

    }
}
