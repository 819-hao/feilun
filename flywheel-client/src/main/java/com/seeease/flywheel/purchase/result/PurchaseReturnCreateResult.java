package com.seeease.flywheel.purchase.result;

import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseReturnCreateResult implements Serializable {

    private Integer id;

    /**
     * 采购退货单号
     */
    private String serialNo;
    /**
     * 采购退货类型
     */
    private Integer purchaseReturnType;
    /**
     * 出库单
     */
    private List<StoreWorkCreateResult> storeWorkList;

    private List<String> serialList;

    private Integer customerId;

    private Integer customerContactId;

    private Integer storeId;

    private String shortcodes;
}
