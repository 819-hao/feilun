package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/5/18 19:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseLineByApplyResult implements Serializable {

    private List<PurchaseLineByApplyResultDto> list;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseLineByApplyResultDto implements Serializable {

        private String serialNo;

        private Integer createId;

    }
}
