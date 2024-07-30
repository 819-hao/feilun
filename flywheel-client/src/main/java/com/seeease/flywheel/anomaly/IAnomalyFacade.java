package com.seeease.flywheel.anomaly;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.anomaly.request.AnomalyListRequest;
import com.seeease.flywheel.anomaly.request.AnomalyStockCreateRequest;
import com.seeease.flywheel.anomaly.result.AnomalyListResult;
import com.seeease.flywheel.anomaly.result.AnomalyStockCreateResult;

/**
 * 异常
 *
 * @author Tiro
 * @date 2023/3/6
 */
public interface IAnomalyFacade {
    /**
     * 创建
     * @param request
     * @return
     */
    AnomalyStockCreateResult create(AnomalyStockCreateRequest request);

    /**
     * 列表
     * @param request
     * @return
     */
    PageResult<AnomalyListResult> list(AnomalyListRequest request);
}
