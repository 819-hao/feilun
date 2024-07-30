package com.seeease.flywheel.serve.allocate.convert;

import com.seeease.flywheel.allocate.request.AllocateCreateRequest;
import com.seeease.flywheel.allocate.result.*;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.serve.allocate.entity.BillAllocate;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateLine;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateTask;
import com.seeease.flywheel.serve.base.EnumConvert;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author Tiro
 * @date 2023/3/7
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface AllocateConverter extends EnumConvert {
    AllocateConverter INSTANCE = Mappers.getMapper(AllocateConverter.class);


    BillAllocate convert(AllocateCreateRequest request);

    @Mappings(
            value = {
                    @Mapping(source = "guaranteeCardManage", target = "guaranteeCardManage", defaultValue = "0"),
            }
    )
    BillAllocateLine convert(AllocateCreateRequest.AllocateLineDto dto);

    AllocateCreateResult.AllocateDto convertAllocateDto(BillAllocate billAllocate);

    AllocateListResult convertAllocateListResult(BillAllocate billAllocate);

    AllocateDetailsResult convertAllocateDetailsResult(BillAllocate billAllocate);

    AllocateDetailsResult.AllocateLineVO convertAllocateLineVO(BillAllocateLine line);

    AllocateStockQueryImportResult convertAllocateStockQueryImportResult(StockBaseInfo stockBaseInfo);

    AllocateExportListResult convertAllocateExportList(BillAllocateLine t);

    AllocateTaskListResult convertAllocateTaskListResult(BillAllocateTask task);

    @Mappings(
            value = {
                    @Mapping(source = "serialNo", target = "allocateNo"),
                    @Mapping(source = "id", target = "allocateId"),
            }
    )
    BillAllocateTask convertBillAllocateTask(BillAllocate allocate);

    AllocateStockBaseInfoImportResult convertAllocateStockBaseInfoImportResult(StockBaseInfo stockBaseInfo);
    BorrowStockBaseInfoImportResult convertBorrowStockBaseInfoImportResult(StockBaseInfo stockBaseInfo);
}
