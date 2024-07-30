package com.seeease.flywheel.storework.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 发货拦截
 *
 * @author Tiro
 * @date 2023/9/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WmsWorkInterceptRequest implements Serializable {
    /**
     * 第三方单号
     */
    private String bizOrderCode;
    /**
     * 拦截，取消拦截
     */
    private boolean intercept;
}
