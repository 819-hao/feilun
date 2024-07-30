package com.seeease.flywheel.storework.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/4
 */
@Data
public class StoreWorkOutStorageRequest implements Serializable {
    /**
     * 作业单id集合
     */
    private List<Integer> workIds;
}
