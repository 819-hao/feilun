package com.seeease.flywheel.purchase.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/9/25
 */
@Data
public class AttachmentStockRecordListRequest extends PageRequest implements Serializable {
    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 采购单号
     */
    private String purchaseSerialNo;

    /**
     * 供应商
     */
    private String customerName;
}
