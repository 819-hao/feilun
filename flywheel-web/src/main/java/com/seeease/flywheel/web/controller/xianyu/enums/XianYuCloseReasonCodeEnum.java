package com.seeease.flywheel.web.controller.xianyu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tiro
 * @date 2023/10/19
 */
@Getter
@AllArgsConstructor
public enum XianYuCloseReasonCodeEnum {
    QA_STAFF_NOT_VISIT_HOME("QA_STAFF_NOT_VISIT_HOME", "质检员未上门取件"),
    SELLER_CAN_NOT_CONTACT("SELLER_CAN_NOT_CONTACT", "用户无法联系"),
    SELLER_NOT_COME_STORE_AT_TIME("SELLER_NOT_COME_STORE_AT_TIME", "用户未按时到店"),
    CANCEL_BY_SELLER_DEMAND("CANCEL_BY_SELLER_DEMAND", "用户要求不回收了"),
    QA_NOT_QUALIFIED("QA_NOT_QUALIFIED", "不符合服务商质检要求"),
    OTHER("OTHER", "其他原因"),
    ;
    private String code;
    private String desc;
}
