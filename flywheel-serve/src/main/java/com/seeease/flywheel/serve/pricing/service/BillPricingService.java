package com.seeease.flywheel.serve.pricing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.pricing.request.*;
import com.seeease.flywheel.pricing.result.*;
import com.seeease.flywheel.serve.pricing.entity.BillPricing;
import com.seeease.flywheel.serve.pricing.entity.SalesPriorityModifyDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【bill_pricing(订价记录)】的数据库操作Service
 * @createDate 2023-03-21 10:44:40
 */
public interface BillPricingService extends IService<BillPricing> {

    /**
     * 新建
     *
     * @param request
     * @return
     */
    PricingCreateResult create(PricingCreateRequest request);

    /**
     * 新建自动定价
     * @param request
     * @return
     */
    PricingCreateResult createAuto(PricingCreateRequest request);

    /**
     * 重新定价
     *
     * @param request
     * @return
     */
    PricingCreateResult again(PricingCreateRequest request);


    /**
     * 定价
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
     * 定价通过
     *
     * @param request
     * @return
     */
    PricingCompletedResult completed(PricingCompletedRequest request);

    /**
     * 详情
     *
     * @param request
     * @return
     */
    PricingDetailsResult details(PricingDetailsRequest request);

    /**
     * 分页数据
     *
     * @param request
     * @return
     */
    PageResult<PricingListResult> list(PricingListRequest request);


    /**
     * 定价日志
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
     * 批量定价导入
     *
     * @param request
     * @return
     */
    List<PricingStockQueryImportResult> importList(PricingStockQueryImportRequest request,Integer state);

    int batchUpdateSalesPriority(SalesPriorityModifyDTO dto);

    void updateByStockId(Integer stockId, BigDecimal finalPurchase);
}
