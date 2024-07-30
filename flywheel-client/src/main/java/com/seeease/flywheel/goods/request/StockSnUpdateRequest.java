package com.seeease.flywheel.goods.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockSnUpdateRequest implements Serializable {

    private Integer stockId;

    /**
     * 0 无 1 空白 1 有
     */
    private Integer isCard;

    private String warrantyDate;

    /**
     * 采购附件详情
     */
    private Map<String, List<Integer>> attachmentMap;

    private String stockSn;

    private Integer workSource;

    private Integer goodsId;
    private String model;
}
