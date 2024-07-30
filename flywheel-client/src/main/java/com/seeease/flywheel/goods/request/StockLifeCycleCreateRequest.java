package com.seeease.flywheel.goods.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/27 15:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockLifeCycleCreateRequest implements Serializable {

    private String wno;

    private Integer stockId;

    private Integer storeId;

    private String originSerialNo;

    private String operationDesc;

    //定价异步
    private String updatedBy;

    private Integer updatedId;

    private Integer createdId;

    private String createdBy;

    /**
     * 操作时间，时间戳
     */
    private Long operationTime;
}
