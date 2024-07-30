package com.seeease.flywheel.serve.storework.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.storework.request.StoreWorkLogRequest;
import com.seeease.flywheel.storework.result.StoreWorkLogResult;
import com.seeease.flywheel.serve.storework.entity.LogStoreWorkOpt;
import com.seeease.flywheel.serve.storework.service.LogStoreWorkOptService;
import com.seeease.flywheel.serve.storework.mapper.LogStoreWorkOptMapper;
import org.springframework.stereotype.Service;

/**
 * @author Tiro
 * @description 针对表【log_store_work_opt(仓库作业操作记录)】的数据库操作Service实现
 * @createDate 2023-02-03 11:46:46
 */
@Service
public class LogStoreWorkOptServiceImpl extends ServiceImpl<LogStoreWorkOptMapper, LogStoreWorkOpt>
        implements LogStoreWorkOptService {

    @Override
    public Page<StoreWorkLogResult> page(StoreWorkLogRequest request) {
        return this.baseMapper.getPage(new Page(request.getPage(), request.getLimit()), request);
    }
}




