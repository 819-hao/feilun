package com.seeease.flywheel.sale.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author trio
 * @date 2023/1/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DouYinSaleOrderListRequest implements Serializable {
    /**
     * 销售单状态
     */
    private Integer saleState;

    private Date updatedTime;

    private List<String> serialNoList;
}
