package com.seeease.flywheel.pricing.result;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingFinishBatchResult implements Serializable {

    List<PricingFinishResult> resultList;
}
