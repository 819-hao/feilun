package com.seeease.flywheel.serve.storework.convert;

import com.seeease.flywheel.rfid.result.RfidWorkDetailResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.goods.entity.ExtAttachmentStock;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.entity.LogStoreWorkOpt;
import com.seeease.flywheel.storework.request.StoreWorKCreateRequest;
import com.seeease.flywheel.storework.request.StoreWorkEditRequest;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.flywheel.storework.result.StoreWorkDetailResult;
import com.seeease.flywheel.storework.result.StoreWorkListByModeResult;
import com.seeease.flywheel.storework.result.StoreWorkListResult;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Auther Gilbert
 * @Date 2023/1/17 17:37
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface BillStoreWorkPreConvert extends EnumConvert {
    BillStoreWorkPreConvert INSTANCE = Mappers.getMapper(BillStoreWorkPreConvert.class);

    //转换传入参数和返回参数
    List<BillStoreWorkPre> convert(List<StoreWorKCreateRequest> request);

    //列表页
    List<StoreWorkListResult> convertStoreWorkListResult(List<BillStoreWorkPre> request);

    List<StoreWorkListByModeResult> convertStoreWorkListByModeResult(List<BillStoreWorkPre> request);

    /**
     * 响应转换
     *
     * @param billStoreWorkPre
     * @return
     */
    List<StoreWorkCreateResult> convertStoreWorkWaitReceivingResult(List<BillStoreWorkPre> billStoreWorkPre);

    @Mappings(value = {
            @Mapping(source = "workId", target = "id")
    })
    BillStoreWorkPre convertBillStoreWorkPre(StoreWorkEditRequest request);

    /**
     * 转换
     *
     * @param billStoreWorkPre
     * @return
     */
    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdId", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdTime", ignore = true),
            @Mapping(target = "updatedId", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "updatedTime", ignore = true),
    })
    LogStoreWorkOpt convertLogStoreWorkOpt(BillStoreWorkPre billStoreWorkPre);


    StoreWorkDetailResult convertStoreWorkDetailResult(BillStoreWorkPre billStoreWorkPre);


    @Mapping(source = "stockSn", target = "stockSn", defaultValue = "")
    @Mapping(target = "wno", expression = "java(\"\")")
    @Mapping(target = "workId", source = "id")
    RfidWorkDetailResult toRfidDetail(StoreWorkDetailResult storeWorkDetailResult);


    StoreWorkListResult.ExtAttachmentStockVO convert(ExtAttachmentStock extAttachmentStock);
}
