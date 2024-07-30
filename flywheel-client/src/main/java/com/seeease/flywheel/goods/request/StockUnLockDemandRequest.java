package com.seeease.flywheel.goods.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/8/18
 */
@Data
public class StockUnLockDemandRequest implements Serializable {
    /**
     * stock id
     */
    private Integer stockId;
}