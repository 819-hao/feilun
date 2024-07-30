package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class ApplyFinancialPaymentQueryByConditionRequest extends PageRequest {

    /**
     * 是否被使用
     */
    private Integer whetherUse;

    /**
     * 打款时间
     */
    private String createdStartTime;
    private String createdEndTime;
    /**
     * 打款人
     */
    private String operator;

    /**
     * 关联单号
     */
    private String purchaseSerialNo;

    private String serialNo;

    private Integer state;

    private Integer typePayment;

    /**
     * 创建人
     */
    private String applicant;

    private String applicantStartTime;
    private String applicantEndTime;

    /**
     * 导出手动选择
     */
    private List<Integer> docBatchIds;

    /**
     * 订单来源
     */
    private Integer shopId;
    /**
     * 是否重复
     */
    private Integer whetherRepeat;

    private Integer saleMode;
    private Integer demandId;
}
