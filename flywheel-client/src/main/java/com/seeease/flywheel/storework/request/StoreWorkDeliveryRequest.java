package com.seeease.flywheel.storework.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 发货接口
 *
 * @Auther Gilbert
 * @Date 2023/1/17 18:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkDeliveryRequest implements Serializable {

    /**
     * 作业单id集合
     */
    private List<Integer> workIds;

    /**
     * 发货物流单号
     */
    private String deliveryExpressNumber;

    /**
     * 是否门店发货
     */
    private boolean shopDelivery;

    /**
     * 是否批量发货
     */
    private boolean batchDelivery;
}
