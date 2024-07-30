package com.seeease.flywheel.goods.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/11/20
 */
@Data
public class StockGuaranteeCardManageFindRequest implements Serializable {
    /**
     * 库存id
     */
    private List<Integer> stockIdList;
}
