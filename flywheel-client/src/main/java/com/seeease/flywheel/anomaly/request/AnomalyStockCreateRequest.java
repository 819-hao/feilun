package com.seeease.flywheel.anomaly.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 异常商品转出
 * @Date create in 2023/4/12 11:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyStockCreateRequest implements Serializable {

    /**
     * 商品id列表
     */
    private List<Integer> stockIdList;

    /**
     * 0 走维修
     * 1 走商品状态
     */
    private Integer direct;

}
