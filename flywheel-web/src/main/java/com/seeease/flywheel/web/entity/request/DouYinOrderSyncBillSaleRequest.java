package com.seeease.flywheel.web.entity.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/7/10
 */
@Data
public class DouYinOrderSyncBillSaleRequest implements Serializable {
    /**
     * 抖音订单ids
     */
    private List<String> douYinOrderIdList;
}
