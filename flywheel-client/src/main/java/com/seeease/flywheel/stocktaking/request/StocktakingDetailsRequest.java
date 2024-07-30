package com.seeease.flywheel.stocktaking.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/6/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakingDetailsRequest  extends PageRequest implements Serializable {

    /**
     * 盘点ID
     */
    private Integer id;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 状态
     */
    private Integer stocktakingLineState;
}
