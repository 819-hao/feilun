package com.seeease.flywheel.rfid.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfidStockSnMatchResult {

    /**
     * 品牌
     */
    private String brandName;
    /**
     * 系列
     */
    private String seriesName;
    /**
     * 型号
     */
    private String model;
    /**
     * 商品编码
     */
    private String wno;

}
