package com.seeease.flywheel.purchase.result;

import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseAcceptRepairResult implements Serializable {
    /**
     * 采购单id
     */
    private Integer id;

    private Integer purchaseSource;

    /**
     * 采购单号
     */
    private String serialNo;

    /**
     * 客户id
     */
    private Integer customerId;

    private Integer customerContactId;

    /**
     * 入库单
     */
    private List<StoreWorkCreateResult> storeWorkList;

    private String shortcodes;
}
