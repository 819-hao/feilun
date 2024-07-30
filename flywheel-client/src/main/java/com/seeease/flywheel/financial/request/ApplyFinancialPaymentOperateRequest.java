package com.seeease.flywheel.financial.request;

import lombok.Data;

import java.io.Serializable;


/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
public class ApplyFinancialPaymentOperateRequest implements Serializable {

    /**
     * 驳回
     * 确认
     * 作废
     * <p>
     * 财务
     * PAID(1, "已打款"),//已确认
     * REJECTED(2, "已驳回"),
     * OBSOLETE(4, "已作废"),
     * <p>
     * 业务自己操作 则为
     * *     CANCEL(3, "已取消"),
     */
    private Integer state;

    /**
     * 驳回原因 或者 打款凭证
     *
     */
    private String result;

    private Integer id;

    private Integer bankId;

    private Integer customerContactsId;

    private String operator;

    /**
     * 是否重复
     */
    private Integer whetherRepeat;


}
