package com.seeease.flywheel.web.common.express.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/27 17:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SfExpressCancelOrderRequest extends SfExpressBaseRequest implements Serializable {
    private Integer dealType;
    private String language;
    private String orderId;
    private Integer totalWeight;
    private List<?> waybillNoInfoList;
}
