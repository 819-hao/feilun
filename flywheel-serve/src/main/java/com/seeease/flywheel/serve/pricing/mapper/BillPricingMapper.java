package com.seeease.flywheel.serve.pricing.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.pricing.request.PricingListRequest;
import com.seeease.flywheel.pricing.result.PricingListResult;
import com.seeease.flywheel.serve.pricing.entity.BillPricing;
import com.seeease.flywheel.serve.pricing.entity.SalesPriorityModifyDTO;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @author dmmasxnmf
 * @description 针对表【bill_pricing(订价记录)】的数据库操作Mapper
 * @createDate 2023-03-21 10:44:40
 * @Entity com.seeease.flywheel.BillPricing
 */
public interface BillPricingMapper extends SeeeaseMapper<BillPricing> {

    /**
     * 列表查询
     *
     * @param page
     * @param request
     * @return
     */
    Page<PricingListResult> list(Page page, @Param("request") PricingListRequest request);

    /**
     * 批量更新销售优先等级/分级
     *
     * @param dto
     * @return
     */
    int batchUpdateSalesPriority(SalesPriorityModifyDTO dto);

    void updateByStockId(@Param("stockId") Integer stockId, @Param("finalPurchase") BigDecimal finalPurchase);
}




