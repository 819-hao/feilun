package com.seeease.flywheel.financial.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2024/3/5
 */
@Data
public class HfTradeFlowSaveRequest implements Serializable {

    /**
     * 扫码交易单号(支付宝、微信交易号)
     */
    private String tradeNo;

    /**
     * 交易金额(分)
     */
    private String ordAmt;

    /**
     * 支付方式
     */
    private String mobilePayType;

    /**
     * 商户号
     */
    private String memberId;

    /**
     * 商户名称
     */
    private String merName;

    /**
     * 交易时间
     */
    private String transDateTime;

    /**
     * 终端号
     */
    private String deviceId;

    /**
     *
     */
    private String termOrdId;

    private String ordId;
    private String partOrderId;

    /**
     * 备注
     */
    private String remarks;
}
