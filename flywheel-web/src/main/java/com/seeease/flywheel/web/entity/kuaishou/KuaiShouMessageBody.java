package com.seeease.flywheel.web.entity.kuaishou;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 快手消息体
 *
 * @author Tiro
 * @date 2023/4/25
 */
@Data
public class KuaiShouMessageBody implements Serializable {

    /**
     * 消息唯一id
     */
    @JsonAlias("eventId")
    private String eventId;
    /**
     * 业务消息内容唯一id
     */
    @JsonAlias("msgId")
    private String msgId;
    /**
     * 业务id如订单id、退款单id、商品id
     */
    @JsonAlias("bizId")
    private Long bizId;
    /**
     * 授权用户id
     */
    @JsonAlias("userId")
    private Long userId;
    /**
     * 授权用户openId
     */
    @JsonAlias("openId")
    private String openId;
    /**
     * 应用id
     */
    @JsonAlias("appKey")
    private String appKey;
    /**
     * 消息标示
     */
    @JsonAlias("event")
    private String event;
    /**
     * 状态 0未知 1发送中 2发送成功 3发送失败
     */
    @JsonAlias("status")
    private Integer status;
    /**
     * 创建时间
     */
    @JsonAlias("createTime")
    private Long createTime;
    /**
     * 更新时间
     */
    @JsonAlias("updateTime")
    private Long updateTime;

    /**
     * 消息内容，业务内容Json串，详见消息文档参数
     */
    @JsonAlias("info")
    private String info;

    /**
     * 是否心跳测试事件
     */
    @JsonAlias("test")
    private Boolean test;

    @AllArgsConstructor
    @Getter
    public enum KuaiShouMessageBodyEvent {
        UNKNOWN(""),

        //订单创建
        TRADE_CREATE("100"),

        //订单已支付(订单状态：[0, "未知状态"], [10, "待付款"], [30, "已付款"], [40, "已发货"], [50, "已签收"], [70, "订单成功"], [80, "订单失败"]; 订单取消后会转为“订单失败”状态)
        TRADE_PAID("kwaishop_order_paySuccess"),

        //订单交易失败消息
        TRADE_CANCELED("kwaishop_order_orderFail"),

        //买家收货信息变更
        TRADE_ADDRESS_CHANGE("105"),

        //买家发起售后申请消息
        REFUND_CREATED("200"),
        //买家修改售后申请消息
        REFUND_MODIFIED("208"),
        //买家售后关闭消息
        REFUND_CLOSED("207"),

        //
        REFUND_AGREED("kwaishop_aftersales_addRefund"),

        //
        RETURN_APPLY_AGREED("kwaishop_aftersales_updateRefund"),

        ;

        private String value;

        public static KuaiShouMessageBodyEvent fromCode(String value) {
            return Arrays.stream(KuaiShouMessageBodyEvent.values())
                    .filter(t -> t.getValue().equals(value))
                    .findFirst()
                    .orElse(UNKNOWN);
        }
    }
}
