package com.seeease.flywheel.goods.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/27 20:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockLifeCycleListResult implements Serializable {

    private String createdBy;

    private String createdTime;

    private String operationDesc;

    private String originSerialNo;
}
