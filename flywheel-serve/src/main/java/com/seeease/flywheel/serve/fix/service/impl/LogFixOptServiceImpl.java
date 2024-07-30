package com.seeease.flywheel.serve.fix.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.fix.request.FixLogRequest;
import com.seeease.flywheel.fix.result.FixLogResult;
import com.seeease.flywheel.serve.fix.entity.LogFixOpt;
import com.seeease.flywheel.serve.fix.mapper.LogFixOptMapper;
import com.seeease.flywheel.serve.fix.service.LogFixOptService;
import org.springframework.stereotype.Service;

/**
* @author dmmasxnmf
* @description 针对表【log_fix_opt(维修单)】的数据库操作Service实现
* @createDate 2023-02-06 14:31:59
*/
@Service
public class LogFixOptServiceImpl extends ServiceImpl<LogFixOptMapper, LogFixOpt>
    implements LogFixOptService {

    @Override
    public Page<FixLogResult> page(FixLogRequest request) {
        return this.baseMapper.getPage(new Page(request.getPage(), request.getLimit()), request);
    }
}




