package com.seeease.flywheel.express.request.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/26 13:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SfExpressAccessTokenRequest implements Serializable {
    @JsonProperty("partnerID")
    private String partnerID;
}
