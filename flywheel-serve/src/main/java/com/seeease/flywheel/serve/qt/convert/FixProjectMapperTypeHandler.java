package com.seeease.flywheel.serve.qt.convert;

import com.alibaba.fastjson.TypeReference;
import com.seeease.flywheel.serve.qt.entity.FixProjectMapper;
import com.seeease.seeeaseframework.mybatis.type.JsonArrayTypeHandler;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/18
 */
public class FixProjectMapperTypeHandler extends JsonArrayTypeHandler<FixProjectMapper> {
    /**
     * @param clazz
     */
    public FixProjectMapperTypeHandler(Class<List<FixProjectMapper>> clazz) {
        super(clazz);
    }

    @Override
    protected TypeReference<List<FixProjectMapper>> specificType() {
        return new TypeReference<List<FixProjectMapper>>() {
        };

    }
}
