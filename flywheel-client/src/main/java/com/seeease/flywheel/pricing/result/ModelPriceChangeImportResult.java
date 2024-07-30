package com.seeease.flywheel.pricing.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 采购
 * @Date create in 2023/3/31 10:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelPriceChangeImportResult implements Serializable {

    /**
     * 表身号
     */
    private String stockSn;

    private Integer stockId;
    private Integer goodsId;

}
