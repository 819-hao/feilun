package com.seeease.flywheel.purchase.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2023/9/25
 */
@Data
@Builder
@NoArgsConstructor
public class AttachmentStockImportRequest extends ImportRequest<AttachmentStockImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        @ExcelReaderProperty(name = "供应商")
        private String customerName;

        @ExcelReaderProperty(name = "品牌(一级分类)")
        private String brandName;

        @ExcelReaderProperty(name = "系列(二级分类)")
        private String seriesName;

        @ExcelReaderProperty(name = "型号(商品名称)")
        private String model;

        @ExcelReaderProperty(name = "数量")
        private Integer number;

        @ExcelReaderProperty(name = "采购价")
        private BigDecimal purchasePrice;

        @ExcelReaderProperty(name = "寄售价")
        private BigDecimal consignmentPrice;

        @ExcelReaderProperty(name = "颜色")
        private String colour;

        @ExcelReaderProperty(name = "材质")
        private String material;

        @ExcelReaderProperty(name = "尺寸-长(表带类)")
        private String lengthSize;

        @ExcelReaderProperty(name = "尺寸-宽(表带类)")
        private String widthSize;

        @ExcelReaderProperty(name = "形状-直径(玻璃类)")
        private String diameterSize;

        @ExcelReaderProperty(name = "形状-厚度(玻璃类)")
        private String thicknessSize;

        @ExcelReaderProperty(name = "形状-弧度(玻璃类)")
        private String radianSize;

        @ExcelReaderProperty(name = "机芯号")
        private String movementNo;

        @ExcelReaderProperty(name = "电池型号")
        private String batteryModel;

        @ExcelReaderProperty(name = "腕表型号")
        private String gwModel;

        @ExcelReaderProperty(name = "备注")
        private String remarks;

        /**
         * 客户id
         */
        private Integer customerId;
        /**
         * 联系人id
         */
        private Integer customerContactsId;
        /**
         * 型号id
         */
        private Integer goodsId;
    }
}