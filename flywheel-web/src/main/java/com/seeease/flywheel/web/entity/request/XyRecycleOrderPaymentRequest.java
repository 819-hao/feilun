package com.seeease.flywheel.web.entity.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 打款
 *
 * @author Tiro
 * @date 2023/10/20
 */
@Data
public class XyRecycleOrderPaymentRequest implements Serializable {

    /**
     * id
     */
    private Integer id;

    /**
     * 打款金额
     */
    private BigDecimal paymentPrice;

    /**
     * 打款流水号
     */
    private String paymentNo;

    /**
     * 打款时间
     */
    private Date paymentTime;
}
