package com.seeease.flywheel.web.infrastructure.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.web.entity.ExpressOrder;
import com.seeease.flywheel.web.entity.enums.ExpressOrderStateEnum;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【express_order(物流单)】的数据库操作Service
 * @createDate 2023-09-19 16:00:02
 */
public interface ExpressOrderService extends IService<ExpressOrder> {

    /**
     * 批量插入
     *
     * @param expressOrderList
     * @return
     */
    int insertBatchSomeColumn(List<ExpressOrder> expressOrderList);

    /**
     * 更新并状态变更
     *
     * @param order
     * @param transitionEnum
     */
    void upAndStateTransition(ExpressOrder order, ExpressOrderStateEnum.TransitionEnum transitionEnum);
}
