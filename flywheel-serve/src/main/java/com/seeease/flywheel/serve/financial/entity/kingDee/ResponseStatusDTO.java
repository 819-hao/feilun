package com.seeease.flywheel.serve.financial.entity.kingDee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 */

@NoArgsConstructor
@Data
public class ResponseStatusDTO {
    @JsonProperty("IsSuccess")
    private Boolean isSuccess;
    @JsonProperty("Errors")
    private List<?> errors;
    @JsonProperty("SuccessEntitys")
    private List<SuccessEntitysDTO> successEntitys;
    @JsonProperty("SuccessMessages")
    private List<?> successMessages;
    @JsonProperty("MsgCode")
    private Integer msgCode;
}
