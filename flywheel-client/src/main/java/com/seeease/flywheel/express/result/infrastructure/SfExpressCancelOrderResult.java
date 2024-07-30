package com.seeease.flywheel.express.result.infrastructure;

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
 * @Date create in 2023/6/27 17:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SfExpressCancelOrderResult extends BaseSfExpressDataResult implements Serializable {

    @JsonProperty("msgData")
    private MsgDataDTO msgData;

    @NoArgsConstructor
    @Data
    public static class MsgDataDTO implements Serializable {
        @JsonProperty("orderId")
        private String orderId;
        @JsonProperty("waybillNoInfoList")
        private List<WaybillNoInfoListDTO> waybillNoInfoList;
        @JsonProperty("resStatus")
        private Integer resStatus;
        @JsonProperty("extraInfoList")
        private Object extraInfoList;
    }

    @NoArgsConstructor
    @Data
    public static class WaybillNoInfoListDTO implements Serializable {
        @JsonProperty("waybillType")
        private Integer waybillType;
        @JsonProperty("waybillNo")
        private String waybillNo;
    }
}
