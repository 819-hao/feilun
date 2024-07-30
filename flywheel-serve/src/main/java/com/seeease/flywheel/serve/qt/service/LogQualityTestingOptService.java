package com.seeease.flywheel.serve.qt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.qt.request.QualityTestingLogRequest;
import com.seeease.flywheel.qt.result.QualityTestingLogResult;
import com.seeease.flywheel.serve.qt.entity.LogQualityTestingOpt;

/**
 * @author dmmasxnmf
 * @description 针对表【log_quality_testing_opt(质检记录)】的数据库操作Service
 * @createDate 2023-02-06 11:04:34
 */
public interface LogQualityTestingOptService extends IService<LogQualityTestingOpt> {

    /**
     * 日志记录
     * @param request
     * @return
     */
    Page<QualityTestingLogResult> page(QualityTestingLogRequest request);

}
