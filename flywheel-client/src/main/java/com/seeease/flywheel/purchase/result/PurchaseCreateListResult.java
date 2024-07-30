package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseCreateListResult implements Serializable {

    private Integer id;

    /**
     * 采购单号
     */
    private String serialNo;

    private Integer businessKey;

    private String shortcodes;

    private Integer storeId;

    private BigDecimal totalPurchasePrice;

    private List<PurchaseDetailsResult.PurchaseLineVO> line;

    private Integer createdId;
    private String createdBy;

    private PurchaseCreateListResult.PurchaseTaskVO purchaseTaskVO;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseTaskVO implements Serializable {

//        private String processInstanceId;
//
//        private String activityId;

        private String serialNo;
    }
}
