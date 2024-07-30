package com.seeease.flywheel.serve.base;

import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/13 14:32
 */
@Data
@Builder
public class PurchaseLineNotice implements Serializable {

//    private List<Integer> stockIdList;

    private String serialNo;

    private Integer purchaseId;

    private PurchaseLineStateEnum lineState;

    private Integer stockId;

    /**
     * 实际维修费用
     */
    private BigDecimal fixPrice;

    /**
     * 预计维修费用
     */
    private BigDecimal planFixPrice;

    /**
     * 是否计算寄售价
     */
    private WhetherEnum computeConsignmentPrice;

    /**
     * 是否计算回购价
     */
    private WhetherEnum computeBuyBackPrice;

    /**
     * 是否可以结算
     * 1 可以结算
     * 0 不可以结算
     * null 已结算
     */
    private WhetherEnum isSettlement;
}
