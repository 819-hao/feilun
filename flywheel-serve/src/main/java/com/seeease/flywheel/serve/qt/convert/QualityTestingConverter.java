package com.seeease.flywheel.serve.qt.convert;

import com.seeease.flywheel.qt.request.*;
import com.seeease.flywheel.qt.result.*;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.flywheel.serve.qt.entity.FixProjectMapper;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/1/17 14:34
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface QualityTestingConverter extends EnumConvert {

    QualityTestingConverter INSTANCE = Mappers.getMapper(QualityTestingConverter.class);

    /**
     * 入参转换
     *
     * @param request
     * @return
     */
    List<BillQualityTesting> convert(List<QualityTestingCreateRequest> request);

    /**
     * id转换
     *
     * @param dto
     * @return
     */

    @Mappings(value = {
            @Mapping(source = "qualityTestingId", target = "id"),
    })
    BillQualityTesting convert(QualityTestingReceiveRequest.QualityTestingReceiveListRequest dto);

    @Mappings(value = {
            @Mapping(source = "qualityTestingId", target = "id"),
    })
    BillQualityTesting convert(QualityTestingDecisionRequest dto);

    /**
     * 质检通知
     *
     * @param dto
     * @return
     */
    @Mappings(value = {
            @Mapping(source = "fixId", target = "fixId"),
    })
    BillQualityTesting convert(FixQualityTestingRequest dto);

    @Mappings(value = {
            @Mapping(source = "qualityTestingId", target = "id"),
    })
    BillQualityTesting convert(QualityTestingEditRequest dto);

    /**
     * 响应转换
     *
     * @param billQualityTesting
     * @return
     */
    QualityTestingCreateResult convertBillQualityTestingCreateResult(BillQualityTesting billQualityTesting);

    /**
     * 响应转化
     *
     * @param billQualityTesting
     * @return
     */
    QualityTestingReceiveResult convertBillQualityTestingReceiveResult(BillQualityTesting billQualityTesting);

    /**
     * 响应转化
     *
     * @param billQualityTesting
     * @return
     */
    QualityTestingDecisionListResult convertBillQualityTestingDecisionResult(BillQualityTesting billQualityTesting);

    /**
     * 响应转换
     *
     * @param billQualityTesting
     * @return
     */
    FixQualityTestingResult convertFixBillQualityTestingResult(BillQualityTesting billQualityTesting);

    /**
     * list转换
     *
     * @param billQualityTesting
     * @return
     */
    QualityTestingListResult convertQualityTestingListResult(BillQualityTesting billQualityTesting);

    /**
     * 质检单转详情
     *
     * @param billQualityTesting
     * @return
     */
    QualityTestingDetailsResult convertQualityTestingDetailsResult(BillQualityTesting billQualityTesting);

    /**
     * @param qualityTestingDetailsResult
     * @return
     */
    BillQualityTesting convertBillQualityTesting(QualityTestingDetailsResult qualityTestingDetailsResult);

    /**
     * 转换stock
     *
     * @param request
     * @return
     */
    @Mappings(value = {
            @Mapping(source = "stockId", target = "id")
    })
    Stock convertStock(BillQualityTesting request);

    /**
     * 转换
     * @param request
     * @return
     */
    FixProjectMapper convertFixProjectMapper(QualityTestingDecisionRequest.FixProjectMapper request);
}
