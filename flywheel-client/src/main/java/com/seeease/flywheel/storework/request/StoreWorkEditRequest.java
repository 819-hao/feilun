package com.seeease.flywheel.storework.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/10 14:56
 */
@Data
public class StoreWorkEditRequest implements Serializable {

    private Integer workId;

    private Integer commoditySituation;

    private String remarks;

//    private String expressNumber;
    private String deliveryExpressNumber;
}
