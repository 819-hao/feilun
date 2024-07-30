package com.seeease.flywheel.serve.allocate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateTask;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_allocate_task(品牌调拨任务)】的数据库操作Service
 * @createDate 2023-08-28 20:48:48
 */
public interface BillAllocateTaskService extends IService<BillAllocateTask> {

    /**
     * 批量新增
     *
     * @param taskList
     * @return
     */
    int insertBatchSomeColumn(List<BillAllocateTask> taskList);
}
