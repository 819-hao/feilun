package com.seeease.flywheel.stocktaking.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/6/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakingDetailsResult implements Serializable {
    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 盘点行状态:1-盘盈，2-盘亏
     */
    private Integer stocktakingLineState;
    /**
     * 表身号
     */
    private String stockSn;
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
     * 附件详情
     */
    private String attachment;


}
