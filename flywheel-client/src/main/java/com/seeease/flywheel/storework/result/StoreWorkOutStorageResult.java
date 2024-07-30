package com.seeease.flywheel.storework.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkOutStorageResult implements Serializable {
    /**
     * 作业单id集合
     */
    private List<Integer> workIds;

    private List<StoreWorkCreateResult> storeWorkCreateResultList;

    /**
     * 是否需要质检
     */
    private Integer needQt;
}
