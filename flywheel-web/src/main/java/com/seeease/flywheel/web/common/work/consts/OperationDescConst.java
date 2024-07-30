package com.seeease.flywheel.web.common.work.consts;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/4/1 10:37
 */

public interface OperationDescConst {

    String PURCHASE_CREATE = "新建采购单";

    String CUSTOMER_DELIVERY = "客户已发货";

    String LOGISTICS_RECEIVING = "物流收货_%s";

    String LOGISTICS_DELIVERY = "物流发货";

    String SHOP_LOGISTICS_RECEIVING = "门店收货_%s";

    String SHOP_LOGISTICS_DELIVERY = "门店发货";

    String LOGISTICS_RECEIVING_NO = "接收";

    int LOGISTICS_RECEIVING_NO_VALUE = 0;

    String LOGISTICS_RECEIVING_YES = "拒收";

    int LOGISTICS_RECEIVING_YES_VALUE = 1;

    String QUALITY_TESTING = "质检判定_%s";

    String LOGISTICS_RECEIVING_PASS = "通过";

    int LOGISTICS_RECEIVING_PASS_VALUE = 1;

    String LOGISTICS_RECEIVING_ERROR = "异常";

    int LOGISTICS_RECEIVING_ERROR_VALUE = 2;

    String LOGISTICS_RECEIVING_RETURN = "需退回";

    int LOGISTICS_RECEIVING_RETURN_VALUE = 3;

    String LOGISTICS_RECEIVING_FIX = "需维修";

    int LOGISTICS_RECEIVING_FIX_VALUE = 4;

    String LOGISTICS_RECEIVING_CONFIRM = "待客户确认";

    int LOGISTICS_RECEIVING_CONFIRM_VALUE = 5;

    int LOGISTICS_RECEIVING_CONFIRM_RETURN_VALUE = 6;

    int LOGISTICS_RECEIVING_CONFIRM_FIX_VALUE = 7;

    String QT_CHANGE = "质检转交";

    String FIX_RECEIVE = "维修收货";

    String FIX_FINISH = "维修完成";

    String IN_STORAGE = "仓库入库";

    String OUT_STORAGE = "仓库出库";

    String PRICING = "提交定价审核";

    String PRICING_CHECK = "定价审核_%s";

    String PRICING_CHECK_PASS = "通过";

    int PRICING_CHECK_PASS_VALUE = 1;

    String PRICING_CHECK_ERROR = "驳回";

    int PRICING_CHECK_ERROR_VALUE = 0;

    String ACCEPT_REPAIR = "客户确认_%s";

    String DEMAND_REPAIR = "需求方确认_%s";

    String ACCEPT_REPAIR_FIX = "去维修";

    int ACCEPT_REPAIR_FIX_VALUE = 1;

    String ACCEPT_REPAIR_RETURN = "去退货";

    int ACCEPT_REPAIR_RETURN_VALUE = 0;

    //5.25
    String ACCEPT_REPAIR_IN = "去入库";

    int ACCEPT_REPAIR_IN_VALUE = 3;

    String SHOP_RECEIVING = "退还已收";

    String CONFIRM_RETURN = "归还用户";

    String PRICING_CREATE = "%s定价单";

    String NEW = "新建";

    String NEW_AUTO = "自动新建";

    String RESTART = "重启";

    String ALLOCATE_CREATE = "新建调拨单";

    String PURCHASE_RETURN_CREATE = "新建采购退货单";

    String SALE_CREATE = "新建销售单";

    String SALE_CONFIRM = "确认销售单";

    String SALE_RETURN_CREATE = "新建%s销售退货单";

    String SALE_RETURN_CREATE_TH = "同行";

    String SALE_RETURN_CREATE_GR = "个人";

    String ALLOCATE_CANCEL = "取消调拨单";

    String PRICING_CANCEL = "取消定价单";

    String PURCHASE_CANCEL = "取消采购单";

    String PURCHASE_RETURN_CANCEL = "取消采购退货单";

    String SALE_CANCEL = "取消销售单";

    String SALE_RETURN_CANCEL = "取消%s销售退货单";

    String SALE_UPLOAD_EXPRESS = "%s销售退货上传快递单号";

    String NORMAL = "转正常";

    String FIX = "走维修";

    String EXCEPTION_STOCK_HANDLE = "商品异常处理_%s";

    String CONSIGNMENT_CONFIRMED_SALE = "寄售确认售出";

    String FIX_ALLOT = "维修分配";
    String FIX_CHECK = "维修分配";

    String FIX_CREATE = "新建维修单";
}
