package com.seeease.flywheel.serve.anomaly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.anomaly.request.AnomalyListRequest;
import com.seeease.flywheel.anomaly.result.AnomalyListResult;
import com.seeease.flywheel.serve.anomaly.entity.BillAnomaly;

/**
 * @author dmmasxnmf
 * @description 针对表【bill_anomaly(异常单)】的数据库操作Service
 * @createDate 2023-04-12 14:35:07
 */
public interface BillAnomalyService extends IService<BillAnomaly> {

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    PageResult<AnomalyListResult> list(AnomalyListRequest request);
}
