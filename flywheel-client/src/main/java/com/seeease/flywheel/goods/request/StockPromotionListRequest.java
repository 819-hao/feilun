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
public class StockPromotionListRequest extends PageRequest {


    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 状态 0下架 1上架
     */
    private Integer status;

    /**
     * 型号
     */
    private String model;

    private String startTime;
    private String endTime;
}
