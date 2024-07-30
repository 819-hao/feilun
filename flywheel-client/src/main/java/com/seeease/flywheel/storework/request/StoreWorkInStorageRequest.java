package com.seeease.flywheel.storework.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/2/3
 */
@Data
public class StoreWorkInStorageRequest implements Serializable {
    /**
     * 作业单id集合
     */
    private List<Integer> workIds;

    private Integer storeId;
}
