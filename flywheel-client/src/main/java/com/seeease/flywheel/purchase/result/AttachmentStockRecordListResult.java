package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/9/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentStockRecordListResult implements Serializable {

    /**
     * 采购单号
     */
    private String purchaseSerialNo;

    /**
     * 采购id
     */
    private Integer purchaseId;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 客户联系人
     */
    private Integer customerContactId;

    /**
     * 导入数量
     */
    private Integer countNumber;

    /**
     * 供应商
     */
    private String customerName;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private String createdTime;
}
