package com.seeease.flywheel.storework.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/4 11:24
 */
@Data
public class StoreWorkRfidMatchRequest implements Serializable {

    /**
     * 关联单号
     */
    private String no;

    /**
     * 商品编码列表
     */
    private Set<String> wnoList;
}
