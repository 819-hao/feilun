package com.seeease.flywheel.serve.goods.convert;

import com.seeease.flywheel.goods.request.StockManageInfoImportRequest;
import com.seeease.flywheel.goods.result.StockManageInfoImportResult;
import com.seeease.flywheel.goods.result.StockManageInfoListResult;
import com.seeease.flywheel.goods.result.StockManageShelvesInfoImportResult;
import com.seeease.flywheel.goods.result.StockManageShelvesInfoListResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.goods.entity.StockManageInfo;
import com.seeease.flywheel.serve.goods.entity.StockManageShelvesInfo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author Tiro
 * @date 2023/8/8
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface StockManageInfoConverter extends EnumConvert {
    StockManageInfoConverter INSTANCE = Mappers.getMapper(StockManageInfoConverter.class);

    StockManageInfo convert(StockManageInfoImportRequest.ImportDto dto);

    StockManageInfoImportResult convertImportResult(StockManageInfo info);

    StockManageInfoListResult convertListResult(StockManageInfo info);

    StockManageShelvesInfoListResult convertStockManageShelvesInfoListResult(StockManageShelvesInfo info);

    StockManageShelvesInfoImportResult convertStockManageInfoImportResult(StockManageShelvesInfo info);
}
