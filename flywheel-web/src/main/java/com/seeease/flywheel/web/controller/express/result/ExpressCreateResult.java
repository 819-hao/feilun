package com.seeease.flywheel.web.controller.express.result;

import com.doudian.open.api.logistics_newCreateOrder.LogisticsNewCreateOrderResponse;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/9/1 16:29
 */
@Data
@Builder
public class ExpressCreateResult implements Serializable {

    private LogisticsNewCreateOrderResponse logisticsNewCreateOrderResponse;
}
