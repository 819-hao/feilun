package com.seeease.flywheel.serve.dict.constant;

import java.util.HashMap;
import java.util.Map;

public interface StockConsts {

    Map<Integer, String> map = new HashMap<Integer, String>(){{
        put(1,"保卡");
        put(2,"空白保卡");
    }};
}
