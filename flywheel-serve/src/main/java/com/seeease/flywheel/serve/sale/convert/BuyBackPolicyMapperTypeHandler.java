package com.seeease.flywheel.serve.sale.convert;

import com.alibaba.fastjson.TypeReference;
import com.seeease.flywheel.serve.sale.entity.BuyBackPolicyMapper;
import com.seeease.seeeaseframework.mybatis.type.JsonArrayTypeHandler;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/18
 */
public class BuyBackPolicyMapperTypeHandler extends JsonArrayTypeHandler<BuyBackPolicyMapper> {
    /**
     * @param clazz
     */
    public BuyBackPolicyMapperTypeHandler(Class<List<BuyBackPolicyMapper>> clazz) {
        super(clazz);
    }

    @Override
    protected TypeReference<List<BuyBackPolicyMapper>> specificType() {
        return new TypeReference<List<BuyBackPolicyMapper>>() {
        };

    }
}
