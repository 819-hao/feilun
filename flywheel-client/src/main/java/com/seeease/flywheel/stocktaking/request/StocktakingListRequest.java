package com.seeease.flywheel.stocktaking.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/6/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakingListRequest extends PageRequest implements Serializable {

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 盘点仓库id
     */
    private Integer storeId;

    /**
     * 盘点状态:1-完成
     */
    private Integer stocktakingState;

    /**
     * 盘点单号
     */
    private String serialNo;

    /**
     * 创建人
     */
    private String createdBy;

}
