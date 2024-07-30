package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/9/25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentStockImportResult implements Serializable {
    /**
     * 采购单id
     */
    private List<Integer> purchaseIdList;
}
