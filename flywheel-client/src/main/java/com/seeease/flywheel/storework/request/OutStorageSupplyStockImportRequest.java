package com.seeease.flywheel.storework.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/3/30
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutStorageSupplyStockImportRequest extends ImportRequest<OutStorageSupplyStockImportRequest.ImportDto> {

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 场景
     */
    private StoreWorkOutStorageSupplyStockRequest.SupplyScenario useScenario;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {
        /**
         * 表身号
         */
        @ExcelReaderProperty(name = "表身号")
        private String stockSn;
    }
}
