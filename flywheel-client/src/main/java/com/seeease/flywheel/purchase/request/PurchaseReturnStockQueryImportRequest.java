package com.seeease.flywheel.purchase.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.*;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 采购退货
 * @Date create in 2023/3/31 09:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseReturnStockQueryImportRequest extends ImportRequest<PurchaseReturnStockQueryImportRequest.ImportDto> {

    private Integer customerId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "表身号")
        private String stockSn;
    }
}
