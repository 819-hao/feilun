package com.seeease.flywheel.goods.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/8/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockManageInfoImportResult implements Serializable {

    private Integer id;
    private Integer stockId;
}