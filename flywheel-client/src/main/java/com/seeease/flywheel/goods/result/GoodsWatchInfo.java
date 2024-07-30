package com.seeease.flywheel.goods.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
public class GoodsWatchInfo implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 型号
     */
    private String model;

}
