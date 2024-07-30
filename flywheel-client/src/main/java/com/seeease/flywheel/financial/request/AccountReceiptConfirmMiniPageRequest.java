package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

@Data
public class AccountReceiptConfirmMiniPageRequest extends PageRequest {

    /**
     * 待核销，部分核销---申请人，客户姓名
     * 已核销---关联单号/客户名/金额
     *
     */
    private String searchCriteria;

    /**
     * 核销状态，0待核销 1部分核销 2已核销
     */
    private Integer status;

    /**
     * 当前登录人的shopId
     */
    private Integer shopId;

}
