package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
@Data
public class FinancialStatementQueryAllRequest extends PageRequest implements Serializable {

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 流水归属
     */
    private Integer shopId;
    /**
     * 收款主体
     */
    private Integer subjectId;

    /**
     * 流水号
     */
    private String serialNo;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 付款人
     */
    private String payer;

    /**
     * 导出手动选择
     */
    private List<Integer> docBatchIds;


}
