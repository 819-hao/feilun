package com.seeease.flywheel.web.common.work.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程任务定义id枚举（流程任务定义key）
 *
 * @author Tiro
 * @date 2023/2/3
 */
@Getter
@AllArgsConstructor
public enum TaskDefinitionKeyEnum {
    UPLOAD_EXPRESS_NUMBER("uploadExpressNumber", "上传快递单号"),
    UPLOAD_TO_C_RETURN_EXPRESS_NUMBER("uploadToCReturnExpressNumber", "销售退货上传快递单号"),
    UPLOAD_TO_B_RETURN_EXPRESS_NUMBER("uploadToBReturnExpressNumber", "销售退货上传快递单号"),
    LOGISTICS_RECEIVING("logisticsReceiving", "物流收货"),
    LOGISTICS_DELIVERY("logisticsDelivery", "物流发货"),
    QT_DETERMINE("qtDetermine", "质检判定"),
    ACCEPT_REPAIR("acceptRepair", "客户接受维修"),
    QT_WAIT_DELIVER("qtWaitDeliver ", "质检转交"),
    REPAIR_RESULT_DETERMINE("repairResultDetermine", "维修结果判定"),
    IN_STORAGE("inStorage", "仓管入库"),
    OUT_STORAGE("outStorage", "仓管出库"),
    REPAIR_RECEIVING("repairReceiving", "维修员接修"),
    REPAIR_COMPLETED("repairCompleted", "维修完成"),
    WAIT_PRICING("waitPricing", "定价完成"),
    WAIT_CHECK("waitCheck", "审核完成"),
    SHOP_RECEIVING("shopReceiving", "门店收货"),
    SHOP_DELIVERY("shopDelivery", "门店发货"),
    CONFIRM_RETURN("confirmReturn", "确认归还用户"),
    SALE_CONFIRM("saleConfirm", "销售确认"),
    STAY_OR_NOT("stayOrNot", "留不留"),
    REPAIR_OR_NOT("repairOrNot", "修不修"),
    RETURN_RECEIVE("returnReceive", "退回收货"),

    PURCHASE_CREATE("purchaseCreate", "客户建单"),

    FIRST_OFFER("firstOffer", "首次报价"),
    FIRST_CLIENT_VERIFY("firstClientVerify", "客户确认"),
    SECOND_OFFER("secondOffer", "二次报价"),
    SECOND_CLIENT_VERIFY("secondClientVerify", "二次确认"),

    PURCHASE_CHECK("purchaseCheck", "采购审核"),
    PURCHASE_CREATE2("demandPurchaseCreate", "采购建单"),
    FINANCIAL_PAYMENT("financialPayment", "已付款"),

    FIX_WAIT("fixWait", "维修等待"),
    TASK_ALLOT("taskAllot", "维修分配"),
    TASK_DECISION("taskDecision", "送外维修判断"),


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
