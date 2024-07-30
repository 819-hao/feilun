package com.seeease.flywheel.storework.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkOutStorageSupplyStockResult implements Serializable {

    /**
     * 作业单id集合
     */
    private List<Integer> workIds;
}
