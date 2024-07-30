package com.seeease.flywheel.express.result.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 业务操作响应的模版
 * @Date create in 2023/6/27 10:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseSfExpressBusinessResult extends BaseSfExpressResult implements Serializable {

    @JsonProperty("apiResultData")
    private String apiResultData;
}
