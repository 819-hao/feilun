package com.seeease.flywheel.goods.result;

import com.seeease.flywheel.goods.entity.GoodsMetaInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsMetaInfoQueryResult implements Serializable {

    /**
     * 商品列表
     */
    private List<GoodsMetaInfo> goodsList;
}
