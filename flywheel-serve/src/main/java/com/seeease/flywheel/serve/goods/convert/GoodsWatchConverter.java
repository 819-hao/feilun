package com.seeease.flywheel.serve.goods.convert;

import com.seeease.flywheel.goods.request.GoodsWatchUpdateImportRequest;
import com.seeease.flywheel.goods.result.GoodsWatchInfo;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author Tiro
 * @date 2023/8/8
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface GoodsWatchConverter extends EnumConvert {
    GoodsWatchConverter INSTANCE = Mappers.getMapper(GoodsWatchConverter.class);

    GoodsWatchInfo convertGoodsWatch(GoodsWatch goodsWatch);

    @Mappings(value = {
            @Mapping(target = "modelCode", ignore = true),
    })
    GoodsWatch convert(GoodsWatchUpdateImportRequest.ImportDto dto);
}
