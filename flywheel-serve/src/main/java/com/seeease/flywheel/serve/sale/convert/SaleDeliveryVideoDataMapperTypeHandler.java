package com.seeease.flywheel.serve.sale.convert;

import com.alibaba.fastjson.TypeReference;
import com.seeease.flywheel.sale.entity.SaleDeliveryVideoData;
import com.seeease.seeeaseframework.mybatis.type.JsonArrayTypeHandler;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/9/15
 */
public class SaleDeliveryVideoDataMapperTypeHandler extends JsonArrayTypeHandler<SaleDeliveryVideoData> {
    /**
     * @param clazz
     */
    public SaleDeliveryVideoDataMapperTypeHandler(Class<List<SaleDeliveryVideoData>> clazz) {
        super(clazz);
    }

    @Override
    protected TypeReference<List<SaleDeliveryVideoData>> specificType() {
        return new TypeReference<List<SaleDeliveryVideoData>>() {
        };
    }
}
