package com.seeease.flywheel.serve.fix.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.fix.request.FixLogRequest;
import com.seeease.flywheel.fix.result.FixLogResult;
import com.seeease.flywheel.serve.fix.entity.LogFixOpt;
import org.apache.ibatis.annotations.Param;

/**
 * @author dmmasxnmf
 * @description 针对表【log_fix_opt(维修单)】的数据库操作Mapper
 * @createDate 2023-02-06 14:31:59
 * @Entity com.seeease.flywheel.LogFixOpt
 */
public interface LogFixOptMapper extends BaseMapper<LogFixOpt> {

    /**
     * 分页
     * @param page
     * @param request
     * @return
     */
    Page<FixLogResult> getPage(Page page, @Param("request") FixLogRequest request);

}




