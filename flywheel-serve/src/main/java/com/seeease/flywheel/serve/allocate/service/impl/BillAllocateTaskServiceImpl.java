package com.seeease.flywheel.serve.allocate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateTask;
import com.seeease.flywheel.serve.allocate.mapper.BillAllocateTaskMapper;
import com.seeease.flywheel.serve.allocate.service.BillAllocateTaskService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_allocate_task(品牌调拨任务)】的数据库操作Service实现
 * @createDate 2023-08-28 20:48:48
 */
@Service
public class BillAllocateTaskServiceImpl extends ServiceImpl<BillAllocateTaskMapper, BillAllocateTask>
        implements BillAllocateTaskService {

    @Override
    public int insertBatchSomeColumn(List<BillAllocateTask> taskList) {
        return baseMapper.insertBatchSomeColumn(taskList);
    }

}




