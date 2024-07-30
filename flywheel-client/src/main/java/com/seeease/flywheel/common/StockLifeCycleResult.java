package com.seeease.flywheel.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/27 16:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockLifeCycleResult implements Serializable {

    /**
     * 商品编码
     */
    private String stockWno;

    /**
     * 商品id
     */
    private Integer stockId;
//
//    /**
//     * 选项值 参考各自的状态选择
//     * 有选择 则选择 无选择则不需要
//     */
//    private String state;

    private String originSerialNo;

    private String operationDesc;

    /**
     * 异步需要手动传输
     */
    private Integer storeId;

    private String createdBy;

    private Integer createdId;

    /**
     * 创建时间
     */
    private Date createdTime;
}
