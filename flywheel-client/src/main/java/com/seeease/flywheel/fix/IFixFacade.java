package com.seeease.flywheel.fix;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.fix.request.*;
import com.seeease.flywheel.fix.result.*;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/1/17 17:27
 */

public interface IFixFacade {

    /**
     * 接收
     *
     * @param request
     * @return
     */
    List<FixReceiveListResult> receive(List<FixReceiveRequest.FixReceiveListRequest> request);

    /**
     * 新接修
     *
     * @param request
     * @return
     */
    FixRepairResult repair(FixRepairRequest request);


    /**
     * 维修完成
     *
     * @param request
     * @return
     */
    FixFinishResult finish(FixFinishRequest request);

    /**
     * 列表
     *
     * @param request
     * @return
     */
    PageResult<FixListResult> list(FixListRequest request);

    /**
     * 详情
     *
     * @param request
     * @return
     */
    FixDetailsResult details(FixDetailsRequest request);

    /**
     * 编辑
     *
     * @param request
     * @return
     */
    FixEditResult edit(FixEditRequest request);

    /**
     * 延期
     *
     * @param request
     * @return
     */
    FixDelayResult delay(FixDelayRequest request);

    /**
     * 编辑维修加急
     *
     * @param request
     */
    void edit(FixSpecialExpeditingRequest request);

    /**
     * 维修日志
     *
     * @param request
     * @return
     */
    PageResult<FixLogResult> logList(FixLogRequest request);

    /**
     * 所有进行中已超时
     *
     * @return
     */
    List<FixListResult> allList();

    /**
     * 分配
     *
     * @param request
     * @return
     */
    FixAllotResult allot(FixAllotRequest request);

    /**
     * 送外
     *
     * @param request
     * @return
     */
    FixForeignResult foreign(FixForeignRequest request);

    /**
     * 创建
     *
     * @param request
     * @return
     */
    FixCreateResult create(FixCreateRequest request);

    /**
     * 甘特图
     *
     * @return
     */
    List<FixGanttChartResult> ganttChart();

    /**
     * 维修结果集
     *
     * @param request
     * @return
     */
    FixEditResultResult editResult(FixEditResultRequest request);



    /**
     * 维修师列表
     *
     * @return
     */
    List<MaintenanceMasterListResult> maintenanceMasterList();

    /**
     * 维修师
     * @param request
     * @return
     */
    FixMaintenanceResult editMaintenance(FixMaintenanceRequest request);
}
