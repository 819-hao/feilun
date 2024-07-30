package com.seeease.flywheel.serve.maindata.convert;


import com.seeease.flywheel.maindata.request.StoreQuotaAddRequest;
import com.seeease.flywheel.maindata.result.StoreQuotaQueryResult;
import com.seeease.flywheel.serve.base.EnumConvert;

import com.seeease.flywheel.serve.maindata.entity.StoreQuota;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/4/1
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface StoreQuotaConverter extends EnumConvert {

    StoreQuotaConverter INSTANCE = Mappers.getMapper(StoreQuotaConverter.class);


    @Mapping(target = "smId",source = "request.shopId")
    StoreQuota to(StoreQuotaAddRequest request,
                  List<StoreQuota.Line> ctLines,
                  List<StoreQuota.Line> osLines);


    StoreQuota.Line to(StoreQuotaAddRequest.Line line);

    StoreQuotaQueryResult.Line to(StoreQuota.Line line, String brandName);

    StoreQuotaQueryResult to(StoreQuota v,
              String shopName,
              BigDecimal osQuota,
              BigDecimal ctQuota,
              List<StoreQuotaQueryResult.Line> osQuotas,
              List<StoreQuotaQueryResult.Line> ctQuotas,
              BigDecimal usedOsQuota,
              BigDecimal usedCtQuota);
}
