package com.seeease.flywheel.sale.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
@Data
public class DouYinOrderConsolidationRequest implements Serializable {

    /**
     * 订单ids
     */
    private List<Integer> ids;
}
