package com.seeease.flywheel.serve.goods.convert;

import com.seeease.flywheel.goods.request.StockLifeCycleCreateRequest;
import com.seeease.flywheel.goods.result.StockLifeCycleListResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.goods.entity.BillLifeCycle;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/27 15:48
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface StockLifeCycleConverter extends EnumConvert {

    StockLifeCycleConverter INSTANCE = Mappers.getMapper(StockLifeCycleConverter.class);

    @Mappings(value = {
            @Mapping(target = "operationTime", source = "operationTime", defaultExpression = "java(System.currentTimeMillis())")
    })
    BillLifeCycle convert(StockLifeCycleCreateRequest request);

    StockLifeCycleListResult convertStockLifecycleResult(BillLifeCycle request);
}
