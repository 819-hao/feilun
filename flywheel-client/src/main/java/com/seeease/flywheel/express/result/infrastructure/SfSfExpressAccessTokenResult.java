package com.seeease.flywheel.express.result.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description access_token 模版
 * @Date create in 2023/6/26 13:45
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SfSfExpressAccessTokenResult extends BaseSfExpressResult implements Serializable {
    @JsonProperty("accessToken")
    private String accessToken;
    @JsonProperty("expiresIn")
    private Integer expiresIn;
}
