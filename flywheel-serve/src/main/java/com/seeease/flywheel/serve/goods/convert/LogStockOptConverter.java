package com.seeease.flywheel.serve.goods.convert;

import com.seeease.flywheel.goods.result.LogStockOptListResult;
import com.seeease.flywheel.serve.goods.entity.LogStockOpt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2024/3/19 14:42
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface LogStockOptConverter {

    LogStockOptConverter INSTANCE = Mappers.getMapper(LogStockOptConverter.class);

    @Mapping(target = "updatedTime", source = "updatedTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    LogStockOptListResult convertList(LogStockOpt request);
}
