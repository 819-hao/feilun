package com.seeease.flywheel.financial.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialInvoiceMaycurRequest implements Serializable {
    private String invoiceStatus;
    private String serialNo;
    private String rejectReason;
    private String pdfUrl;
    private String invoiceTitle;
    private String unitTaxNumber;
    private Integer invoiceOrigin;
    private Integer invoiceType;
    private Date openTicketTime;
    /**
     * 开票号码
     */
    private String invoiceNumber;
    /**
     * 使用场景
     */
    private UseScenario useScenario;
    public enum UseScenario {

        CANCEL,
        CREATE,
        QRY,
        STATUS_NOTIFY_FAIL,
        STATUS_NOTIFY_SUCCEED,
        UPDATE,

    }
}
