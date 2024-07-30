package com.seeease.flywheel.web.infrastructure.k3cloud;

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
 * @Date create in 2023/8/3 10:38
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class K3CloudGiVoucherRequest implements Serializable {
    @JsonProperty("FormId")
    private String formId;
    @JsonProperty("FieldKeys")
    private String fieldKeys;
    @JsonProperty("FilterString")
    private List<FilterStringDTO> filterString;
    @JsonProperty("OrderString")
    private String orderString;
    @JsonProperty("TopRowCount")
    private Integer topRowCount;
    @JsonProperty("StartRow")
    private Integer startRow;
    @JsonProperty("Limit")
    private Integer limit;
    @JsonProperty("SubSystemId")
    private String subSystemId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilterStringDTO implements Serializable {
        @JsonProperty("Left")
        private String left;
        @JsonProperty("FieldName")
        private String fieldName;
        @JsonProperty("Compare")
        private String compare;
        @JsonProperty("Value")
        private String value;
        @JsonProperty("Right")
        private String right;
        @JsonProperty("Logic")
        private String logic;
    }
}
