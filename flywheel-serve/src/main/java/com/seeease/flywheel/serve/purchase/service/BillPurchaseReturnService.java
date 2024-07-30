package com.seeease.flywheel.serve.purchase.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.purchase.request.PurchaseReturnCancelRequest;
import com.seeease.flywheel.purchase.request.PurchaseReturnCreateRequest;
import com.seeease.flywheel.purchase.request.PurchaseReturnListRequest;
import com.seeease.flywheel.purchase.result.PurchaseReturnCancelResult;
import com.seeease.flywheel.purchase.result.PurchaseReturnCreateResult;
import com.seeease.flywheel.purchase.result.PurchaseReturnListResult;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturn;

import java.util.List;

/**
 * @author wbh
 * @date 2023/2/1
 */
public interface BillPurchaseReturnService extends IService<BillPurchaseReturn> {
    /**
     * 采购退货生成
     *
     * @param request
     * @return
     */
    List<PurchaseReturnCreateResult> create(PurchaseReturnCreateRequest request);

    /**
     * 采购退货取消
     *
     * @param request
     * @return
     */
    PurchaseReturnCancelResult cancel(PurchaseReturnCancelRequest request);

    Page<PurchaseReturnListResult> page(PurchaseReturnListRequest request);
}
