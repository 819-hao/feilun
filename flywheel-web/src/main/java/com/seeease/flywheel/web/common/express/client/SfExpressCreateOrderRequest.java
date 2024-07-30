package com.seeease.flywheel.web.common.express.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/9/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SfExpressCreateOrderRequest extends SfExpressBaseRequest implements Serializable {
    private String language;
    private String orderId;
    private CustomsInfoDTO customsInfo;
    private List<CargoDetailsDTO> cargoDetails;
    private String cargoDesc;
    private List<ExtraInfoListDTO> extraInfoList;
    private List<ServiceListDTO> serviceList;
    private List<ContactInfoListDTO> contactInfoList;
    private String monthlyCard;
    private Integer payMethod;
    private Integer expressTypeId;
    private Integer parcelQty;
    private Double totalLength;
    private Double totalWidth;
    private Double totalHeight;
    private Double volume;
    private Double totalWeight;
    private String totalNetWeight;
    private String sendStartTm;
    private Integer isDocall;
    private Integer isSignBack;
    private Integer isOneselfPickup;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomsInfoDTO implements Serializable {
        private Double declaredValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CargoDetailsDTO implements Serializable {
        private Double amount;
        private Double count;
        private String currency;
        private String goodPrepardNo;
        private String hsCode;
        private String name;
        private String productRecordNo;
        private String sourceArea;
        private String taxNo;
        private String unit;
        private Double weight;
        private Double volume;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtraInfoListDTO implements Serializable {
        private String attrName;
        private String attrVal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceListDTO implements Serializable {
        private String name;
        private String value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInfoListDTO implements Serializable {
        private String address;
        private String city;
        private String contact;
        private Integer contactType;
        private String country;
        private String county;
        private String mobile;
        private String postCode;
        private String province;
        private String tel;
        private String company;
    }
}