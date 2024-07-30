package com.seeease.flywheel.serve.goods.enums;

/**
 * @Auther Gilbert
 */
public enum StrategySuggestionEnums {
    SHOP_SUGGESTION("1", "推荐门店销售"),
    MERCHANT_SUGGESTION("2", "推荐商家销售");
    Integer type;
    String name;
    StrategySuggestionEnums(String type, String name) {
        this.name = name;
        this.type = Integer.valueOf(type);
    }
        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    //匹配枚举
        public static StrategySuggestionEnums match(Integer type) {
            if (type != null) {
                for (StrategySuggestionEnums strategySuggestionEnums : StrategySuggestionEnums.values()) {
                    if (strategySuggestionEnums.type == type) {
                        return strategySuggestionEnums;
                    }
                }
            }
            return null;
        }
}
