package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class FinancialInvoiceQueryByConditionRequest extends PageRequest {

    /**
     * 创建时间
     */
    private String createdStartTime;
    private String createdEndTime;
    /**
     * 开票时间
     */
    private String invoiceStartTime;
    private String invoiceEndTime;

    /**
     * 开票人
     */
    private String invoiceUser;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 申请开票单号
     */
    private String serialNo;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 导出手动选择
     */
    private List<Integer> docBatchIds;

    /**
     * 订单来源
     */
    private Integer shopId;

    /**
     * 订单类型
     */
    private Integer orderType;

    private String customerName;
    private String stockSn;

    /**
     * 开票主体
     */
    private Integer invoiceSubject;

    /**
     * 开票抬头
     */
    private String invoiceTitle;
}
