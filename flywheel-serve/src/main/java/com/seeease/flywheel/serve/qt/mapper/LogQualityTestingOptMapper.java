package com.seeease.flywheel.serve.qt.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.qt.request.QualityTestingLogRequest;
import com.seeease.flywheel.qt.result.QualityTestingLogResult;
import com.seeease.flywheel.serve.qt.entity.LogQualityTestingOpt;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author dmmasxnmf
 * @description 针对表【log_quality_testing_opt(质检记录)】的数据库操作Mapper
 * @createDate 2023-02-06 11:04:34
 * @Entity com.seeease.flywheel.LogQualityTestingOpt
 */
public interface LogQualityTestingOptMapper extends SeeeaseMapper<LogQualityTestingOpt> {

    /**
     * 分页查询
     *
     * @param page
     * @param request
     * @return
     */
    Page<QualityTestingLogResult> getPage(Page page, @Param("request") QualityTestingLogRequest request);
}




