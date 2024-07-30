package com.seeease.flywheel.purchase;

import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.purchase.request.*;
import com.seeease.flywheel.purchase.result.*;

import java.util.List;

/**
 * @author wbh
 * @date 2023/2/1
 */
public interface IPurchaseReturnFacade {
    /**
     * 采购退货新增
     *
     * @param request
     * @return
     */
    List<PurchaseReturnCreateResult> create(PurchaseReturnCreateRequest request);

    /**
     * 取消
     *
     * @param request
     * @return
     */
    PurchaseReturnCancelResult cancel(PurchaseReturnCancelRequest request);

    /**
     * 列表
     *
     * @param request
     * @return
     */
    PageResult<PurchaseReturnListResult> list(PurchaseReturnListRequest request);

    /**
     * 详情
     *
     * @param request
     * @return
     */
    PurchaseReturnDetailsResult details(PurchaseReturnDetailsRequest request);


    /**
     * 查询采购退货导入商品
     *
     * @param request
     * @return
     */
    ImportResult<PurchaseReturnStockQueryImportResult> stockQueryImport(PurchaseReturnStockQueryImportRequest request);
}
