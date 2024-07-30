package com.seeease.flywheel.express.request.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class SfExpressCancelOrderRequest implements Serializable {

    @JsonProperty("dealType")
    private Integer dealType;
    @JsonProperty("language")
    private String language;
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("totalWeight")
    private Integer totalWeight;
    @JsonProperty("waybillNoInfoList")
    private List<?> waybillNoInfoList;
}
