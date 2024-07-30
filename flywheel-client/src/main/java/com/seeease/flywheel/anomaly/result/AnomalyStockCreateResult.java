package com.seeease.flywheel.anomaly.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/4/12 11:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyStockCreateResult implements Serializable {

//    private String serialNo;

    private List<AnomalyStockCreateResultDto> list;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnomalyStockCreateResultDto implements Serializable {

        private Integer stockId;

        private String serialNo;

        private String parentSerialNo;
    }

}
