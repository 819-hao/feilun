package com.seeease.flywheel.express.request.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/26 15:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseSfExpressRequest implements Serializable {

    @JsonProperty("requestID")
    private String requestID;

}
