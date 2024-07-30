package com.seeease.flywheel.serve.tiktok.convert;

import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.tiktok.entity.TiktokLiveStream;
import com.seeease.flywheel.tiktok.request.TiktokLIveStreamSubmitRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @Auther Gilbert
 * @Date 2023/1/17 17:37
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface TiktokLiveStreamConvert extends EnumConvert {
    TiktokLiveStreamConvert INSTANCE = Mappers.getMapper(TiktokLiveStreamConvert.class);


    TiktokLiveStream toDO(TiktokLIveStreamSubmitRequest request);

}
