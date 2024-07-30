package com.seeease.flywheel.stocktaking.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/6/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakingSubmitResult implements Serializable {

    /**
     * 盘点单
     */
    private String serialNo;
}
