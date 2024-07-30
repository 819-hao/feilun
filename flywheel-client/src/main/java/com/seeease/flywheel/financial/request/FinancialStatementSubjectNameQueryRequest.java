package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 退款查询退款主体
 */
@Data
public class FinancialStatementSubjectNameQueryRequest extends PageRequest implements Serializable {

    /**
     * 门店名称
     */
    private String shopName;

    private String originSerialNo;
    private Integer originId;

}
