package com.seeease.flywheel.serve.allocate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.allocate.request.AllocateCancelRequest;
import com.seeease.flywheel.allocate.result.AllocateCancelResult;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateLine;
import com.seeease.flywheel.serve.allocate.enums.AllocateLineStateEnum;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_allocate_line(调拨单行)】的数据库操作Service
 * @createDate 2023-03-07 10:40:02
 */
public interface BillAllocateLineService extends IService<BillAllocateLine> {

    /**
     * 取消调拨单
     *
     * @param request
     * @return
     */
    AllocateCancelResult cancel(AllocateCancelRequest request);

    /**
     * 更新行状态
     *
     * @param allocateId
     * @param stockIdList
     * @param transitionEnum
     */
    void updateLineState(Integer allocateId, List<Integer> stockIdList, AllocateLineStateEnum.TransitionEnum transitionEnum);
}
