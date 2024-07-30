package com.seeease.flywheel.serve.purchase.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.purchase.request.PurchasePlanCreateRequest;
import com.seeease.flywheel.purchase.request.PurchasePlanListRequest;
import com.seeease.flywheel.purchase.result.PurchaseCreateListResult;
import com.seeease.flywheel.purchase.result.PurchasePlanCreateResult;
import com.seeease.flywheel.purchase.result.PurchasePlanExportResult;
import com.seeease.flywheel.purchase.result.PurchasePlanListResult;
import com.seeease.flywheel.serve.purchase.entity.BillPurchasePlan;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author edy
* @description 针对表【bill_purchase_plan】的数据库操作Service
* @createDate 2023-08-08 16:58:36
*/
public interface BillPurchasePlanService extends IService<BillPurchasePlan> {

    PurchasePlanCreateResult create(PurchasePlanCreateRequest request);

    Page<PurchasePlanListResult> listByRequest(PurchasePlanListRequest request);

    Page<PurchasePlanExportResult> export(PurchasePlanListRequest request);
}
