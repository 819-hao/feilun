package com.seeease.flywheel.pricing.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/11/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockGuaranteeCardManageImportResult implements Serializable {
    private Integer id;


}
