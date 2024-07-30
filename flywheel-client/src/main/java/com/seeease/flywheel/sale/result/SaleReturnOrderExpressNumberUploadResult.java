package com.seeease.flywheel.sale.result;

import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleReturnOrderExpressNumberUploadResult implements Serializable {
    /**
     * id
     */
    private Integer id;

    private Integer saleReturnSource;

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 客户id
     */
    private Integer customerId;

    private Integer customerContactId;

    /**
     * 出库单
     */
    private List<StoreWorkCreateResult> storeWorkList;

    private Integer deliveryLocationId;
}
