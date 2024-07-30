package com.seeease.flywheel.serve.pricing.convert;

import com.seeease.flywheel.pricing.request.ApplyPricingCreateRequest;
import com.seeease.flywheel.pricing.request.ApplyPricingEditRequest;
import com.seeease.flywheel.pricing.result.ApplyPricingCreateResult;
import com.seeease.flywheel.pricing.result.ApplyPricingEditResult;
import com.seeease.flywheel.pricing.result.ApplyPricingListResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.pricing.entity.BillApplyPricing;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author Tiro
 * @date 2024/2/23
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface ApplyPricingConvert extends EnumConvert {
    ApplyPricingConvert INSTANCE = Mappers.getMapper(ApplyPricingConvert.class);


    ApplyPricingListResult convertListResult(BillApplyPricing billApplyPricing);

    BillApplyPricing convert(ApplyPricingCreateRequest request);

    BillApplyPricing convert(ApplyPricingEditRequest request);

    ApplyPricingCreateResult convertCreateResult(BillApplyPricing billApplyPricing);

    ApplyPricingEditResult convertEditResult(BillApplyPricing billApplyPricing);

}
