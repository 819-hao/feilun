package com.seeease.flywheel.serve.stocktaking.convert;

import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.stocktaking.entity.BillStocktaking;
import com.seeease.flywheel.serve.stocktaking.entity.BillStocktakingLine;
import com.seeease.flywheel.stocktaking.request.StocktakingSubmitRequest;
import com.seeease.flywheel.stocktaking.result.StocktakingDetailsResult;
import com.seeease.flywheel.stocktaking.result.StocktakingListResult;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;


/**
 *
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface StocktakingConverter extends EnumConvert {

    StocktakingConverter INSTANCE = Mappers.getMapper(StocktakingConverter.class);

    BillStocktaking convert(StocktakingSubmitRequest request);

    StocktakingListResult convertStocktakingListResult(BillStocktaking billStocktaking);

}
