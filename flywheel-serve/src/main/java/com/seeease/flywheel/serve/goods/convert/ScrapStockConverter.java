package com.seeease.flywheel.serve.goods.convert;

import com.seeease.flywheel.goods.result.ScrapOrderDetailResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.goods.entity.BillStockScrap;
import com.seeease.flywheel.serve.goods.entity.BillStockScrapLine;
import com.seeease.flywheel.serve.goods.entity.ScrapStock;
import com.seeease.flywheel.serve.goods.entity.Stock;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author Tiro
 * @date 2023/4/1
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface ScrapStockConverter extends EnumConvert {

    ScrapStockConverter INSTANCE = Mappers.getMapper(ScrapStockConverter.class);

    @Mappings(value = {
            @Mapping(target = "stockId", source = "id"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "revision", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdTime", ignore = true),
            @Mapping(target = "updatedId", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "updatedTime", ignore = true),
            @Mapping(target = "deleted", ignore = true),
            @Mapping(target = "createdId", ignore = true)
    })
    ScrapStock convertStock(Stock stock);
    @Mappings(value = {
            @Mapping(target = "stockId", source = "id"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "revision", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdTime", ignore = true),
            @Mapping(target = "updatedId", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "updatedTime", ignore = true),
            @Mapping(target = "deleted", ignore = true),
            @Mapping(target = "createdId", ignore = true)
    })
    BillStockScrapLine convertStockToLine(Stock s);

    ScrapOrderDetailResult convertBillStockScrap(BillStockScrap billStockScrap);
}
