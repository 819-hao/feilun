package com.seeease.flywheel.serve.rfid.convert;

import com.seeease.flywheel.rfid.result.RfidConfigResult;
import com.seeease.flywheel.serve.goods.convert.StockConverter;
import com.seeease.flywheel.serve.rfid.entity.RfidConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface RfidConfigConverter {

    RfidConfigConverter INSTANCE = Mappers.getMapper(RfidConfigConverter.class);


    RfidConfigResult convert(RfidConfig rfidConfig);
}
