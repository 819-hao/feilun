package com.seeease.flywheel.serve.financial.entity.kingDee;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/1/7 14:15
 */

@NoArgsConstructor
@Data
public class FinancialDocumentsJDDto {
    @JsonProperty("NeedUpDateFields")
    private List<?> NeedUpDateFields = Collections.emptyList();
    @JsonProperty("NeedReturnFields")
    private List<?> NeedReturnFields = Collections.emptyList();
    @JsonProperty("IsDeleteEntry")
    private String IsDeleteEntry = "true";
    @JsonProperty("SubSystemId")
    private String SubSystemId = StrUtil.EMPTY;
    @JsonProperty("IsVerifyBaseDataField")
    private String IsVerifyBaseDataField = "false";
    @JsonProperty("IsEntryBatchFill")
    private String IsEntryBatchFill = "true";
    @JsonProperty("ValidateFlag")
    private String ValidateFlag = "true";
    @JsonProperty("NumberSearch")
    private String NumberSearch = "true";
    @JsonProperty("IsAutoAdjustField")
    private String IsAutoAdjustField = "false";
    @JsonProperty("InterationFlags")
    private String InterationFlags = StrUtil.EMPTY;
    @JsonProperty("IgnoreInterationFlag")
    private String IgnoreInterationFlag = StrUtil.EMPTY;
    @JsonProperty("IsControlPrecision")
    private String IsControlPrecision = "false";
    @JsonProperty("ValidateRepeatJson")
    private String ValidateRepeatJson = "false";
    @JsonProperty("Model")
    private ModelDTO Model;

    @NoArgsConstructor
    @Data
    public static class ModelDTO {
        @JsonProperty("FID")
        private Integer FID = 0;
        @JsonProperty("FBillTypeID")
        private FBillTypeIDDTO FBillTypeID;
        @JsonProperty("FBillNo")
        private String FBillNo = StrUtil.EMPTY;
        @JsonProperty("FDATE")
        private String FDATE;
        @JsonProperty("FENDDATE_H")
        private String FENDDATE_H;
        @JsonProperty("FISINIT")
        private String FISINIT;
        @JsonProperty("FCONTACTUNITTYPE")
        private String FCONTACTUNITTYPE;
        @JsonProperty("FCONTACTUNIT")
        private FCONTACTUNITDTO FCONTACTUNIT;
        @JsonProperty("F_TEZV_Base")
        private F_TEZV_Base F_TEZV_Base;
        @JsonProperty("FAMOUNTFOR")
        private Integer FAMOUNTFOR;
        @JsonProperty("FCURRENCYID")
        private FCURRENCYIDDTO FCURRENCYID;
        @JsonProperty("FDEPARTMENTID")
        private FDEPARTMENTIDDTO FDEPARTMENTID;
        @JsonProperty("FSETTLEORGID")
        private FSETTLEORGIDDTO FSETTLEORGID;
        @JsonProperty("FPAYORGID")
        private FPAYORGIDDTO FPAYORGID;
        @JsonProperty("F_TEZV_Assistant")
        private F_TEZV_Assistant F_TEZV_Assistant;
        @JsonProperty("F_TEZV_Text")
        private String F_TEZV_Text;
        @JsonProperty("FSALEDEPTID")
        private FSALEDEPTIDDTO FSALEDEPTID;
        @JsonProperty("FSALEORGID")
        private FSALEORGIDDTO FSALEORGID;
        @JsonProperty("FSALEGROUPID")
        private FSALEGROUPIDDTO FSALEGROUPID;
        @JsonProperty("FSALEERID")
        private FSALEERIDDTO FSALEERID;
        @JsonProperty("FACCNTTIMEJUDGETIME")
        private String FACCNTTIMEJUDGETIME;
        @JsonProperty("FSettleTypeID")
        private FSettleTypeIDDTO FSettleTypeID;
        @JsonProperty("FMAINBOOKSTDCURRID")
        private FMAINBOOKSTDCURRIDDTO FMAINBOOKSTDCURRID;
        @JsonProperty("FEXCHANGETYPE")
        private FEXCHANGETYPEDTO FEXCHANGETYPE;
        @JsonProperty("FExchangeRate")
        private Integer FExchangeRate;
        @JsonProperty("FNOTAXAMOUNT")
        private Integer FNOTAXAMOUNT;
        @JsonProperty("FTAXAMOUNT")
        private Integer FTAXAMOUNT;
        @JsonProperty("FCancelStatus")
        private String FCancelStatus = StrUtil.EMPTY;
        @JsonProperty("FAR_OtherRemarks")
        private String FAR_OtherRemarks;
        @JsonProperty("FScanPoint")
        private FScanPointDTO FScanPoint;
        @JsonProperty("FPRESETBASE1")
        private FPRESETBASE1DTO FPRESETBASE1;
        @JsonProperty("FPRESETBASE2")
        private FPRESETBASE2DTO FPRESETBASE2;
        @JsonProperty("FPRESETASSISTANT1")
        private FPRESETASSISTANT1DTO FPRESETASSISTANT1;
        @JsonProperty("FPRESETASSISTANT2")
        private FPRESETASSISTANT2DTO FPRESETASSISTANT2;
        @JsonProperty("FPRESETTEXT1")
        private String FPRESETTEXT1;
        @JsonProperty("FPRESETTEXT2")
        private String FPRESETTEXT2;
        @JsonProperty("FEntity")
        private List<FEntityDTO> FEntity;

        @NoArgsConstructor
        @Data
        public static class FBillTypeIDDTO {
            @JsonProperty("FNUMBER")
            private String FNUMBER = StrUtil.EMPTY;
        }

        @NoArgsConstructor
        @Data
        public static class FCONTACTUNITDTO {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class FCURRENCYIDDTO {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class FDEPARTMENTIDDTO {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class FSETTLEORGIDDTO {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class FPAYORGIDDTO {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class F_TEZV_Assistant {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class F_TEZV_Base {
            @JsonProperty("FSTAFFNUMBER")
            private String FSTAFFNUMBER;
        }

        @NoArgsConstructor
        @Data
        public static class FSALEDEPTIDDTO {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class FSALEORGIDDTO {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class FSALEGROUPIDDTO {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class FSALEERIDDTO {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class FSettleTypeIDDTO {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class FMAINBOOKSTDCURRIDDTO {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class FEXCHANGETYPEDTO {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class FScanPointDTO {
            @JsonProperty("FNUMBER")
            private String FNUMBER;
        }

        @NoArgsConstructor
        @Data
        public static class FPRESETBASE1DTO {
            @JsonProperty("FNUMBER")
            private String FNUMBER;
        }

        @NoArgsConstructor
        @Data
        public static class FPRESETBASE2DTO {
            @JsonProperty("FNUMBER")
            private String FNUMBER;
        }

        @NoArgsConstructor
        @Data
        public static class FPRESETASSISTANT1DTO {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class FPRESETASSISTANT2DTO {
            @JsonProperty("FNumber")
            private String FNumber;
        }

        @NoArgsConstructor
        @Data
        public static class FEntityDTO {
            @JsonProperty("FEntryID")
            private Integer FEntryID;
            @JsonProperty("FCOSTID")
            private FCOSTIDDTO FCOSTID;
            @JsonProperty("FCOSTDEPARTMENTID")
            private FCOSTDEPARTMENTIDDTO FCOSTDEPARTMENTID;
            @JsonProperty("FINVOICETYPE")
            private String FINVOICETYPE;
            @JsonProperty("FNOTAXAMOUNTFOR")
            private String FNOTAXAMOUNTFOR;

            @JsonProperty("F_TEZV_Amount")
            private String F_TEZV_Amount;
            @JsonProperty("F_TEZV_Amount1")
            private String F_TEZV_Amount1;
            @JsonProperty("F_TEZV_Text1")
            private String F_TEZV_Text1;
            @JsonProperty("F_TEZV_Text2")
            private String F_TEZV_Text2;
            @JsonProperty("F_TEZV_Text3")
            private String F_TEZV_Text3;

            //总成本 todo F_TEZV_Amount
            //品牌 todo F_TEZV_Text1
            //型号 todo F_TEZV_Text2
            //表身号 todo F_TEZV_Text3
            //物料编码 todo F_TEZV_Base1
            @JsonProperty("F_TEZV_Base1")
            private F_TEZV_Base1 F_TEZV_Base1;
            //数量 todo F_TEZV_Decimal
            @JsonProperty("F_TEZV_Decimal")
            private Integer F_TEZV_Decimal;

            @JsonProperty("FTAXAMOUNTFOR")
            private Integer FTAXAMOUNTFOR;
            @JsonProperty("FAMOUNTFOR_D")
            private Integer FAMOUNTFOR_D;
            @JsonProperty("FNOTAXAMOUNT_D")
            private Integer FNOTAXAMOUNT_D;
            @JsonProperty("FTAXAMOUNT_D")
            private Integer FTAXAMOUNT_D;
            @JsonProperty("FCOMMENT")
            private String FCOMMENT;
            @JsonProperty("FSourceBillNo")
            private String FSourceBillNo;
            @JsonProperty("FIVAmountFor")
            private Integer FIVAmountFor;
            @JsonProperty("FCREATEINVOICE")
            private String FCREATEINVOICE;
            @JsonProperty("FEntryTaxRate")
            private Integer FEntryTaxRate;

            @NoArgsConstructor
            @Data
            public static class FCOSTIDDTO {
                @JsonProperty("FNumber")
                private String FNumber;
            }

            @NoArgsConstructor
            @Data
            public static class FCOSTDEPARTMENTIDDTO {
                @JsonProperty("FNumber")
                private String FNumber;
            }

            @NoArgsConstructor
            @Data
            public static class F_TEZV_Base1 {
                @JsonProperty("FNumber")
                private String FNumber;
            }
        }
    }
}
