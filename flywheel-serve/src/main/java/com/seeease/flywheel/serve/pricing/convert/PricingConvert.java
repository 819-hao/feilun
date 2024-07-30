package com.seeease.flywheel.serve.pricing.convert;

import com.seeease.flywheel.pricing.request.PricingCancelLineRequest;
import com.seeease.flywheel.pricing.request.PricingCompletedRequest;
import com.seeease.flywheel.pricing.request.PricingCreateRequest;
import com.seeease.flywheel.pricing.request.PricingFinishRequest;
import com.seeease.flywheel.pricing.result.*;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.pricing.entity.BillPricing;
import com.seeease.flywheel.serve.pricing.entity.LogPricingOpt;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 11:10
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface PricingConvert extends EnumConvert {

    PricingConvert INSTANCE = Mappers.getMapper(PricingConvert.class);

    /**
     * 创建提交
     *
     * @param request
     * @return
     */
    @Mappings(value = {
            @Mapping(target = "CPrice", source = "tocPrice"),
            @Mapping(target = "BPrice", source = "tobPrice"),
    })
    BillPricing convert(PricingCreateRequest request);

    /**
     * 审核通过提交
     *
     * @param request
     * @return
     */
    @Mappings(value = {
            @Mapping(target = "CPrice", source = "tocPrice"),
            @Mapping(target = "BPrice", source = "tobPrice"),
    })
    BillPricing convert(PricingFinishRequest request);

    BillPricing convert(PricingCompletedRequest request);

    /**
     * 返回值
     *
     * @param request
     * @return
     */
    PricingCreateResult convertPricingCreateResult(BillPricing request);

    /**
     * 返回值
     *
     * @param request
     * @return
     */
    PricingFinishResult convertPricingFinishResult(BillPricing request);

    PricingCompletedResult convertPricingCompletedResult(BillPricing request);

    /**
     * @param request
     * @return
     */
    @Mappings(value = {
            @Mapping(target = "tobMargin", source = "BMargin"),
            @Mapping(target = "tocMargin", source = "CMargin"),
            @Mapping(target = "tobPrice", source = "BPrice"),
            @Mapping(target = "tocPrice", source = "CPrice"),
            @Mapping(target = "toaPrice", source = "APrice"),
            @Mapping(target = "tagPrice", source = "TPrice"),

    })
    PricingDetailsResult convertPricingDetailsResult(BillPricing request);

    /**
     * 分页
     *
     * @param request
     * @return
     */
    PricingListResult convertPricingListResult(BillPricing request);


    PricingLog convertPricingLog(LogPricingOpt request);

    /**
     * 转日志 不做映射
     *
     * @param request
     * @return
     */
    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdId", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdTime", ignore = true),
            @Mapping(target = "updatedId", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "updatedTime", ignore = true),
    })
    LogPricingOpt convertLogPricingOpt(BillPricing request);

    /**
     * 忽略 要做映射
     *
     * @param request
     * @return
     */
    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
    })
    LogPricingOpt convertLogPricingOptIgnore(BillPricing request);

    /**
     * 转换
     *
     * @param request
     * @return
     */
    @Mappings(value = {
            @Mapping(target = "id", source = "stockId"),
            @Mapping(target = "createdId", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdTime", ignore = true),
            @Mapping(target = "updatedId", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "updatedTime", ignore = true),
    })
    Stock convertStock(BillPricing request);


    /**
     * 创建提交
     *
     * @param request
     * @return
     */
    @Mappings(value = {
    })
    BillPricing convert(PricingCancelLineRequest request);
}
