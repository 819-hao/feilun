package com.seeease.flywheel.web.common.express.client;

import com.sf.csim.express.service.IServiceCodeStandard;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/9/19
 */
@Data
public class SfExpressBaseRequest implements Serializable {
    /**
     *
     */
    private transient IServiceCodeStandard service;
    /**
     * 请求追踪id
     */
    private transient String requestId;
}
