package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class FinancialStatementMiniPageQueryRequest extends PageRequest implements Serializable {

    /**
     * 金额或者付款人
     */
    private String searchCriteria;

    /**
     * 流水归属
     */
    private Integer shopId;

}
