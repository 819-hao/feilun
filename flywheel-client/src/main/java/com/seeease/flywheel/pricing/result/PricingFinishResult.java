package com.seeease.flywheel.pricing.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@Data
public class PricingFinishResult implements Serializable {

    private Integer id;

    private String serialNo;

    private Integer stockId;


}
