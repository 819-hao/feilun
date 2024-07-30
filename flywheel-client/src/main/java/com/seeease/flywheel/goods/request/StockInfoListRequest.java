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
public class StockInfoListRequest extends PageRequest {

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 商品状态
     */
    private Integer stockStatus;

    private Integer storeId;

    private String rkStartTime;

    private String rkEndTime;

    private List<Integer> brandIds;

    /**
     * 型号搜索
     */
    private String model;

    /**
     * 盒号搜索
     */
    private String boxNumber;
}
