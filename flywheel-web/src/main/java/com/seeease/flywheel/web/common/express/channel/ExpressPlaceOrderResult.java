package com.seeease.flywheel.web.common.express.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/9/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressPlaceOrderResult implements Serializable {
    /**
     * 业务订单号，下单唯一
     */
    private String businessNo;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 下单成功
     */
    private boolean success;
    /**
     * 异常消息
     */
    private String errMsg;
    /**
     * 物流单号
     */
    private String expressNumber;
}
