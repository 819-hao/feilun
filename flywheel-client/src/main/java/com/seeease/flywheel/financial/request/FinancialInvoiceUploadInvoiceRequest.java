package com.seeease.flywheel.financial.request;

import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class FinancialInvoiceUploadInvoiceRequest implements Serializable {

   private Integer id;

    /**
     * 开票号码
     */
    private String invoiceNumber;

    private String batchPictureUrl;
}
