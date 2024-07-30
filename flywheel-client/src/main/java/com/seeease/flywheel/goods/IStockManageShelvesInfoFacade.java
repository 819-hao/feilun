package com.seeease.flywheel.goods;

import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.request.StockManageShelvesInfoImportRequest;
import com.seeease.flywheel.goods.request.StockManageShelvesInfoListRequest;
import com.seeease.flywheel.goods.result.StockManageShelvesInfoImportResult;
import com.seeease.flywheel.goods.result.StockManageShelvesInfoListResult;

/**
 * @author Tiro
 * @date 2023/9/4
 */
public interface IStockManageShelvesInfoFacade {
    /**
     * 货位流转码列表
     *
     * @param request
     * @return
     */
    PageResult<StockManageShelvesInfoListResult> list(StockManageShelvesInfoListRequest request);

    /**
     * 货位流转码导入
     *
     * @param request
     * @return
     */
    ImportResult<StockManageShelvesInfoImportResult> infoImport(StockManageShelvesInfoImportRequest request);
}
