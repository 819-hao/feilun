package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsListRequest extends PageRequest {


    /**
     * 品牌
     */
    private String brandName;

    /**
     * 系列
     */
    private String seriesName;
    private Integer seriesType;

    /**
     * 型号
     */
    private String model;

    /**
     * 型号
     */
    private List<String> modelList;

    /**
     * 品牌id
     */
    private List<Integer> brandIdList;

    /**
     * 是否需要库存数量
     */
    private boolean needStockNumber;

    /**
     * 经营权
     */
    private Integer rightOfManagement;

    /**
     * 是否需要所在库存数量
     */
    private boolean needStockNumberByLocation;

    /**
     * 所在位置
     */
    private Integer locationId;
}
