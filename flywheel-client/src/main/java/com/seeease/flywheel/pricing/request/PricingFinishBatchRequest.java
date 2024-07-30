package com.seeease.flywheel.pricing.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/6/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingFinishBatchRequest implements Serializable {
    private List<PricingFinishRequest> requestList;
}
