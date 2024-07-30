package com.seeease.flywheel.web.common.work.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 流程定义id枚举（流程定义key）
 *
 * @author Tiro
 * @date 2023/3/1
 */
@Getter
@AllArgsConstructor
public enum ProcessDefinitionKeyEnum {

    /**
     * 采购
     */
    PURCHASE("purchase", "同行采购流程"),
    RECYCLE("recycle", "个人回收流程"),
    PERSONAL_CONSIGN_SALE("personalConsignSale", "个人寄售流程"),
    PERSONAL_BUY_BACK("personalBuyBack", "个人回购流程"),

    PURCHASE_RETURN("purchaseReturn", "采购退货"),
    STORE_PURCHASE_RETURN("shopPurchaseReturn", "门店采购退货"),

    /**
     * 调拨
     */
    HQ_ALLOCATION("hqAllocation", "总部调拨"),
    SHOP_ALLOCATION_TO_SHOP("shopAllocationToShop", "门店调拨"),
    SHOP_ALLOCATION_TO_HQ("shopAllocationToHQ", "门店调回总部"),


    /**
     * 销售
     */
    TO_C_SALE("toCSale", "个人销售"),
    TO_C_SALE_ON_LINE("toCSaleOnLine", "线上销售"),
    TO_B_SALE("toBSale", "同行销售"),
    TO_C_SALE_RETURN("toCSaleReturn", "个人销售退货"),
    TO_C_SALE_RETURN_ON_LINE("toCSaleReturnOnLine", "线上销售退货"),
    TO_B_SALE_RETURN("toBSaleReturn", "同行销售退货"),

    PRICING("pricing", "定价流程"),
    EXCEPTION_STOCK_HANDLE("exceptionStockHandle", "异常处理流程"),

    SHOP_BUY_BACK("shopBuyback", "商城回购"),
    SHOP_RECYCLE("shopRecycle", "商城回收"),

    PURCHASE_TASK("purchaseTask", "采购需求流程"),

    FIX_TASK("fixFlow", "维修流程"),
    ;
    /**
     * 流程定义的id
     */
    private String key;
    /**
     * 流程定义名称
     */
    private String name;

    /**
     * @param key
     * @return
     */
    public static ProcessDefinitionKeyEnum fromKey(String key) {
        return Arrays.stream(ProcessDefinitionKeyEnum.values())
                .filter(t -> key.equals(t.getKey()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("流程不存在"));
    }
}
