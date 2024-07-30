package com.seeease.flywheel.serve.fix.convert;

import com.alibaba.fastjson.TypeReference;
import com.seeease.seeeaseframework.mybatis.type.JsonArrayTypeHandler;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/18
 */
public class StringMapperTypeHandler extends JsonArrayTypeHandler<String> {
    /**
     * @param clazz
     */
    public StringMapperTypeHandler(Class<List<String>> clazz) {
        super(clazz);
    }

    @Override
    protected TypeReference<List<String>> specificType() {
        return new TypeReference<List<String>>() {
        };

    }
}
