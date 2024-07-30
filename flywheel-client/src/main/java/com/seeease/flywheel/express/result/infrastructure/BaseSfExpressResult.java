package com.seeease.flywheel.express.result.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 响应模版，公共参数 三个
 * @Date create in 2023/6/27 10:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseSfExpressResult implements Serializable {

    @JsonProperty("apiErrorMsg")
    private String apiErrorMsg;
    @JsonProperty("apiResponseID")
    private String apiResponseID;
    @JsonProperty("apiResultCode")
    private String apiResultCode;
}
