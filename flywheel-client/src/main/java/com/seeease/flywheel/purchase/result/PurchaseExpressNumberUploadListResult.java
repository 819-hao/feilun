package com.seeease.flywheel.purchase.result;

import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseExpressNumberUploadListResult implements Serializable {
    /**
     * 采购单id
     */
    private Integer id;

    private Integer purchaseSource;
    private Integer purchaseType;

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

    /**
     * 采购创建时间
     */
    private Date purchaseCreatedTime;

    private String applyPaymentSerialNo;

}
