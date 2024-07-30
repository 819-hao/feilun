package com.seeease.flywheel.storework.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 查询列表页请求
 *
 * @Auther Gilbert
 * @Date 2023/1/18 10:15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkOutStorageDetailRequest implements Serializable {

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 归属门店id
     */
    private Integer belongingStoreId;
}
