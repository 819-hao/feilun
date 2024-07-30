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
 * @Date create in 2023/6/26 15:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SfExpressCreateOrderResult extends BaseSfExpressDataResult implements Serializable {
    @JsonProperty("msgData")
    private MsgDataDTO msgData;

    @NoArgsConstructor
    @Data
    public static class MsgDataDTO implements Serializable {
        @JsonProperty("orderId")
        private String orderId;
        @JsonProperty("originCode")
        private String originCode;
        @JsonProperty("destCode")
        private String destCode;
        @JsonProperty("filterResult")
        private Integer filterResult;
        @JsonProperty("remark")
        private String remark;
        @JsonProperty("url")
        private Object url;
        @JsonProperty("paymentLink")
        private Object paymentLink;
        @JsonProperty("isUpstairs")
        private Object isUpstairs;
        @JsonProperty("isSpecialWarehouseService")
        private Object isSpecialWarehouseService;
        @JsonProperty("mappingMark")
        private Object mappingMark;
        @JsonProperty("agentMailno")
        private Object agentMailno;
        @JsonProperty("returnExtraInfoList")
        private Object returnExtraInfoList;
        @JsonProperty("waybillNoInfoList")
        private List<WaybillNoInfoListDTO> waybillNoInfoList;
        @JsonProperty("routeLabelInfo")
        private List<RouteLabelInfoDTO> routeLabelInfo;
        @JsonProperty("contactInfoList")
        private Object contactInfoList;
    }

    @NoArgsConstructor
    @Data
    public static class RouteLabelDataDTO implements Serializable {
        @JsonProperty("waybillNo")
        private String waybillNo;
        @JsonProperty("sourceTransferCode")
        private String sourceTransferCode;
        @JsonProperty("sourceCityCode")
        private String sourceCityCode;
        @JsonProperty("sourceDeptCode")
        private String sourceDeptCode;
        @JsonProperty("sourceTeamCode")
        private String sourceTeamCode;
        @JsonProperty("destCityCode")
        private String destCityCode;
        @JsonProperty("destDeptCode")
        private String destDeptCode;
        @JsonProperty("destDeptCodeMapping")
        private String destDeptCodeMapping;
        @JsonProperty("destTeamCode")
        private String destTeamCode;
        @JsonProperty("destTeamCodeMapping")
        private String destTeamCodeMapping;
        @JsonProperty("destTransferCode")
        private String destTransferCode;
        @JsonProperty("destRouteLabel")
        private String destRouteLabel;
        @JsonProperty("proName")
        private String proName;
        @JsonProperty("cargoTypeCode")
        private String cargoTypeCode;
        @JsonProperty("limitTypeCode")
        private String limitTypeCode;
        @JsonProperty("expressTypeCode")
        private String expressTypeCode;
        @JsonProperty("codingMapping")
        private String codingMapping;
        @JsonProperty("codingMappingOut")
        private String codingMappingOut;
        @JsonProperty("xbFlag")
        private String xbFlag;
        @JsonProperty("printFlag")
        private String printFlag;
        @JsonProperty("twoDimensionCode")
        private String twoDimensionCode;
        @JsonProperty("proCode")
        private String proCode;
        @JsonProperty("printIcon")
        private String printIcon;
        @JsonProperty("abFlag")
        private String abFlag;
        @JsonProperty("destPortCode")
        private String destPortCode;
        @JsonProperty("destCountry")
        private String destCountry;
        @JsonProperty("destPostCode")
        private String destPostCode;
        @JsonProperty("goodsValueTotal")
        private String goodsValueTotal;
        @JsonProperty("currencySymbol")
        private String currencySymbol;
        @JsonProperty("cusBatch")
        private String cusBatch;
        @JsonProperty("goodsNumber")
        private String goodsNumber;
        @JsonProperty("errMsg")
        private String errMsg;
        @JsonProperty("checkCode")
        private String checkCode;
        @JsonProperty("proIcon")
        private String proIcon;
        @JsonProperty("fileIcon")
        private String fileIcon;
        @JsonProperty("fbaIcon")
        private String fbaIcon;
        @JsonProperty("icsmIcon")
        private String icsmIcon;
        @JsonProperty("destGisDeptCode")
        private String destGisDeptCode;
        @JsonProperty("newIcon")
        private Object newIcon;
    }

    @NoArgsConstructor
    @Data
    public static class RouteLabelInfoDTO implements Serializable {
        @JsonProperty("code")
        private String code;
        @JsonProperty("routeLabelData")
        private RouteLabelDataDTO routeLabelData;
        @JsonProperty("message")
        private String message;
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
