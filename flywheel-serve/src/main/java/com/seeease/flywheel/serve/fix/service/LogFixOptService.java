package com.seeease.flywheel.serve.fix.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.fix.request.FixLogRequest;
import com.seeease.flywheel.fix.result.FixLogResult;
import com.seeease.flywheel.serve.fix.entity.LogFixOpt;

/**
* @author dmmasxnmf
* @description 针对表【log_fix_opt(维修单)】的数据库操作Service
* @createDate 2023-02-06 14:31:59
*/
public interface LogFixOptService extends IService<LogFixOpt> {

    /**
     * 分页
     * @param request
     * @return
     */
    Page<FixLogResult> page(FixLogRequest request);
}
