package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapStockPageRequest extends PageRequest {

    private String stockSn;

    private String wno;

    private String model;

    /**
     * 品牌
     */
    private String brandName;
    /**
     * 系列
     */
    private String seriesName;

    private Integer stockSrc;

    private Integer state;

    private String scrapReason;

    private String startCreatedTime;

    private String endCreatedTime;
}
