package com.seeease.flywheel.serve.financial.entity.kingDee;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seeease.flywheel.serve.financial.entity.kingDee.NeedReturnDataDTO;
import com.seeease.flywheel.serve.financial.entity.kingDee.ResponseStatusDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 */

@NoArgsConstructor
@Data
public class ResultDTO {
    @JsonProperty("ResponseStatus")
    private ResponseStatusDTO responseStatus;
    @JsonProperty("Id")
    private Integer id;
    @JsonProperty("Number")
    private String number;
    @JsonProperty("NeedReturnData")
    private List<NeedReturnDataDTO> needReturnData;
}
