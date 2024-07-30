package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业微信小程序---新增确认收货单
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountReceiptConfirmRejectedResult implements Serializable {

    /**
     * 打款单id
     */
    private Integer id;

    /**
     * 创建人id
     */
    private Integer createdId;


    /**
     * 单号
     */
    private String serialNo;

    /**
     * 状态
     */
    private String state;

    /**
     * 门店
     */
    private Integer shopId;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;

}
