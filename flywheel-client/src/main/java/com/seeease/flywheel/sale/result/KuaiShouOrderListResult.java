package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author trio
 * @date 2023/1/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KuaiShouOrderListResult implements Serializable {
    /**
     * id
     */
    private Integer id;
    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;
    /**
     * 单号
     */
    private String serialNo;

    /**
     * 状态
     */
    private Integer orderStatus;
    private String orderStatusDesc;

    /**
     * 是否合单 0否 1是
     */
    private Integer whetherUse;

    /**
     * 联系人
     */
    private String decryptPostReceiver;

    /**
     * 联系人电话
     */
    private String decryptPostTel;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 使用时间
     */
    private String usageTime;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 买家留言
     */
    private String buyerWords;

    private String goodsModel;
}
