package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseTaskCreateResult implements Serializable {

    private Integer id;

    /**
     * 采购单号
     */
    private String serialNo;

//    private Integer businessKey;
//
//    private String shortcodes;
//
//    private Integer storeId;
//
//    private BigDecimal totalPurchasePrice;
//
//    private List<PurchaseDetailsResult.PurchaseLineVO> line;
//
//    private Integer createdId;
//    private String createdBy;


    /**
     * 采购对接人
     */
    private String purchaseJoin;
}
