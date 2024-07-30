package com.seeease.flywheel.sale.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
@Data
public class B3SaleReturnOrderAddRemarkRequest implements Serializable {

    /**
     * 主键id
     */
    private List<Integer> ids;
    /**
     * 备注
     */
    private String remark;



}
