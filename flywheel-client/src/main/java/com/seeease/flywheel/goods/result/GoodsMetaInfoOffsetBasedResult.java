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
public class GoodsMetaInfoOffsetBasedResult implements Serializable {
    /**
     * 商品列表
     */
    private List<GoodsMetaInfo> goodsList;
    /**
     * 当前偏移量
     */
    private Integer currentOffset;
    /**
     * 是否最后的数据
     */
    private boolean isEnd;
}
