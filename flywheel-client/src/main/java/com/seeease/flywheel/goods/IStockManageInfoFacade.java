package com.seeease.flywheel.goods;

import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.request.StockManageInfoImportRequest;
import com.seeease.flywheel.goods.request.StockManageInfoListRequest;
import com.seeease.flywheel.goods.result.StockManageInfoImportResult;
import com.seeease.flywheel.goods.result.StockManageInfoListResult;

/**
 * @author Tiro
 * @date 2023/8/8
 */
public interface IStockManageInfoFacade {


    /**
     * 列表
     *
     * @param request
     * @return
     */
    PageResult<StockManageInfoListResult> list(StockManageInfoListRequest request);

    /**
     * 导入
     *
     * @param request
     * @return
     */
    ImportResult<StockManageInfoImportResult> stockManageInfoImport(StockManageInfoImportRequest request);
}
