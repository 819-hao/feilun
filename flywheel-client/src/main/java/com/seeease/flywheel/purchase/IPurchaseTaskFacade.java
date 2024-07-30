package com.seeease.flywheel.purchase;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.purchase.request.*;
import com.seeease.flywheel.purchase.result.*;

import java.util.List;
import java.util.Map;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/10/25 15:16
 */

public interface IPurchaseTaskFacade {

    /**
     * 采购任务发起
     *
     * @param request
     * @return
     */
    PurchaseTaskCreateResult create(PurchaseTaskCreateRequest request);

    /**
     * 采购任务列表
     *
     * @param request
     * @return
     */
    PageResult<PurchaseTaskPageResult> list(PurchaseTaskPageRequest request);

    /**
     * 采购任务详情
     *
     * @param request
     * @return
     */
    PurchaseTaskDetailsResult details(PurchaseTaskDetailsRequest request);

    /**
     * 采购任务审核
     *
     * @param request
     * @return
     */
    PurchaseTaskCheckResult check(PurchaseTaskCheckRequest request);

    /**
     * 编辑
     *
     * @param request
     * @return
     */
    PurchaseTaskEditResult edit(PurchaseTaskEditRequest request);

    /**
     * 分组统计
     *
     * @return
     */
    Map<Integer, Long> groupBy();

    /**
     * 导出
     *
     * @param request
     * @return
     */
    List<PurchaseTaskExportResult> export(PurchaseTaskPageRequest request);

    PurchaseTaskCancelResult cancel(PurchaseTaskCancelRequest request);
}
