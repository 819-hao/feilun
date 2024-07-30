package com.seeease.flywheel.goods.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2023/11/20
 */
@Data
public class StockGuaranteeCardManageEditRequest implements Serializable {

    private Integer id;

    /**
     * 保卡信息
     */
    private String cardInfo;
    /**
     * 成本
     */
    private BigDecimal cost;
    /**
     * 使用场景
     */
    private UseScenario useScenario;
    public enum UseScenario {
        /**
         * 修改保卡
         */
        CARD_INFO,
        /**
         * 修改成本
         */
        COST,
    }
}
