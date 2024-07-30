package com.seeease.flywheel.storework.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 发货查询接口
 *
 * @Auther Gilbert
 * @Date 2023/1/17 18:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkDeliveryQueryRequest implements Serializable {

    /**
     * 表身号
     */
    private Integer stockId;

    /**
     * 关联单号
     */
    private String originSerialNo;
}
