package com.seeease.flywheel.pricing.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/4/11 15:03
 */
@Data
public class PricingCreateByStockRequest implements Serializable {

    private List<Integer> stockIdList;
}
