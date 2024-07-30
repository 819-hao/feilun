package com.seeease.flywheel.goods.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/9/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockManageShelvesInfoListResult implements Serializable {

    /**
     * 主图
     */
    private String image;
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
     * 速记码
     */
    private String shelvesSimplifiedCode;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private String createdTime;
}
