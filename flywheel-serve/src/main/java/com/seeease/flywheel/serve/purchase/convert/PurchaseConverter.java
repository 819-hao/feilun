package com.seeease.flywheel.serve.purchase.convert;

import com.seeease.flywheel.purchase.request.PurchaseCreateRequest;
import com.seeease.flywheel.purchase.request.PurchaseEditRequest;
import com.seeease.flywheel.purchase.result.PurchaseCreateListResult;
import com.seeease.flywheel.purchase.result.PurchaseDetailsResult;
import com.seeease.flywheel.purchase.result.PurchaseExpressNumberUploadListResult;
import com.seeease.flywheel.purchase.result.PurchaseListResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLineDetailsVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/7
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface PurchaseConverter extends EnumConvert {

    PurchaseConverter INSTANCE = Mappers.getMapper(PurchaseConverter.class);

    @Mappings(
            value = {
                    @Mapping(source = "salePrice", target = "salePrice", defaultValue = "0"),
            }
    )
    BillPurchase convert(PurchaseCreateRequest dto);

    BillPurchase convert(PurchaseEditRequest request);

    @Mappings(value = {
            @Mapping(source = "fixPrice", target = "fixPrice", defaultValue = "0"),
            @Mapping(source = "salesPriority", target = "salesPriority", defaultValue = "0"),
            @Mapping(source = "purchasePrice", target = "purchasePrice", defaultValue = "0"),
            @Mapping(source = "recyclePrice", target = "recyclePrice", defaultValue = "0"),
            @Mapping(source = "salePrice", target = "salePrice", defaultValue = "0"),
            @Mapping(source = "planFixPrice", target = "planFixPrice", defaultValue = "0"),
            @Mapping(source = "dealPrice", target = "dealPrice", defaultValue = "0"),
            @Mapping(source = "recycleServePrice", target = "recycleServePrice", defaultValue = "0"),
            @Mapping(source = "buyBackPrice", target = "buyBackPrice", defaultValue = "0"),
            @Mapping(source = "referenceBuyBackPrice", target = "referenceBuyBackPrice", defaultValue = "0"),
            @Mapping(source = "consignmentPrice", target = "consignmentPrice", defaultValue = "0"),
            @Mapping(source = "clinchPrice", target = "clinchPrice", defaultValue = "0"),
            @Mapping(source = "watchbandReplacePrice", target = "watchbandReplacePrice", defaultValue = "0"),
    })
    BillPurchaseLine convert(PurchaseCreateRequest.BillPurchaseLineDto dto);

    BillPurchaseLine convert(PurchaseEditRequest.BillPurchaseLineDto dto);

    @Mappings(value = {
            @Mapping(source = "stockSn", target = "sn"),
            @Mapping(source = "goodsLevel", target = "level"),
            @Mapping(target = "consignmentPrice", ignore = true),
    })
    Stock convertStock(BillPurchaseLine dto);

    PurchaseListResult convertPurchaseListResult(BillPurchase billPurchase);

    @Mappings(value = {
            @Mapping(source = "purchaseSubjectId", target = "purchaseSubjectId"),
            @Mapping(source = "demanderStoreId", target = "demanderStoreId"),
            @Mapping(source = "viaSubjectId", target = "viaSubjectId"),
            @Mapping(source = "customerContactId", target = "contactId"),
            @Mapping(source = "customerId", target = "customerId"),

    })
    PurchaseDetailsResult convertPurchaseDetailsResult(BillPurchase billPurchase);

    @Mappings(value = {
            @Mapping(source = "createdTime", target = "purchaseCreatedTime"),
    })
    PurchaseExpressNumberUploadListResult convertPurchaseExpressNumberUploadResult(BillPurchase billPurchase);

    List<PurchaseDetailsResult.PurchaseLineVO> convertPurchaseLineVO(List<BillPurchaseLineDetailsVO> vo);

    @Mappings(value = {
            @Mapping(source = "purchaseSource", target = "businessKey")
    })
    PurchaseCreateListResult convertPurchaseCreateResult(BillPurchase billPurchase);

    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "serialNo", ignore = true),
            @Mapping(target = "purchaseType", ignore = true),
            @Mapping(target = "purchaseMode", ignore = true),
            @Mapping(target = "purchaseSource", ignore = true),
            @Mapping(target = "applyPaymentSerialNo", ignore = true),
            @Mapping(target = "totalPurchasePrice", ignore = true),
            @Mapping(target = "purchaseState", ignore = true),
            @Mapping(target = "saleSerialNo", ignore = true),
            @Mapping(target = "salePrice", ignore = true),
            @Mapping(target = "originSaleSerialNo", ignore = true),
            @Mapping(target = "dealEndTime", ignore = true),
            @Mapping(target = "dealBeginTime", ignore = true),
            @Mapping(target = "recycleModel", ignore = true),
            @Mapping(target = "frontIdentityCard", ignore = true),
            @Mapping(target = "reverseIdentityCard", ignore = true),
            @Mapping(target = "agreementTransfer", ignore = true),
            @Mapping(target = "recoveryPricingRecord", ignore = true),
            @Mapping(target = "buyBackTransfer", ignore = true),
            @Mapping(target = "remarks", ignore = true),

            @Mapping(target = "revision", ignore = true),
            @Mapping(target = "createdId", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdTime", ignore = true),
            @Mapping(target = "updatedId", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "updatedTime", ignore = true),
            @Mapping(target = "deleted", ignore = true),

    })
    BillPurchase convertBillPurchase(BillPurchase billPurchase);

    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "purchaseId", ignore = true),
            @Mapping(target = "purchasePrice", ignore = true),
            @Mapping(target = "purchaseLineState", ignore = true),
            @Mapping(target = "originStockId", ignore = true),
            @Mapping(target = "originPurchaseReturnSerialNo", ignore = true),
            @Mapping(target = "recyclePrice", ignore = true),

            @Mapping(target = "salePrice", ignore = true),
            @Mapping(target = "dealPrice", ignore = true),
            @Mapping(target = "recycleServePrice", ignore = true),
            @Mapping(target = "buyBackPrice", ignore = true),
            @Mapping(target = "referenceBuyBackPrice", ignore = true),
            //todo
            @Mapping(target = "consignmentPrice", ignore = true),
            @Mapping(target = "clinchPrice", ignore = true),
            @Mapping(target = "watchbandReplacePrice", ignore = true),

            @Mapping(target = "revision", ignore = true),
            @Mapping(target = "createdId", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdTime", ignore = true),
            @Mapping(target = "updatedId", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "updatedTime", ignore = true),
            @Mapping(target = "deleted", ignore = true),
    })
    BillPurchaseLine convertBillPurchaseLine(BillPurchaseLine billPurchaseLine);

    /**
     * 数组转
     *
     * @param billPurchaseLine
     * @return
     */
    PurchaseDetailsResult.PurchaseLineVO convertPurchaseLineVO(BillPurchaseLine billPurchaseLine);

}
