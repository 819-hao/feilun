package com.seeease.flywheel.web.controller.xianyu;

import com.seeease.flywheel.web.entity.XyQtReportVO;
import com.seeease.flywheel.web.entity.XyRecycleOrder;
import com.seeease.flywheel.web.entity.XyRecycleOrderVO;
import com.seeease.flywheel.web.entity.enums.XyRecycleOrderStateEnum;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Tiro
 * @date 2023/10/24
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface XyRecycleConvert {
    XyRecycleConvert INSTANCE = Mappers.getMapper(XyRecycleConvert.class);

    XyRecycleOrderVO convertVO(XyRecycleOrder order);

    XyQtReportVO convertXyQtReportVO(XyRecycleOrder order);

    default String formatDateString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    default Date formatStringToDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
    }


    default XyRecycleOrderStateEnum formatEnum(Integer value) {
        return XyRecycleOrderStateEnum.findByValue(value);
    }

    default Integer formatEnumValue(XyRecycleOrderStateEnum stateEnum) {
        return stateEnum.getValue();
    }
}
