package com.seeease.flywheel.goods;

import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.entity.StockGuaranteeCardManageInfo;
import com.seeease.flywheel.goods.request.StockGuaranteeCardManageEditRequest;
import com.seeease.flywheel.goods.request.StockGuaranteeCardManageFindRequest;
import com.seeease.flywheel.goods.request.StockGuaranteeCardManageListRequest;
import com.seeease.flywheel.goods.request.StockGuaranteeCardManageUpdateRequest;
import com.seeease.flywheel.pricing.request.StockGuaranteeCardManageImportRequest;
import com.seeease.flywheel.pricing.result.StockGuaranteeCardManageImportResult;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/11/20
 */
public interface IStockGuaranteeCardManageFacade {

    /**
     * 列表
     *
     * @param request
     * @return
     */
    PageResult<StockGuaranteeCardManageInfo> list(StockGuaranteeCardManageListRequest request);

    /**
     * @param request
     * @return
     */
    List<StockGuaranteeCardManageInfo> find(StockGuaranteeCardManageFindRequest request);

    /**
     * 更新
     *
     * @param request
     */
    void update(StockGuaranteeCardManageUpdateRequest request);

    /**
     * 导入
     *
     * @param request
     * @return
     */
    ImportResult<StockGuaranteeCardManageImportResult> importHandle(StockGuaranteeCardManageImportRequest request);

    void edit(StockGuaranteeCardManageEditRequest request);
}
