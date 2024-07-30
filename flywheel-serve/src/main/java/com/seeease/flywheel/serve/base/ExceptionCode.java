package com.seeease.flywheel.serve.base;

import com.seeease.springframework.exception.e.BusinessExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tiro
 * @date 2023/1/9
 */
@Getter
@AllArgsConstructor
public enum ExceptionCode implements BusinessExceptionCode {

    OPT_NOT_SUPPORT(-1, "不支持的操作类型"),
    ENUM_TYPE_NOT_SUPPORT(-1, "不支持枚举类型"),
    PURCHASE_RETURN_TYPE_NOT_SUPPORT(-1, "不支持的采购退货类型"),
    PURCHASE_RETURN_RESUBMIT(-1, "重复生成采购退货"),
    PURCHASE_EDIT_FAIL(-1, "采购单编辑失败"),
    PURCHASE_TYPE_NOT_SUPPORT(-1, "不支持的采购类型"),
    OPERATION_DATA_DOES_NOT_MATCH_DATABASE_DATA(-1, "操作数据和数据库数据不匹配"),
    PURCHASE_BILL_NOT_EXIST(-1, "采购单不存在"),
    SALE_ORDER_BILL_NOT_EXIST(-1, "销售单不存在"),
    SALE_RETURN_ORDER_BILL_NOT_EXIST(-1, "销售退货单不存在"),
    PURCHASE_RETURN_NOT_EXIST(-1, "采购退货单不存在"),
    FIX_BILL_NOT_EXIST(-1, "维修单不存在"),
    STORE_WORK_BILL_NOT_EXIST(-1, "入库单不存在"),
    QT_BILL_NOT_EXIST(-1, "质检单不存在"),
    QT_BILL_NOT_EDIT(-1, "质检状态不支持修改"),
    STRATEGY_EXCEPTION(-1, "策略异常"),
    PURCHASE_TYPE_MODE_NOT_SUPPORT(-1, "不支持的采购类型或方式"),
    ALLOCATE_BILL_NOT_EXIST(-1, "调拨单不存在"),
    PRICING_BILL_NOT_EXIST(-1, "定价单不存在"),
    FINANCIAL_DOCUMENTS_NOT_EXIST(-1, "财务单不存在"),
    PRICING_B_NOT_EXIST(-1, "b价不存在"),
    PRICING_C_NOT_EXIST(-1, "c价不存在"),
    PRICING_CONSIGNMENT_NOT_EXIST(-1, "寄售价不存在"),
    SALE_ORDER_TYPE_MODE_NOT_SUPPORT(-1, "不支持的销售类型"),
    SALE_RETURN_ORDER_TYPE_MODE_NOT_SUPPORT(-1, "不支持的销售退货类型"),
    SHOP_WORK_EDIT_FAIL(-1, "门店入库单编辑失败"),
    BILL_EVENT_HANDLER_FAIL(-1, "单据事件处理失败"),
    STOCK_PUT_ON_SHELVES_FAIL(-1, "上架失败"),
    GOODS_NOT_SUPPORT(-1, "商品不存在或不可操作"),
    SERIES_NOT_SUPPORT(-1, "系列不存在或不可操作"),
    GOODS_MODEL_MISMATCHING(-1, "商品与目标型号不匹配"),
    BATCH_AUDIT_FAIL(-1, "仅可选择状态为【待核销、退货待核销】"),
    APPLY_FINANCIAL_PAYMENT_FAIL(-1, "仅可针对应付单生成打款单"),
    ONLY_ONE_SERVICE_MODE_CAN_BE_SELECTED(-1, "只能选择一种业务方式生成打款单"),
    APPLY_FINANCIAL_PAYMENT_EXIST(-1, "应收应付单已经生成过申请打款单"),
    APPLY_FINANCIAL_PAYMENT_EXIST2(-1, "物流订单创建失败"),
    APPLY_FINANCIAL_RECYCLE_EXIST2(-1, "商城订单创建失败"),
    BUYBACK_FINANCIAL_RECYCLE_EXIST2(-1, "订单不存在"),
    MALL_FINANCIAL_RECYCLE_EXIST2(-1, "商城用户未上传打款信息,不允许创建"),
    CUSTOMER_ACCOUNT_BALANCE_LEFT_ERR(-1,"客户余额不足"),
    CUSTOMER_ACCOUNT_CONSIGNMENTMARGIN_LEFT_ERR(-1,"客户保证金余额不足"),
    ACCOUNT_RECE_CONFIRM_AMOUNT_ERR(-1,"流水金额与确认收款金额不等"),
    FINANCIAL_INVOICE_STATE_NOT_ALL_NO_INVOICED(-1,"需要所有的都是未开票状态"),
    FINANCIAL_INVOICE_STATE_IN_INVOICED_NOT_SUPPORT(-1,"需要所有的都是未开票状态"),
    PURCHASE_TASK_BILL_NOT_EXIST(-1, "采购任务单不存在"),
    STOCK_RECE_CONFIRM_AMOUNT_ERR(-1, "表不符合重新定价规则"),

    SERIES_BATCH_DELETE_NOT_SUPPORT(-1, "该系列有绑定商品，禁止删除！"),
    FIX_NOT_EXIT(-1, "维修单不存在"),
    FIX_SITE_TAG_NOT_EXIT(-1, "维修等级不存在"),
    FIX_SITE_NOT_EXIT(-1, "维修站点不存在"),
    FIX_NOT_FOREIGN(-1, "不允许送外"),

    PURCHASE_BILL_TASK_NOT_EXIST(-1, "采购需求单不存在"),

    OPT_TIME_SUPPORT(-1, "2023.9月以前的订单不允许业务开票"),


    ;
    private int errCode;

    private String errMsg;
}
