package com.seeease.flywheel.purchase;

import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.purchase.request.*;
import com.seeease.flywheel.purchase.result.*;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/7
 */
public interface IPurchaseFacade {


    /**
     * 采购列表
     *
     * @param request
     * @return
     */
    PageResult<PurchaseListResult> list(PurchaseListRequest request);


    /**
     * 采购详情
     *
     * @param request
     * @return
     */
    PurchaseDetailsResult details(PurchaseDetailsRequest request);


    List<PurchaseExportResult> export(PurchaseExportRequest request);
    /**
     * 采购新增
     *
     * @param request
     * @return
     */
    PurchaseCreateListResult create(PurchaseCreateRequest request);


    /**
     * 采购编辑
     *
     * @param request
     * @return
     */
    PurchaseEditResult edit(PurchaseEditRequest request);

    /**
     * 采购取消
     *
     * @param request
     * @return
     */
    PurchaseCancelResult cancel(PurchaseCancelRequest request);

    /**
     * 上传快递单号
     *
     * @param request
     * @return
     */
    PurchaseExpressNumberUploadListResult uploadExpressNumber(PurchaseExpressNumberUploadRequest request);

    /**
     * 门店收货
     *
     * @param request
     * @return
     */
    PurchaseExpressNumberUploadListResult shopReceiving(PurchaseExpressNumberUploadRequest request);

    /**
     * 归还客户
     *
     * @param request
     * @return
     */
    PurchaseExpressNumberUploadListResult confirmReturn(PurchaseExpressNumberUploadRequest request);

    /**
     * 维修确认
     *
     * @param request
     * @return
     */
    PurchaseAcceptRepairResult acceptRepair(PurchaseAcceptRepairRequest request);

    /**
     * 申请结算
     *
     * @param request
     */
    void applySettlement(PurchaseApplySettlementRequest request);

    /**
     * 采购关联销售单
     *
     * @param request
     * @return
     */
    PurchaseForSaleResult purchaseForSale(PurchaseForSaleRequest request);

    /**
     * 转回收
     *
     * @param request
     */
    void changeRecycle(PurchaseChangeRecycleRequest request);

    /**
     * 延长时间
     *
     * @param request
     */
    void extendTime(PurchaseExtendTimeRequest request);


    /**
     * 查询采购导入商品
     *
     * @param request
     * @return
     */
    ImportResult<PurchaseStockQueryImportResult> stockQueryImport(PurchaseStockQueryImportRequest request);

    /**
     * 批量结算
     * @param request
     */
    void batchSettle(PurchaseBatchSettleRequest request);

    /**
     * 补差价
     * @param request
     */
    void marginCover(PurchaseMarginCoverRequest request);
}
