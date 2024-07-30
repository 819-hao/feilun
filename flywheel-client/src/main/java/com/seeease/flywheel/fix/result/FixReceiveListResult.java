package com.seeease.flywheel.fix.result;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 维修收货
 * @Date create in 2023/1/17 17:25
 */
@Data
@Builder
public class FixReceiveListResult implements Serializable {

    private Integer id;

    /**
     * 维修单号
     */
    private String serialNo;

    /**
     * 维修商品
     */
    private Integer stockId;

    private Integer fixSource;

    private Integer shopId;
}
