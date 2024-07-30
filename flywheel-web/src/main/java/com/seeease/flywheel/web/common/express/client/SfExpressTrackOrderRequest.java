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
 * @Date create in 2023/6/27 14:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SfExpressTrackOrderRequest extends SfExpressBaseRequest implements Serializable {
    private String language;
    private String trackingType;
    private List<String> trackingNumber;
    private String methodType;
}
