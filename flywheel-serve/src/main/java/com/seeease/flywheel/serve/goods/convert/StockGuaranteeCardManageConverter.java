package com.seeease.flywheel.serve.goods.convert;

import com.seeease.flywheel.goods.entity.StockGuaranteeCardManageInfo;
import com.seeease.flywheel.goods.request.StockGuaranteeCardManageUpdateRequest;
import com.seeease.flywheel.pricing.result.StockGuaranteeCardManageImportResult;
import com.seeease.flywheel.serve.goods.entity.StockGuaranteeCardManage;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author Tiro
 * @date 2023/11/20
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface StockGuaranteeCardManageConverter {

    StockGuaranteeCardManageConverter INSTANCE = Mappers.getMapper(StockGuaranteeCardManageConverter.class);

    StockGuaranteeCardManageInfo convertInfo(StockGuaranteeCardManage manage);

    StockGuaranteeCardManage convert(StockGuaranteeCardManageUpdateRequest request);

    StockGuaranteeCardManageImportResult convertResult(StockGuaranteeCardManage manage);
}
