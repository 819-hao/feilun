package com.seeease.flywheel.serve.helper.convert;


import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.helper.request.BreakPriceAuditSubmitRequest;
import com.seeease.flywheel.helper.result.BreakPriceAuditHistoryResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.helper.entity.BreakPriceAudit;
import com.seeease.flywheel.serve.helper.entity.BreakPriceAuditHistory;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
imports = {JSONObject.class,Integer.class})
public interface BreakPriceAuditHistoryConvert extends EnumConvert {

    BreakPriceAuditHistoryConvert INSTANCE = Mappers.getMapper(BreakPriceAuditHistoryConvert.class);


    BreakPriceAuditHistoryResult to (BreakPriceAuditHistory history);

    List<BreakPriceAuditHistoryResult> toList (List<BreakPriceAuditHistory> history);
}
