package com.seeease.flywheel.storework.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 入库已收货接口
 *
 * @Auther Gilbert
 * @Date 2023/1/17 18:46
 */
@Data
public class StoreWorkReceivedRequest implements Serializable {

    /**
     * 作业单id集合
     */
    private List<Integer> workIds;

    /**
     * 物流收货状态
     */
    private Integer logisticsRejectState;

    /**
     * 是否门店收货
     */
    private boolean shopReceived;
}
