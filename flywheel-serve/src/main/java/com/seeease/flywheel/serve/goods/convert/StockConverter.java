package com.seeease.flywheel.serve.goods.convert;

import com.seeease.flywheel.goods.entity.GoodsMetaInfo;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.goods.request.StockExt1;
import com.seeease.flywheel.goods.result.*;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.goods.entity.GoodsMetaInfoDto;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.entity.StockPromotion;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author Tiro
 * @date 2023/4/1
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface StockConverter extends EnumConvert {

    StockConverter INSTANCE = Mappers.getMapper(StockConverter.class);

    @Mappings(value = {
            @Mapping(target = "stockId", source = "id"),
            @Mapping(target = "stockSn", source = "sn"),
    })
    StockBaseInfo convertStockBaseInfo(Stock stock);

    GoodsMetaInfo convert(GoodsMetaInfoDto dto);

    /**
     * 转出打印参数
     *
     * @param stock
     * @return
     */
    @Mappings(value = {
            @Mapping(target = "id", source = "stockId"),
            @Mapping(target = "stockSn", source = "stockSn"),
            @Mapping(target = "purchaseSource", source = "stockSrc"),
    })
    StockPrintResult convertStockPrintResult(StockExt stock);

    @Mappings(value = {
            @Mapping(target = "stockId", source = "id"),
            @Mapping(target = "stockSn", source = "sn"),
    })
    StockForNoLoginResult convertStockForNoLoginResult(Stock stock);

    SettleStockQueryImportResult convertSettleStockQueryImportResult(StockBaseInfo t);
    GroupSettleStockQueryImportResult convertGroupSettleStockQueryImportResult(StockBaseInfo t);

    FinancialInvoiceStockQueryImportResult convertInvoiceStockQueryImportResult(StockBaseInfo t);

    StockPromotionInfo convertStockPromotionInfo(StockPromotion promotion);

    StockExt1 to(StockExt v);
}
