package com.seeease.flywheel.serve.qt.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.qt.request.QualityTestingListRequest;
import com.seeease.flywheel.qt.request.QualityTestingWaitDeliverListRequest;
import com.seeease.flywheel.qt.result.QualityTestingListResult;
import com.seeease.flywheel.qt.result.QualityTestingWaitDeliverListResult;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author dmmasxnmf
 * @description 针对表【bill_quality_testing(质检记录)】的数据库操作Mapper
 * @createDate 2023-01-18 09:55:53
 * @Entity com.seeease.flywheel.BillQualityTesting
 */
public interface BillQualityTestingMapper extends SeeeaseMapper<BillQualityTesting> {

    /**
     * 分页数据
     * @param page
     * @param request
     * @return
     */
    Page<QualityTestingListResult> getPage(Page page, @Param("request") QualityTestingListRequest request);

    /**
     * 待质检列表
     * @param page
     * @param request
     * @return
     */
    Page<QualityTestingWaitDeliverListResult> getPageWaitDeliver(Page page, @Param("request") QualityTestingWaitDeliverListRequest request);

}




