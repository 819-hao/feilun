package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.ImportRequest;
import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Tiro
 * @date 2023/3/30
 */

@Data
@Builder
@NoArgsConstructor
public class TxHistoryImportRequest extends ImportRequest<TxHistoryImportRequest.ImportDto> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportDto implements Serializable {

        /**
         *编号
         */
        @ExcelReaderProperty(name = "编号")
        private String serial;
        /**
         *名称
         */
        @ExcelReaderProperty(name = "名称")
        private String name;
        /**
         *纳税人识别号
         */
        @ExcelReaderProperty(name = "纳税人识别号")
        private String tiCode;
        /**
         *地址
         */
        @ExcelReaderProperty(name = "地址")
        private String address;
        /**
         *电话
         */
        @ExcelReaderProperty(name = "电话")
        private String phone;
        /**
         *销售方电话
         */
        @ExcelReaderProperty(name = "销售方电话")
        private String sellerPhone;

        /**
         *开户行'
         */
        @ExcelReaderProperty(name = "开户行")
        private String accountBank;
        /**
         *账号
         */
        @ExcelReaderProperty(name = "账号")
        private String account;
        /**
         *销售方名称
         */
        @ExcelReaderProperty(name = "销售方名称")
        private String sellerName;
        /**
         *身份证号码
         */
        @ExcelReaderProperty(name = "身份证号码")
        private String idCard;
        /**
         *商品编码
         */
        @ExcelReaderProperty(name = "表身号")
        private String sn;
        /**
         *商品名称规格
         */
        @ExcelReaderProperty(name = "品牌型号")
        private String productTitle;
        /**
         *成色
         */
        @ExcelReaderProperty(name = "成色")
        private String fineness;
        /**
         *金额
         */
        @ExcelReaderProperty(name = "金额")
        private String amount;

        /**
         *金额大写
         */
        @ExcelReaderProperty(name = "金额大写")
        private String amountUppercase;
        /**
         *合同编号
         */
        @ExcelReaderProperty(name = "合同编号")
        private String contractCode;
        /**
         *交易日期
         */
        @ExcelReaderProperty(name = "交易日期")
        private Date txTime;
        /**
         *物流公司
         */
        @ExcelReaderProperty(name = "物流公司")
        private String logisticsFirm;
        /**
         *单号
         */
        @ExcelReaderProperty(name = "单号")
        private String code;
        /**
         *支付通道
         */
        @ExcelReaderProperty(name = "支付通道")
        private String payChannel;

        /**
         *收款账号
         */
        @ExcelReaderProperty(name = "收款账号")
        private String receiveAccount;
        /**
         *流水号
         */
        @ExcelReaderProperty(name = "流水号")
        private String txCode;
        /**
         * 备注
         */
        @ExcelReaderProperty(name = "备注")
        private String remark;

        /**
         *采购员
         */
        @ExcelReaderProperty(name = "采购人员")
        private String buyer;
        /**
         *制证人员
         */
        @ExcelReaderProperty(name = "制证人员")
        private String certifier;
        /**
         *制证时间
         */
        @ExcelReaderProperty(name = "制证日期")
        private Date certifierTime;
    }
}
