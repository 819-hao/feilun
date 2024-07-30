package com.seeease.flywheel.web.entity.kuaishou;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @Author Mr. Du
 * @Description 订单已支付消息
 * @Date create in 2023/11/22 14:00
 */

@Data
public class KuaiShouPaySuccessInfo implements Serializable {
    /**
     * 订单ID
     */
    @JsonAlias("oid")
    private Long oid;
    /**
     * 商家ID
     */
    @JsonAlias("sellerId")
    private Long sellerId;
    /**
     * 订单状态：[0, "未知状态"], [10, "待付款"], [30, "已付款"], [40, "已发货"], [50, "已签收"], [70, "订单成功"], [80, "订单失败"]; 订单取消后会转为“订单失败”状态
     */
    @JsonAlias("status")
    private Integer status;
    /**
     * 业务变更时间
     */
    @JsonAlias("updateTime")
    private Long updateTime;

    /**
     * (订单状态：[0, "未知状态"], [10, "待付款"], [30, "已付款"], [40, "已发货"], [50, "已签收"], [70, "订单成功"], [80, "订单失败"]; 订单取消后会转为“订单失败”状态)
     */
    @AllArgsConstructor
    @Getter
    public enum KuaiShouPaySuccessInfoStatus {
        UNKNOWN(0),
        TRADE_CREATE(10),
        TRADE_PAID(30),
        TRADE_CANCELED(40),
        TRADE_ADDRESS_CHANGE(50),
        REFUND_CREATED(70),
        REFUND_MODIFIED(80),


        ;

        private Integer value;

        public static KuaiShouPaySuccessInfoStatus fromCode(Integer value) {
            return Arrays.stream(KuaiShouPaySuccessInfoStatus.values())
                    .filter(t -> t.getValue().equals(value))
                    .findFirst()
                    .orElse(UNKNOWN);
        }
    }
}
