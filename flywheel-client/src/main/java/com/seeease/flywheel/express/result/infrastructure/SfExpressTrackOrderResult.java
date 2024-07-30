package com.seeease.flywheel.express.result.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/27 14:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SfExpressTrackOrderResult extends BaseSfExpressDataResult implements Serializable {

    @JsonProperty("msgData")
    private MsgDataDTO msgData;

    @NoArgsConstructor
    @Data
    public static class MsgDataDTO implements Serializable {
        @JsonProperty("routeResps")
        private List<RouteRespsDTO> routeResps;
    }

    @NoArgsConstructor
    @Data
    public static class RouteRespsDTO implements Serializable {
        @JsonProperty("mailNo")
        private String mailNo;
        @JsonProperty("routes")
        private List<RoutesDTO> routes;
    }

    @NoArgsConstructor
    @Data
    public static class RoutesDTO implements Serializable {
        @JsonProperty("acceptTime")
        private String acceptTime;
        @JsonProperty("acceptAddress")
        private String acceptAddress;
        @JsonProperty("opCode")
        private String opCode;
        @JsonProperty("remark")
        private String remark;
    }
}
