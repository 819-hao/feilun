package com.seeease.flywheel.sale.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 销售发货视频等资源数据
 *
 * @author Tiro
 * @date 2023/9/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleDeliveryVideoData implements Serializable {
    /**
     * 库存商品id
     */
    private SaleDeliveryVideoData.MediaTypeEnum mediaType;
    /**
     * 资源地址
     */
    private String resource;

    /**
     * 资源类型
     */
    enum MediaTypeEnum {
        VIDEO,
        IMAGE
    }
}
