package com.seeease.flywheel.serve.anomaly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.anomaly.request.AnomalyListRequest;
import com.seeease.flywheel.anomaly.result.AnomalyListResult;
import com.seeease.flywheel.serve.anomaly.entity.BillAnomaly;
import org.apache.ibatis.annotations.Param;

/**
 * @author dmmasxnmf
 * @description 针对表【bill_anomaly(异常单)】的数据库操作Mapper
 * @createDate 2023-04-12 14:35:07
 * @Entity com.seeease.flywheel.BillAnomaly
 */
public interface BillAnomalyMapper extends BaseMapper<BillAnomaly> {

    /**
     * 分页查询
     * @param page
     * @param request
     * @return
     */
    Page<AnomalyListResult> list(Page page, @Param("request") AnomalyListRequest request);

}




