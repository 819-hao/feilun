package com.seeease.flywheel.sale.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/7/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderAccuracyQueryRequest implements Serializable {

    /**
     * 第三方订单编号
     */
    private List<String> thirdOrderNoList;
}
