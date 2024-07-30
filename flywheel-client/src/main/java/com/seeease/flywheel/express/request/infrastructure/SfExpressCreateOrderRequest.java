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
 * @Date create in 2023/6/26 15:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SfExpressCreateOrderRequest extends BaseSfExpressRequest implements Serializable {

    @JsonProperty("language")
    private String language;
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("customsInfo")
    private CustomsInfoDTO customsInfo;
    @JsonProperty("cargoDetails")
    private List<CargoDetailsDTO> cargoDetails;
    @JsonProperty("cargoDesc")
    private String cargoDesc;
    @JsonProperty("extraInfoList")
    private List<ExtraInfoListDTO> extraInfoList;
    @JsonProperty("serviceList")
    private List<ServiceListDTO> serviceList;
    @JsonProperty("contactInfoList")
    private List<ContactInfoListDTO> contactInfoList;
    @JsonProperty("monthlyCard")
    private String monthlyCard;
    @JsonProperty("payMethod")
    private Integer payMethod;
    @JsonProperty("expressTypeId")
    private Integer expressTypeId;
    @JsonProperty("parcelQty")
    private Integer parcelQty;
    @JsonProperty("totalLength")
    private Double totalLength;
    @JsonProperty("totalWidth")
    private Double totalWidth;
    @JsonProperty("totalHeight")
    private Double totalHeight;
    @JsonProperty("volume")
    private Double volume;
    @JsonProperty("totalWeight")
    private Double totalWeight;
    @JsonProperty("totalNetWeight")
    private String totalNetWeight;
    @JsonProperty("sendStartTm")
    private String sendStartTm;
    @JsonProperty("isDocall")
    private Integer isDocall;
    @JsonProperty("isSignBack")
    private Integer isSignBack;
    @JsonProperty("isOneselfPickup")
    private Integer isOneselfPickup;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomsInfoDTO implements Serializable {
        @JsonProperty("declaredValue")
        private Double declaredValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CargoDetailsDTO implements Serializable {
        @JsonProperty("amount")
        private Double amount;
        @JsonProperty("count")
        private Double count;
        @JsonProperty("currency")
        private String currency;
        @JsonProperty("goodPrepardNo")
        private String goodPrepardNo;
        @JsonProperty("hsCode")
        private String hsCode;
        @JsonProperty("name")
        private String name;
        @JsonProperty("productRecordNo")
        private String productRecordNo;
        @JsonProperty("sourceArea")
        private String sourceArea;
        @JsonProperty("taxNo")
        private String taxNo;
        @JsonProperty("unit")
        private String unit;
        @JsonProperty("weight")
        private Double weight;
        @JsonProperty("volume")
        private Double volume;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtraInfoListDTO implements Serializable {
        @JsonProperty("attrName")
        private String attrName;
        @JsonProperty("attrVal")
        private String attrVal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceListDTO implements Serializable {
        @JsonProperty("name")
        private String name;
        @JsonProperty("value")
        private String value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInfoListDTO implements Serializable {
        @JsonProperty("address")
        private String address;
        @JsonProperty("city")
        private String city;
        @JsonProperty("contact")
        private String contact;
        @JsonProperty("contactType")
        private Integer contactType;
        @JsonProperty("country")
        private String country;
        @JsonProperty("county")
        private String county;
        @JsonProperty("mobile")
        private String mobile;
        @JsonProperty("postCode")
        private String postCode;
        @JsonProperty("province")
        private String province;
        @JsonProperty("tel")
        private String tel;
        @JsonProperty("company")
        private String company;
    }

}
