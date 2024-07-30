package com.seeease.flywheel.web.entity.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

/**
 * @author Tiro
 * @date 2023/7/20
 */
@Data
public class DouYinProductMappingListRequest extends PageRequest {

    /**
     * 抖音商品id
     */
    private String douYinProductId;

    /**
     * 抖音sku_id
     */
    private String douYinSkuId;

    /**
     * 型号编码
     */
    private String modelCode;
}
