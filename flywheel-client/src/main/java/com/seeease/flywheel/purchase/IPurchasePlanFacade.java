package com.seeease.flywheel.purchase;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.purchase.request.PurchasePlanCreateRequest;
import com.seeease.flywheel.purchase.request.PurchasePlanDetailsRequest;
import com.seeease.flywheel.purchase.request.PurchasePlanListRequest;
import com.seeease.flywheel.purchase.request.PurchasePlanUpdateRequest;
import com.seeease.flywheel.purchase.result.PurchasePlanCreateResult;
import com.seeease.flywheel.purchase.result.PurchasePlanDetailsResult;
import com.seeease.flywheel.purchase.result.PurchasePlanExportResult;
import com.seeease.flywheel.purchase.result.PurchasePlanListResult;

/**
 * @author Tiro
 * @date 2023/1/7
 */
public interface IPurchasePlanFacade {

    /**
     * 创建采购计划
     *
     * @param request
     * @return
     */
    public PurchasePlanCreateResult create(PurchasePlanCreateRequest request);

    public PageResult<PurchasePlanListResult> list(PurchasePlanListRequest request);

    public PurchasePlanDetailsResult details(PurchasePlanDetailsRequest request);

    public void update(PurchasePlanUpdateRequest request);

    PageResult<PurchasePlanExportResult> export(PurchasePlanListRequest request);
}
