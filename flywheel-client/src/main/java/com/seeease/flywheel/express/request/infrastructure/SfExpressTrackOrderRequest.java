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
 * @Date create in 2023/6/27 14:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SfExpressTrackOrderRequest implements Serializable {

    @JsonProperty("language")
    private String language;
    @JsonProperty("trackingType")
    private String trackingType;
    @JsonProperty("trackingNumber")
    private List<String> trackingNumber;
    @JsonProperty("methodType")
    private String methodType;
}
