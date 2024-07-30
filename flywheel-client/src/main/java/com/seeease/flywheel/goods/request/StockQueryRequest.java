package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/10 09:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockQueryRequest extends PageRequest {

    /**
     * 表身号
     */
    private List<String> stockSnList;

    /**
     * 是否可售
     */
    private boolean isSaleable;

    private List<Integer> stockIds;

}
