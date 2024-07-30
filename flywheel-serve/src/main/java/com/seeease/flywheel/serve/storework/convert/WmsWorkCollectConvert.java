package com.seeease.flywheel.serve.storework.convert;

import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPreExt;
import com.seeease.flywheel.serve.storework.entity.WmsWorkCapacityDTO;
import com.seeease.flywheel.storework.result.WmsWaitWorkCollectResult;
import com.seeease.flywheel.storework.result.WmsWorkExpressResult;
import com.seeease.flywheel.storework.result.WmsWorkListResult;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author Tiro
 * @date 2023/8/31
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface WmsWorkCollectConvert extends EnumConvert {
    WmsWorkCollectConvert INSTANCE = Mappers.getMapper(WmsWorkCollectConvert.class);

    @Mappings(value = {
            @Mapping(source = "id", target = "workId")
    })
    WmsWorkListResult convertWmsWaitWorkListResult(BillStoreWorkPreExt billStoreWorkPre);


    WmsWorkExpressResult.GoodsInfo convertWmsWorkExpressGoodsInfo(BillStoreWorkPreExt billStoreWorkPre);

    WmsWaitWorkCollectResult.WmsWorkCapacityResult convert(WmsWorkCapacityDTO dto);
}
