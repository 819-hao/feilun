package com.seeease.flywheel.web.common.express.client;

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

    private MsgDataDTO msgData;

    @NoArgsConstructor
    @Data
    public static class MsgDataDTO implements Serializable {

        private List<RouteRespsDTO> routeResps;
    }

    @NoArgsConstructor
    @Data
    public static class RouteRespsDTO implements Serializable {

        private String mailNo;

        private List<RoutesDTO> routes;
    }

    @NoArgsConstructor
    @Data
    public static class RoutesDTO implements Serializable {

        private String acceptTime;

        private String acceptAddress;

        private String opCode;

        private String remark;
    }
}
