package com.seeease.flywheel.serve.pricing.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.pricing.request.PricingLogListRequest;
import com.seeease.flywheel.pricing.result.PricingLogListResult;
import com.seeease.flywheel.serve.pricing.entity.LogPricingOpt;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author dmmasxnmf
* @description 针对表【log_pricing_opt(订价记录)】的数据库操作Mapper
* @createDate 2023-03-21 18:55:55
* @Entity com.seeease.flywheel.LogPricingOpt
*/
public interface LogPricingOptMapper extends SeeeaseMapper<LogPricingOpt> {

    /**
     * 列表
     * @param page
     * @param request
     * @return
     */
    Page<PricingLogListResult> list(Page page, @Param("request") PricingLogListRequest request);
}




