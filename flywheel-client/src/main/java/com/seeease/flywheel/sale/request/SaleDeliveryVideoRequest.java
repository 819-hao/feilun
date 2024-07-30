package com.seeease.flywheel.sale.request;

import com.seeease.flywheel.sale.entity.SaleDeliveryVideoData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/9/14
 */
@Data
public class SaleDeliveryVideoRequest implements Serializable {
    /**
     * 库存商品id
     */
    private Integer stockId;
    /**
     * 资源数据
     */
    private List<SaleDeliveryVideoData> data;
}
