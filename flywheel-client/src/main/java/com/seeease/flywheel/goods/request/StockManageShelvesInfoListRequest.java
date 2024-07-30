package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

/**
 * @author Tiro
 * @date 2023/9/4
 */
@Data
public class StockManageShelvesInfoListRequest extends PageRequest {

    /**
     * 型号
     */
    private String model;

    /**
     * 品牌id
     */
    private Integer brandId;

}