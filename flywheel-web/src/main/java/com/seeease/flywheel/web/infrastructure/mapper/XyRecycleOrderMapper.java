package com.seeease.flywheel.web.infrastructure.mapper;

import com.seeease.flywheel.web.entity.XyRecycleOrder;
import com.seeease.flywheel.web.entity.XyRecycleOrderStatsVO;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【xy_recycle_quote_order(闲鱼估价订单)】的数据库操作Mapper
 * @createDate 2023-10-20 11:35:18
 * @Entity com.seeease.flywheel.web.entity.XyRecycleOrder
 */
public interface XyRecycleOrderMapper extends SeeeaseMapper<XyRecycleOrder> {

    /**
     * @return
     */
    List<XyRecycleOrderStatsVO> statsByOrderState();
}




