package com.seeease.flywheel.recycle.result;

import com.seeease.flywheel.sale.request.SaleLoadRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 打款单信息
 *
 * @Auther Gilbert
 * @Date 2023/9/1 10:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarkektRecycleGetSaleProcessResult implements Serializable {

    /**
     * 回收单主键
     */
    private Integer recycleId;

    /**
     * 销售开启流程对象
     */
    private SaleLoadRequest saleLoadRequest;

}
