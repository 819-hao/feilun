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
public class StockExceptionListRequest extends PageRequest {


    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 商品位置
     */
    private Integer locationId;

    /**
     * 经营权
     */
    private Integer rightOfManagement;

    /**
     * 仓库
     */
    private Integer storeId;

    /**
     * 品牌idlist
     */
    private List<Integer> brandIdList;

    /**
     * 型号列表
     */
    private List<Integer> goodsIdList;

    private Integer stockSrc;

    private Integer stockStatus;

}
