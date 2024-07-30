package com.seeease.flywheel.express.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/8/30 19:32
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KsExpressPrintResult implements Serializable {

    @JsonProperty("cmd")
    private String cmd;
    @JsonProperty("requestID")
    private String requestID;
    @JsonProperty("version")
    private String version;
    @JsonProperty("task")
    private TaskDTO task;

    /**
     * @Author Mr. Du
     * @Description
     * @Date create in 2023/8/30 19:33
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskDTO implements Serializable {
        @JsonProperty("taskID")
        private String taskID;
        @JsonProperty("preview")
        private Boolean preview;
        @JsonProperty("printer")
        private String printer;
        @JsonProperty("documents")
        private List<DocumentsDTO> documents;

        @JsonProperty("firstDocumentNumber")
        private Integer firstDocumentNumber;
        @JsonProperty("totalDocumentCount")
        private Integer totalDocumentCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentsDTO implements Serializable {
        @JsonProperty("documentID")
        private String documentID;
        @JsonProperty("waybillCode")
        private String waybillCode;
        @JsonProperty("ksOrderFlag")
        private Boolean ksOrderFlag;
        //ContentsDTO DataDTO
        @JsonProperty("contents")
        private List<Object> contents;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentsDTO implements Serializable {
        @JsonProperty("templateURL")
        private String templateURL;
        @JsonProperty("key")
        private String key;
        @JsonProperty("ver")
        private String ver;

        @JsonProperty("signature")
        private String signature;
        @JsonProperty("encryptedData")
        private String encryptedData;
        @JsonProperty("addData")
        private Map addData;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataDTO implements Serializable {
        @JsonProperty("templateURL")
        private String templateURL;
        @JsonProperty("data")
        private Map<String, String> data;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address implements Serializable {

        @JsonProperty("countryCode")
        private String countryCode;
        @JsonProperty("provinceName")
        private String provinceName;
        @JsonProperty("cityName")
        private String cityName;
        @JsonProperty("districtName")
        private String districtName;
        @JsonProperty("streetName")
        private String streetName;
        @JsonProperty("detailAddress")
        private String detailAddress;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Contact implements Serializable {

        @JsonProperty("name")
        private String name;
        @JsonProperty("phone")
        private String phone;
        @JsonProperty("mobile")
        private String mobile;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SenderInfo implements Serializable {

        @JsonProperty("address")
        private Address address;
        @JsonProperty("contact")
        private Contact contact;
    }
}
