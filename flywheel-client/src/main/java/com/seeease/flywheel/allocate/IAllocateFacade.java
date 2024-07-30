package com.seeease.flywheel.allocate;

import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.allocate.request.*;
import com.seeease.flywheel.allocate.result.*;
import com.seeease.flywheel.goods.entity.StockBaseInfo;

import java.util.List;

/**
 * 调拨
 *
 * @author Tiro
 * @date 2023/3/6
 */
public interface IAllocateFacade {

    /**
     * 调拨创建
     *
     * @param request
     * @return
     */
    AllocateCreateResult create(AllocateCreateRequest request);

    /**
     * 调拨列表
     *
     * @param request
     * @return
     */
    PageResult<AllocateListResult> list(AllocateListRequest request);

    /**
     * 调拨详情
     *
     * @param request
     * @return
     */
    AllocateDetailsResult details(AllocateDetailsRequest request);

    /**
     * 调拨取消
     *
     * @param request
     * @return
     */
    AllocateCancelResult cancel(AllocateCancelRequest request);

    /**
     * 调拨商品导入查询
     *
     * @param request
     * @return
     */
    ImportResult<AllocateStockQueryImportResult> stockQueryImport(AllocateStockQueryImportRequest request);

    PageResult<AllocateExportListResult> export(AllocateExportListRequest request);

    /**
     * 调拨商品导入
     */
    ImportResult<AllocateStockBaseInfoImportResult> allocateStockImport(AllocateStockImportRequest request, List<StockBaseInfo> stockBaseInfos);

    ImportResult<BorrowStockBaseInfoImportResult> allocateStockImport2(BorrowStockImportRequest request, List<StockBaseInfo> stockBaseInfos);

    /**
     * 超时查询
     *
     * @param request
     * @return
     */
    List<AllocateToTimeoutResult> toTimeout(AllocateToTimeoutRequest request);
}
