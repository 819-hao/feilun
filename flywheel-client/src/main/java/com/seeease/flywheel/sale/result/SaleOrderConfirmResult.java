package com.seeease.flywheel.sale.result;

import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 销售订单确认结果
 *
 * @author Tiro
 * @date 2023/3/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderConfirmResult implements Serializable {
    /**
     * 订单id
     */
    private Integer orderId;
    /**
     * 发货位置门店id
     */
    private Integer deliveryLocationId;
    /**
     * 发货位置门店简码
     */
    private String shortcodes;
    /**
     * 出库单列表
     */
    List<StoreWorkCreateResult> storeWorkList;

    private String serialNo;
}
