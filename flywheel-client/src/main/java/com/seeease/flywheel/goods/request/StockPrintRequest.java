package com.seeease.flywheel.goods.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/2 14:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPrintRequest implements Serializable {

    private List<Integer> stockIdList;
}
