package com.seeease.flywheel.storework.result;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

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
@Setter
public class StoreWorkDeliveryResult implements Serializable {

    /**
     * 作业单id集合
     */
    private List<Integer> workIds;

    private List<StoreWorkCreateResult> storeWorkCreateResultList;
}
