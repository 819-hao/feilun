package com.seeease.flywheel.serve.purchase.convert;

import com.seeease.flywheel.purchase.request.AttachmentStockImportRequest;
import com.seeease.flywheel.purchase.result.AttachmentStockRecordListResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.goods.entity.ExtAttachmentStock;
import com.seeease.flywheel.serve.goods.entity.ExtAttachmentStockRecord;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.purchase.entity.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author Tiro
 * @date 2023/9/25
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface AttachmentStockRecordConverter extends EnumConvert {
    AttachmentStockRecordConverter INSTANCE = Mappers.getMapper(AttachmentStockRecordConverter.class);


    AttachmentStockRecordListResult convert(ExtAttachmentStockRecord record);

    AttachmentStockImportDto convertDto(AttachmentStockImportRequest.ImportDto importDto);

    @Mappings(value = {
            @Mapping(source = "sn", target = "stockSn"),
            @Mapping(source = "level", target = "goodsLevel"),
            @Mapping(source = "remark", target = "remarks"),
            @Mapping(source = "id", target = "stockId"),
    })
    BillPurchaseLine convertBillPurchaseLine(Stock stock);

    @Mappings(value = {
            @Mapping(source = "stockSn", target = "sn"),
    })
    Stock convertStock(AttachmentStockImportDto importDto);

    ExtAttachmentStock convertExtAttachmentStock(AttachmentStockImportDto importDto);

    @Mappings(value = {
            @Mapping(source = "id", target = "purchaseId"),
            @Mapping(source = "serialNo", target = "purchaseSerialNo"),
    })
    ExtAttachmentStockRecord convertRecord(BillPurchase billPurchase);
}
