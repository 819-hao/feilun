package com.seeease.flywheel.serve.customer.convert;

import com.seeease.flywheel.customer.request.BankCreateRequest;
import com.seeease.flywheel.customer.request.BankUpdateRequest;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.customer.entity.Bank;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author wbh
 * @date 2023/3/1
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface BankConvert extends EnumConvert {

    BankConvert INSTANCE = Mappers.getMapper(BankConvert.class);


    Bank convert(BankCreateRequest request);

    Bank convert(BankUpdateRequest request);
}
