package com.seeease.flywheel.express.result.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/27 14:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseSfExpressDataResult implements Serializable {

    @JsonProperty("success")
    private String success;
    @JsonProperty("errorCode")
    private String errorCode;
    @JsonProperty("errorMsg")
    private Object errorMsg;
}
