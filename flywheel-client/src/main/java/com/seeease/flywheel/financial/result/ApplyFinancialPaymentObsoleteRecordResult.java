package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wbh
 * @date 2023/5/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFinancialPaymentObsoleteRecordResult implements Serializable {
    /**
     * 打款单id
     */
    private Integer id;
    /**
     * 创建人id
     */
    private Integer createdId;


    /**
     * 打款单号
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
