package com.seeease.flywheel.web.common.express.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/26 15:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SfExpressCreateOrderResult extends BaseSfExpressDataResult implements Serializable {

    private MsgDataDTO msgData;

    @NoArgsConstructor
    @Data
    public static class MsgDataDTO implements Serializable {

        private String orderId;

        private String originCode;

        private String destCode;

        private Integer filterResult;

        private String remark;

        private Object url;

        private Object paymentLink;

        private Object isUpstairs;

        private Object isSpecialWarehouseService;

        private Object mappingMark;

        private Object agentMailno;

        private Object returnExtraInfoList;

        private List<WaybillNoInfoListDTO> waybillNoInfoList;

        private List<RouteLabelInfoDTO> routeLabelInfo;

        private Object contactInfoList;
    }

    @NoArgsConstructor
    @Data
    public static class RouteLabelDataDTO implements Serializable {

        private String waybillNo;

        private String sourceTransferCode;

        private String sourceCityCode;

        private String sourceDeptCode;

        private String sourceTeamCode;

        private String destCityCode;

        private String destDeptCode;

        private String destDeptCodeMapping;

        private String destTeamCode;

        private String destTeamCodeMapping;

        private String destTransferCode;

        private String destRouteLabel;

        private String proName;

        private String cargoTypeCode;

        private String limitTypeCode;

        private String expressTypeCode;

        private String codingMapping;

        private String codingMappingOut;

        private String xbFlag;

        private String printFlag;

        private String twoDimensionCode;

        private String proCode;

        private String printIcon;

        private String abFlag;

        private String destPortCode;

        private String destCountry;

        private String destPostCode;

        private String goodsValueTotal;

        private String currencySymbol;

        private String cusBatch;

        private String goodsNumber;

        private String errMsg;

        private String checkCode;

        private String proIcon;

        private String fileIcon;

        private String fbaIcon;

        private String icsmIcon;

        private String destGisDeptCode;

        private Object newIcon;
    }

    @NoArgsConstructor
    @Data
    public static class RouteLabelInfoDTO implements Serializable {

        private String code;

        private RouteLabelDataDTO routeLabelData;

        private String message;
    }

    @NoArgsConstructor
    @Data
    public static class WaybillNoInfoListDTO implements Serializable {

        private Integer waybillType;

        private String waybillNo;
    }
}
