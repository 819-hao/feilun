package com.seeease.flywheel.sale.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.util.List;


/**
 *
 */
@Data
public class SaleOrderSettlementListRequest extends PageRequest {
    /**
     * 客户id
     */
    private Integer customerId;

    private String serialNo;

    private String stockSn;

    private Integer goodsId;

    private Boolean whetherTH = Boolean.FALSE;

    private List<Integer> lineStateList;

}
