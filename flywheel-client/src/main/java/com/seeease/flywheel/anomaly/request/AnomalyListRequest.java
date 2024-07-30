package com.seeease.flywheel.anomaly.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyListRequest extends PageRequest implements Serializable {


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
     * 状态
     */
    private Integer anomalyState;

    /**
     * 品牌idlist
     */
    private List<Integer> brandIdList;

    /**
     * 型号列表
     */
    private List<Integer> goodsIdList;
}
