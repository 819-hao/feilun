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
 * @Date create in 2023/6/27 17:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SfExpressCancelOrderResult extends BaseSfExpressDataResult implements Serializable {
    private MsgDataDTO msgData;

    @NoArgsConstructor
    @Data
    public static class MsgDataDTO implements Serializable {
        private String orderId;
        private List<WaybillNoInfoListDTO> waybillNoInfoList;
        private Integer resStatus;
        private Object extraInfoList;
    }

    @NoArgsConstructor
    @Data
    public static class WaybillNoInfoListDTO implements Serializable {
        private Integer waybillType;
        private String waybillNo;
    }
}
