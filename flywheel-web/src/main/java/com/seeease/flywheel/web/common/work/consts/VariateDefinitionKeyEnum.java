package com.seeease.flywheel.web.common.work.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author Mr. Du
 * @Description 变量的枚举
 * @Date create in 2023/3/2 19:39
 */
@Getter
@AllArgsConstructor
public enum VariateDefinitionKeyEnum {

    STORE_WORK_SERIAL_NO_LIST("storeWorkSerialNoList", "拆单业务key集合"),
    STORE_WORK_SERIAL_NO("storeWorkSerialNo", "拆单业务key"),
    LOGISTICS_REJECT_STATE("logisticsRejectState", "收货&拒收"),
    //    ACCEPT_STATE("acceptState", "转交状态"),
    //todo
    DELIVER_TO("deliverTo", "转交状态"),
    QT_STATE("qtState", "质检判断状态"),

    FIX_ON_QT("fixOnQt", "维修后质检"),
    SHORT_CODES("shortcodes", "门店简码"),
    ORDER_CREATE_CODES("createShortcodes", "下单门店简码"),

    OUT_STORAGE_NEED_QT("needQt", "需要质检"),

    FROM_SHORTCODES("fromShopShortcodes", "发货方门店简码"),
    TO_SHORTCODES("toShopShortcodes", "收货方门店简码"),

    FIX_SERIAL_NO("fixSerialNo", "维修单"),

    APPLY_PURCHASE_SERIAL_NO("applyPurchaseSerialNo", "采购需求单"),

    APPLY_PURCHASE_OWNER("applyPurchaseOwner", "采购需求确认人"),

    ALLOCATE_WORK_SERIAL_JSON_LIST("workSerialJsonList", "调拨作业集合"),

    CK_SERIAL_NO("ckSerialNo", "出库单号"),
    RK_SERIAL_NO("rkSerialNo", "入库单号"),

    SALE_WORK_SERIAL_NO_LIST("workSerialNoList", "发货作业单集合"),

    LOCATION_ID("locationId", "位置信息"),

    CHECK_STATE("checkState", "定价审核"),

    OWNER("owner", "任务归属人"),

    SALE_CONFIRM("saleConfirm", "销售需要确认"),

    PRICING_AUTO("pricingAuto", "是否自动定价"),

    VERIFY("verify", "确认"),
    BALANCE("balance", "差额"),
    /**
     * 专属顾问
     */
    COUNSELOR_USER("counselorUser", "专属顾问"),

    PURCHASE_JOIN("purchaseJoin", "对接采购人"),

    CHECK("check", "需求审核"),

    LINE("isLine", "线上退回"),

    /**
     * 是否接修
     */
    IS_REPAIR("isRepair", "是否接修"),

    /**
     * 是否分配
     */
    IS_ALLOT("isAllot", "是否分配"),

    /**
     * 是否接受 1
     */
    IS_ACCEPT("isAccept", "是否接受"),

    /**
     * 在哪维修 0 本店 1 体内 2 体内
     */
    IS_LOCAL("isLocal", "在哪维修"),

    ;
    /**
     * 流程中任务定义的id
     */
    private String key;
    /**
     * 流程中任务名称
     */
    private String name;
}
