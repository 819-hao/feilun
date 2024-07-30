package com.seeease.flywheel.qt;


import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.qt.request.*;
import com.seeease.flywheel.qt.result.QualityTestingDecisionListResult;
import com.seeease.flywheel.qt.result.QualityTestingListResult;
import com.seeease.flywheel.qt.result.QualityTestingLogResult;
import com.seeease.flywheel.qt.result.QualityTestingWaitDeliverListResult;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description 质检rpc
 * @Date create in 2023/1/17 14:00
 */

public interface IQualityTestingFacade {

    /**
     * 列表
     *
     * @param request
     * @return
     */
    PageResult<QualityTestingListResult> list(QualityTestingListRequest request);

    /**
     * 待质检转交
     *
     * @param request
     * @return
     */
    PageResult<QualityTestingWaitDeliverListResult> qtWaitDeliver(QualityTestingWaitDeliverListRequest request);

    /**
     * 更改质检数据
     *
     * @param request
     */
    void edit(QualityTestingEditRequest request);

    /**
     * 质检判定
     *
     * @param request
     * @return
     */
    QualityTestingDecisionListResult decision(QualityTestingDecisionRequest request);

    /**
     * 质检日志
     *
     * @param request
     * @return
     */
    PageResult<QualityTestingLogResult> logList(QualityTestingLogRequest request);

    /**
     * 批量通过
     *
     * @param list
     * @return
     */
    List<QualityTestingDecisionListResult> batchPass(List<Integer> list);

    /**
     * 质检转交
     * @param  qualityTestingId
     */
    QualityTestingDecisionListResult receive(Integer qualityTestingId);

}
