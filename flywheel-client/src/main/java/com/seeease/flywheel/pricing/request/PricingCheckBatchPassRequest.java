package com.seeease.flywheel.pricing.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/15 19:37
 */
@Data
public class PricingCheckBatchPassRequest implements Serializable {

    private List<Integer> pricingIdList;
}
