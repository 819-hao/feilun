package com.seeease.flywheel.web.entity.douyin;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/4/27
 */
@Data
public class DouYinProductFormatNew implements Serializable {

    Map<String, List<ProductProperty>> productPropertyMap;

    @Data
    public static class ProductProperty {
        @JSONField(name = "PropertyName")
        private String propertyName;

        @JSONField(name = "Name")
        private String name;
    }


    /**
     * 获取型号
     *
     * @param value
     * @return
     */
    public static String getGoodsModel(String value) {
        Map<String, List<ProductProperty>> productPropertyMap = JSONObject.parseObject(value, new TypeReference<Map<String, List<ProductProperty>>>() {
        });

        return productPropertyMap.values().stream()
                .flatMap(Collection::stream)
                .filter(t -> t.getPropertyName().equals("型号"))
                .map(ProductProperty::getName)
                .findFirst()
                .orElse(null);
    }


}
