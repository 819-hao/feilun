package com.seeease.flywheel.serve.storework.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.serve.storework.entity.LogStoreWorkOpt;
import com.seeease.flywheel.storework.request.StoreWorkLogRequest;
import com.seeease.flywheel.storework.result.StoreWorkLogResult;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Tiro
 * @description 针对表【log_store_work_opt(仓库作业操作记录)】的数据库操作Mapper
 * @createDate 2023-02-03 11:46:46
 * @Entity com.seeease.flywheel.serve.storework.entity.LogStoreWorkOpt
 */
public interface LogStoreWorkOptMapper extends SeeeaseMapper<LogStoreWorkOpt> {

    /**
     * 分页数据
     *
     * @param page
     * @param request
     * @return
     */
    Page<StoreWorkLogResult> getPage(Page page, @Param("request") StoreWorkLogRequest request);
}




