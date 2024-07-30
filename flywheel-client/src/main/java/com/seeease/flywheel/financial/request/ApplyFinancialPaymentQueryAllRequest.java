package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/6 16:40
 */
@Data
public class ApplyFinancialPaymentQueryAllRequest extends PageRequest {

    /**
     * 类型
     *    PEER_PROCUREMENT(0, "同行采购"),
     *     PERSONAL_RECYCLING(1, "个人回收"),
     *     SEND_PERSON(2, "个人寄售"),
     *     BUY_BACK(3, "个人回购"),
     *     INDEPENDENT_FINANCIAL_SETTLEMENT(4, "财务自主结算"),
     */
    private Integer typePayment;

    /**
     * 单号查询
     */
    private String serialNo;

    private List<Integer> salesMethod;
}
