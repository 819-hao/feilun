package com.seeease.flywheel.pricing;

import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.pricing.request.*;
import com.seeease.flywheel.pricing.result.*;
import com.seeease.springframework.SingleResponse;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:57
 */

public interface IPricingFacade {

    /**
     * 创建
     *
     * @param request
     * @return
     */
    PricingCreateResult create(PricingCreateRequest request);


    /**
     * 定价完成
     *
     * @param request
     * @return
     */
    PricingFinishResult finish(PricingFinishRequest request);

    /**
     * 批量定价
     *
     * @param request
     * @return
     */
    PricingFinishBatchResult finishBatch(PricingFinishBatchRequest request);

    /**
     * 审核完成
     *
     * @param request
     * @return
     */
    PricingCompletedResult completed(PricingCompletedRequest request);

    /**
     * 批量审核数据
     *
     * @param request
     * @return
     */
    List<PricingCompletedResult> batchPass(List<Integer> request);

    /**
     * 详情
     *
     * @param request
     * @return
     */
    PricingDetailsResult details(PricingDetailsRequest request);

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    PageResult<PricingListResult> list(PricingListRequest request);

    /**
     * 日志
     *
     * @param request
     * @return
     */
    PageResult<PricingLogListResult> logList(PricingLogListRequest request);

    /**
     * 取消
     *
     * @param request
     * @return
     */
    PricingCancelResult cancel(PricingCancelRequest request);

    /**
     * 定价
     *
     * @param request
     * @return
     */
    ImportResult<PricingStockQueryImportResult> stockQueryImport(PricingStockQueryImportRequest request,Integer state);

    /**
     * 批量修改商品销售等级/分级
     *
     * @param request
     * @return
     */
    ImportResult<SalesPriorityModifyImportResult> salesPriorityModifyImport(SalesPriorityModifyImportRequest request);


    void wuyuPricing(List<WuyuPricingRequest.Item> request);


    PageResult<WuyuPricingPageResult> page(WuyuPricingPageRequest request);

    void wuyuPricingImport(WuyuPricingImportRequest request);

    ImportResult<PricingStockQueryImportResult> update(PricingStockQueryImportRequest request);
}
