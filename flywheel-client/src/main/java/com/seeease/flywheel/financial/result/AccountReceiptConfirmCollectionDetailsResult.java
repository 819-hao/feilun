package com.seeease.flywheel.financial.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountReceiptConfirmCollectionDetailsResult implements Serializable {

    /**
     * 财务流水号
     */
    private String fsSerialNo;

    /**
     * 确认人
     */
    private String confirmor;

    /**
     * 确认时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date confirmTime;

    /**
     * 确认收款金额
     */
    private BigDecimal receivableAmount;
}
