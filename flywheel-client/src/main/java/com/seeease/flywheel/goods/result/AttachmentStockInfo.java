package com.seeease.flywheel.goods.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2024/1/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentStockInfo implements Serializable {

    /**
     * 唯一id,没有唯一编码前端选择时存在问题
     */
    private String uuid;

    /**
     * 仓库id
     */
    private Integer storeId;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 无参数的
     */
    private Boolean single;

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 系列
     */
    private String seriesName;

    /**
     * 型号
     */
    private String model;

    /**
     * 主图
     */
    private String image;

    /**
     * 采购价
     */
    private BigDecimal purchasePrice;

    /**
     * 颜色
     */
    private String colour;

    /**
     * 材质
     */
    private String material;

    /**
     * 尺寸
     */
    private String size;

    /**
     * 适用腕表型号
     */
    private String gwModel;

    /**
     * 库存量
     */
    private Integer inventory;

    /**
     * 仓库
     */
    private String storeName;
}
