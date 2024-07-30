package com.seeease.flywheel.serve.storework.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.storework.request.StoreWorkLogRequest;
import com.seeease.flywheel.storework.result.StoreWorkLogResult;
import com.seeease.flywheel.serve.storework.entity.LogStoreWorkOpt;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Tiro
* @description 针对表【log_store_work_opt(仓库作业操作记录)】的数据库操作Service
* @createDate 2023-02-03 11:46:46
*/
public interface LogStoreWorkOptService extends IService<LogStoreWorkOpt> {

    /**
     * 分页查询
     * @param request
     * @return
     */
    Page<StoreWorkLogResult> page(StoreWorkLogRequest request);

}
