package com.seeease.flywheel.web.common.express.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/9/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressRecoveryOrderResult implements Serializable {
    /**
     * 下单成功
     */
    private boolean success;
    /**
     * 异常消息
     */
    private String errMsg;
}
