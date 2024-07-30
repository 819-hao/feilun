package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class FinancialInvoiceReverseQueryByConditionRequest extends PageRequest {

    /**
     * 创建时间
     */
    private String createdStartTime;
    private String createdEndTime;


    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 申请开票单号
     */
    private String serialNo;

    /**
     * 状态 0 待开始 1 进行中 2 已完成
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
     * 开票主体
     */
    private Integer invoiceSubject;

}
