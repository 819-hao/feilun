package com.seeease.flywheel.goods.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2024/1/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWatchUpdateImportResult implements Serializable {
    private Integer id;
    private String modelCode;
}
