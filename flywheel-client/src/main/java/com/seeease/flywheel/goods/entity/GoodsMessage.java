package com.seeease.flywheel.goods.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
public class GoodsMessage implements Serializable {
    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 消息创建时间
     */
    private Date messageCreateTime;
}
