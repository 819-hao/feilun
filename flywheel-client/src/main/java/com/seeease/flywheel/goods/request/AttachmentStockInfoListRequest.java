package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
import lombok.*;

import java.util.List;

/**
 * @author Tiro
 * @date 2024/1/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentStockInfoListRequest extends PageRequest {

    /**
     * 仓库id
     */
    private Integer storeId;

    /**
     * 品牌id
     */
    private List<Integer> brandIdList;

    /**
     * 型号
     */
    private String model;

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
     * 尺寸类型
     */
    private Integer sizeType;

    /**
     * 适用腕表型号
     */
    private String gwModel;

    /**
     * 使用场景
     */
    private UseScenario useScenario;

    public enum UseScenario {
        /**
         * 调拨场景
         */
        ALLOCATE,
        /**
         * 库存列表
         */
        STOCK_LIST
    }
}
