package com.seeease.flywheel.goods.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
public class ScrapOrderPageResult implements Serializable {

    /**
     * $column.columnComment
     */
    private Integer id;
    /**
     *
     */
    private String serialNo;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 报废原因
     */
    private String scrapReason;
    private String createdTime;
    private String createdBy;

}
