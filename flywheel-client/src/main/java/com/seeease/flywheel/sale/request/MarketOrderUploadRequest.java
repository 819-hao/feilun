package com.seeease.flywheel.sale.request;

import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/4/13
 */
@Data
public class MarketOrderUploadRequest implements Serializable {

    /**
     * 销售门店id
     */
    private Integer shopId;
    /**
     * 第三方订单号
     */
    private String bizOrderCode;
    /**
     * 第一销售人
     */
    private String firstSalesman;

    /**
     * 第二销售人
     */
    private String secondSalesman;

    /**
     * 第三销售人
     */
    private String thirdSalesman;
    /**
     * 客户名
     */
    private String customerName;
    /**
     * 联系方式
     */
    private String customerPhone;
    /**
     * 联系地址
     */
    private String customerAddress;

    /**
     * 总销售金额
     */
    private BigDecimal totalSalePrice;

    /**
     * 订单商品行
     */
    private List<Line> orderLines;

    @Data
    public static class Line implements Serializable {

        /**
         * 库存id
         */
        private Integer stockId;

        /**
         * 成交价
         */
        private BigDecimal clinchPrice;

        /**
         * 是否回购
         */
        private boolean isCounterPurchase;

        /**
         * 是否收取表带更换费
         */
        private boolean watchStrapFeeChangeFlag;

        /**
         * 回购政策
         */
        private List<BuyBackPolicyInfo> buyBackPolicy;

        /**
         * 回购url
         */
        private String repurchasePolicyUrl;
    }
}
