package com.seeease.flywheel.sale.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

/**
 *
 */
@Data
public class SaleOrderRecycleListRequest extends PageRequest {

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 销售类型
     */
    private Integer saleType;
}
