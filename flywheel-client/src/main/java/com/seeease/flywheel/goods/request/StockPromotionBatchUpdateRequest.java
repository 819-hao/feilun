package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * @author Tiro
 * @date 2023/3/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPromotionBatchUpdateRequest implements Serializable {


    private List<Integer> ids;

    /**
     * 状态 0下架 1上架
     */
    private Integer status;

}
