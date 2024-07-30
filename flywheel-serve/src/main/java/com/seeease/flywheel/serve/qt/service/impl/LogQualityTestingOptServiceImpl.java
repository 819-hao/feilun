package com.seeease.flywheel.serve.qt.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.qt.request.QualityTestingLogRequest;
import com.seeease.flywheel.qt.result.QualityTestingLogResult;
import com.seeease.flywheel.serve.qt.entity.LogQualityTestingOpt;
import com.seeease.flywheel.serve.qt.mapper.LogQualityTestingOptMapper;
import com.seeease.flywheel.serve.qt.service.LogQualityTestingOptService;
import org.springframework.stereotype.Service;

/**
* @author dmmasxnmf
* @description 针对表【log_quality_testing_opt(质检记录)】的数据库操作Service实现
* @createDate 2023-02-06 11:04:34
*/
@Service
public class LogQualityTestingOptServiceImpl extends ServiceImpl<LogQualityTestingOptMapper, LogQualityTestingOpt>
    implements LogQualityTestingOptService {

    @Override
    public Page<QualityTestingLogResult> page(QualityTestingLogRequest request) {
        return this.baseMapper.getPage(new Page(request.getPage(), request.getLimit()), request);
    }
}




