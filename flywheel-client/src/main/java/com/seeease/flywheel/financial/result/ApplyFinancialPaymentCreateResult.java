package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wbh
 * @date 2023/5/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFinancialPaymentCreateResult implements Serializable {
    /**
     * 打款单id
     */
    private Integer id;

    /**
     * 打款单号
     */
    private String serialNo;

    /**
     * 打款金额
     */
    //@ValidValue(message = "打款金额不能为空")
    private BigDecimal pricePayment;
}
