package com.seeease.flywheel.web.entity.douyin;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/4/25
 */
@Data
public class DouYinMessageBody implements Serializable {
    /**
     * 消息种类，订单支付/确认消息的tag值为"101"
     */
    @JsonAlias("tag")
    private String tag;
    /**
     * 消息记录ID
     */
    @JsonAlias("msg_id")
    private String msgId;
    /**
     * 消息体
     */
    @JsonAlias("data")
    private String data;

    @AllArgsConstructor
    @Getter
    public enum DouYinMessageBodyTag {
        UNKNOWN(""),

        //订单创建
        TRADE_CREATE("100"),

        //订单已支付
        TRADE_PAID("101"),

        //订单取消
        TRADE_CANCELED("106"),

        //买家收货信息变更
        TRADE_ADDRESS_CHANGE("105"),

        //买家发起售后申请消息
        REFUND_CREATED("200"),
        //买家修改售后申请消息
        REFUND_MODIFIED("208"),
        //买家售后关闭消息
        REFUND_CLOSED("207"),

        //同意退款消息
        REFUND_AGREED("201"),

        //同意退货申请
        RETURN_APPLY_AGREED("202"),

        ;

        private String value;

        public static DouYinMessageBodyTag fromCode(String value) {
            return Arrays.stream(DouYinMessageBodyTag.values())
                    .filter(t -> t.getValue().equals(value))
                    .findFirst()
                    .orElse(UNKNOWN);
        }
    }
}
