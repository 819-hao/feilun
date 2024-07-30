package com.seeease.flywheel.serve.goods.convert;

import com.seeease.flywheel.goods.request.SeriesCreateRequest;
import com.seeease.flywheel.goods.request.SeriesUpdateRequest;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.goods.entity.Series;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author Tiro
 * @date 2023/8/8
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface SeriesConverter extends EnumConvert {
    SeriesConverter INSTANCE = Mappers.getMapper(SeriesConverter.class);
    @Mappings(value = {
            @Mapping(target = "seriesType", source = "type"),
    })
    Series convertUpdateRequest(SeriesUpdateRequest request);
    @Mappings(value = {
            @Mapping(target = "seriesType", source = "type"),
    })
    Series convertCreateRequest(SeriesCreateRequest request);
}
