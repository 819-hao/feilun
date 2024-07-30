package com.seeease.flywheel.serve.fix.convert;

import com.alibaba.fastjson.TypeReference;
import com.seeease.seeeaseframework.mybatis.type.JsonArrayTypeHandler;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/18
 */
public class IntegerMapperTypeHandler extends JsonArrayTypeHandler<Integer> {
    /**
     * @param clazz
     */
    public IntegerMapperTypeHandler(Class<List<Integer>> clazz) {
        super(clazz);
    }

    @Override
    protected TypeReference<List<Integer>> specificType() {
        return new TypeReference<List<Integer>>() {
        };

    }
}
