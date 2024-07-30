package com.seeease.flywheel.purchase.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Mr. Du
 * @Description 采购导入模板
 * @Date create in 2023/3/31 09:43
 */
@Data
@Builder
@NoArgsConstructor
public class PurchaseStockQueryImportRequest extends ImportRequest<PurchaseStockQueryImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        /**
         *  物鱼供货价
         */
        @ExcelReaderProperty(name = "物鱼供货价")
        private BigDecimal wuyuPrice;


        @ExcelReaderProperty(name = "品牌")
        private String brandName;

        @ExcelReaderProperty(name = "系列")
        private String seriesName;

        @ExcelReaderProperty(name = "型号")
        private String model;

        @ExcelReaderProperty(name = "成色")
        private String finess;

        @ExcelReaderProperty(name = "表身号")
        private String stockSn;

        @ExcelReaderProperty(name = "采购价")
        private BigDecimal purchasePrice;

        /**
         * 附件信息开始
         */
        @ExcelReaderProperty(name = "盒子", readConverterExp = "1=是,0=否")
        private String box;

        @ExcelReaderProperty(name = "延保", readConverterExp = "1=是,0=否")
        private String warranty;

        @ExcelReaderProperty(name = "说明书", readConverterExp = "1=是,0=否")
        private String book;

        @ExcelReaderProperty(name = "发票", readConverterExp = "1=是,0=否")
        private String invoice;

        @ExcelReaderProperty(name = "保卡", readConverterExp = "1=是,0=否,2=空白保卡")
        private String card;

        @ExcelReaderProperty(name = "保卡年月")
        private Date warrantyDate;

        @ExcelReaderProperty(name = "中检", readConverterExp = "1=是,0=否")
        private String zCheck;

        @ExcelReaderProperty(name = "国检", readConverterExp = "1=是,0=否")
        private String gCheck;

        @ExcelReaderProperty(name = "徐步天", readConverterExp = "1=是,0=否")
        private String xCheck;

        @ExcelReaderProperty(name = "其他鉴定", readConverterExp = "1=是,0=否")
        private String oCheck;

        @ExcelReaderProperty(name = "钻卡", readConverterExp = "1=是,0=否")
        private String drillCard;

        @ExcelReaderProperty(name = "肩带", readConverterExp = "1=是,0=否")
        private String shoulderStrap;

        @ExcelReaderProperty(name = "防尘袋", readConverterExp = "1=是,0=否")
        private String dustCoverBag;

        @ExcelReaderProperty(name = "购物凭证", readConverterExp = "1=是,0=否")
        private String purchaseVoucher;

        @ExcelReaderProperty(name = "宝石证书", readConverterExp = "1=是,0=否")
        private String jewelCertificate;

        @ExcelReaderProperty(name = "全膜", readConverterExp = "1=是,0=否")
        private String holomembrane;

        @ExcelReaderProperty(name = "背膜", readConverterExp = "1=是,0=否")
        private String notacoria;

        private String singleStock = "0";

        /**
         * 附件信息开始
         */


//        @ExcelReaderProperty(name = "销售优先等级", readConverterExp = "1=仅B端销售,0=B/C可同销,2=仅C端销售")
//        private String salesPriorityName;
//
//        @ExcelReaderProperty(name = "分级")
//        private String goodsLevel;

        @ExcelReaderProperty(name = "备注")
        private String remarks;

        @ExcelReaderProperty(name = "表带类型")
        /**
         * 表带类型
         */
        private String strapMaterial;

        @ExcelReaderProperty(name = "无附件", readConverterExp = "1=有,0=无")
        private String attachment;
    }
}
