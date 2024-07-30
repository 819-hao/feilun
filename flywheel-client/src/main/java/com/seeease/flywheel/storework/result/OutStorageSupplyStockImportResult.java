package com.seeease.flywheel.storework.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/4/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutStorageSupplyStockImportResult implements Serializable {

    /**
     * 作业单id集合
     */
    private Integer workId;
}
