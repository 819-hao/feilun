package com.seeease.flywheel.web.controller.xianyu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/10/19
 */
@Getter
@AllArgsConstructor
public enum XianYuOrderStatusEnum {
    CREATE_DONE(1, "订单创建"),
    CHANGE_ADDRESS_11(11, "用户修改取件地址，待服务商确认"),
    BUYER_PACKED(2, "已上门取件"),
    BUYER_RECEIVING(21, "已收货"),
    BUYER_QUALITY_CHECKED(3, "已质检"),
    SELLER_APPLY_SECOND_CHECK_3_31(31, "用户撤回取消申请"),
    SELLER_ORDER_CONFIRMED(4, "卖家确认交易完成"),
    BUYER_ORDER_CONFIRMED(5, "回收商确认交易完成"),
    SELLER_ORDER_RATED(6, "卖家订单已评价"),
    BUYER_ORDER_RATED(7, "回收商订单已评价"),
    BUYER_CREDIT_PAY(8, "回收商信用预付打款"),
    APPLY_REFUND_GOODS(100, "申请退回"),
    GOOD_HAS_REDUND(101, "货物已退回"),
    SELLER_CANCLE_ORDER(102, "卖家关闭订单"),
    BUYER_CANCLE_ORDER(103, "回收商关闭订单"),
    ZFB_DK_FAIL(104, "支付宝代扣失败"),
    ZFB_DK_SUCCESS(105, "支付宝代扣成功"),
    ZFB_DK_DELAY(106, "支付宝代扣逾期"),
    PACKED_TIME_OUT_107(107, "上门取件超时"),
    ;
    private int code;
    private String desc;

    public static XianYuOrderStatusEnum findByCode(Integer code) {
        return Arrays.stream(XianYuOrderStatusEnum.values())
                .filter(t -> t.getCode() == code)
                .findFirst()
                .orElse(null);
    }
}
