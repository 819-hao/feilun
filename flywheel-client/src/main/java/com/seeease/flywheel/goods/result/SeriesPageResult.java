package com.seeease.flywheel.goods.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
public class SeriesPageResult implements Serializable {

    /**
     * $column.columnComment
     */
    private Integer id;

    /**
     * 系列名称
     */
    private String name;

    /**
     * 品牌id
     */
    private Integer brandId;

    /**
     * 俗称
     */
    private String vulgo;

    /**
     * 型号个数
     */
    private Integer modelNum;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     *
     */
    private Integer type;

}
