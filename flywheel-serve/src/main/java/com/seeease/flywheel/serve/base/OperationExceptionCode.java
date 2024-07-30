package com.seeease.flywheel.serve.base;

import com.seeease.springframework.exception.e.OperationRejectedExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/4 14:37
 */
@Getter
@AllArgsConstructor
public enum OperationExceptionCode implements OperationRejectedExceptionCode {

    QT_BILL_NOT_EXIST("质检单不存在"),
    QT_BILL_NOT_EDIT("质检状态不支持修改"),
    RETURN_BILL_NOT_EDIT("禁止退货"),
    FIX_BILL_NOT_EDIT("禁止维修"),
    ANOMALY_BILL_NOT_EDIT("禁止异常入库"),
    ORDER_INFO_CHANGE("订单信息已发生变化!"),

    PURCHASE_NO_EXIST("采购单不存在"),

    NUMBER_NO_EXIST("数量不匹配"),

    ILLEGAL_PARAMETER("出现非法参数"),

    FINESS_PROHIBIT_MODIFICATION("已售出或寄售商品成色禁止修改"),

    NO_MODIFICATION_ALLOWED("未经允许不能修改"),

    EXISTING_DATA("已经存在数据"),

    STOCK_PARAMETER("表的状态不符合"),

    CUSTOMER_PARAMETER("客户不符合"),

    REFUSE_BILL_PARAMETER("拒绝收货单据类型不符合"),

    APPLY_FINANCIAL_PAYMENT("申请打款单不存在或者未打款或者已使用"),

    APPLY_FINANCIAL_PURCHASE("打款主体&采购主体不一致"),

    APPLY_FINANCIAL_MONEY_PURCHASE("打款价格&采购总价格不一致"),

    APPLY_PURCHASE_MONEY_PURCHASE("非同一门店和采购方式不能采购"),

    SALE_PURCHASE("本次销售单不存在或者未完成"),

    SALE_NOT_CANCEL("销售单已开始不可取消"),

    ORIGIN_SALE_PURCHASE("关联销售单不存在或者未完成"),

    ORIGIN_SALE_STOCK("关联销售单号表不存在或者未完成"),

    REFERENCE_BUY_BACK_PRICE("回购政策不符合要求"),

    BUY_BACK_PRICE("非个人寄售，个人回购不能走这条线"),
    BUY_BACK_EXITS("已经做回购了"),
    BUY_BACK_PRICE_2("个人寄售,回购商品需要等客户确认后才能维修"),
    BUY_BACK_PRICE_3("个人回购商品不能直接判断入库"),
    BUY_BACK_PRICE_4("个人寄售商品维修后不能退货"),
    PRICING_ERROR("定价不符合规范"),
    PURCHASE_RETURN_ERROR("采购退货只能总部建单"),

    PURCHASE_PARAMETER("表身号重复，禁止操作"),
    BUY_BACK_PARAMETER("个人回购或者异常处理商品不能直接判断入库"),

    GOODS_NOT_SALE("商品{}不可售"),
    GOODS_NOT_ALLOCATE("商品{}不可调拨"),
    DOES_NOT_CONFORM_TO_THE_FINANCIAL_SYSTEM_SPECIFICATIONS_OF_KING_DEE("以上订单不符合导入金蝶财务系统规范:{}"),
    KING_DEE_RESULT_WARNING("以上订单导入金蝶财务系统错误订单号:{},以上订单导入金蝶财务系统警告订单号:{}"),
    GOODS_LOCATION_SALE_NOT_UNIQUE("只允许同一位置商品下单"),
    SHOP_CUSTOMER_CONTACTS_ERROR("企业客户联系人不存在"),

    ALLOCATE_BILL_NOT_CANCEL("调拨单不可取消"),
    ZB_NOT_ALLOWED_TO_CREATE_ORDER("总部不允许创建个人销售单"),
    NO_BREAKING_B_PRICE_IS_ALLOWED("不允许低于同行价销售"),
    UNP_FINESS_NEW_NO_BREAKING_PRICE_IS_ALLOWED_C("不允许低于零售价销售"),
    UNP_FINESS_NO_BREAKING_PRICE_IS_ALLOWED_B("无活动价：非99新的商品，不低于B价"),
    FINESS_NEW_NO_BREAKING_PRICE_IS_ALLOWED_P("有活动价：活动类商品不低于活动价"),
    FINESS_NO_BREAKING_PRICE_IS_ALLOWED_CP("有活动价：非99新的商品，不低于活动寄售价"),
    NO_BREAKING_TOTAL_PRICE_IS_ALLOWED("不允许低于成本销售"),
    NO_BREAKING_CONSIGNMENT_PRICE_IS_ALLOWED("破价商品不允许低于寄售价销售"),
    DOU_YIN_ORDER_NUMBER_IS_REQUIRED("抖音销售三方单号必填"),
    ATTACHMENT_EXISTS("附件不能为空"),

    STOCK_PARAMETER_EXISTS("存在相同表身号在库数据，禁止修改"),
    SALE_ORDER_NOT_ALLOWED_TO_CREATE("销售退货单不允许创建"),
    SALE_ORDER_EXISTS_IN_STATE("销售单中有开票中的商品"),

    SALE_ORDER_NOT_TO_CREATE("表带更换必填"),
    BIZ_ORDER_CODE_EXISTS("三方单号已存在,请勿重复录单"),
    ILLEGAL_PARAMETER_CREATE("非建单用户禁止变更"),
    SALE_PARAMETER_CREATE("禁止销售"),

    ILLEGAL_PARAMETER_FINANCE("确认失败"),
    ILLEGAL_PARAMETER_FINANCE_RETURN("退还失败"),
    ILLEGAL_PARAMETER_FINANCE_RETURN_APPLY("需要选择申请打款单"),
    ILLEGAL_FINANCE_RETURN_APPLY("存在财务未确认的采购需求订金单"),

    STRAP_MATERIAL_NON_NULL("表带类型不能为空或者不符合规范"),

    STRAP_PAYMENT_NON_NULL("打款信息不能为空"),
    LOCK_DEMAND_NOT_ALLOWED_TO_SALE("订金商品，其他门店不可售卖！"),
    LOCK_DEMAND_MUST_DEPOSIT("订金商品，销售方式必须是订金销售"),

    PURCHASE_SUBJECT_NON_NULL("采购主体不符合"),
    PRICE_PARAMETER_FINANCE("存在同一型号的新表有不同的c价价格,请修复价格后在定价"),

    PAYMENT_MATERIAL_NON_NULL("申请打款单不能为空或者不符合类型"),
    PAYMENT_MATERIAL_STATE_NOT_ALLOWED("申请打款单状态为已打款或者已取消"),
    PAYMENT_MATERIAL_CREATE_NOT_ALLOWED("申请打款单必须是创建人才能取消"),
    PAYMENT_MATERIAL_SCENARIO_NO_EXIST("申请打款单场景不存在"),

    STOCKTAKING_BILL_NOT_EXIST("盘点单不存在"),

    GOODS_MODEL_NOT_EXIST("商品型号不存在"),
    GOODS_MODEL_REPETITIVE_DATA_ERROR("商品资料存在异常型号:{}"),
    SN_NOT_EXIST("表身号不存在"),
    CONSIGNMENT_PRICE_NOT_EXIST("寄售价大于TOC价"),
    CONSIGNMENT_PRICE_NOT_EXIST_B("寄售价大于TOB价"),
    CONSIGNMENT_PRICE_NOT_0("TOB/TOC/寄售价不能为0"),
    DOU_YIN_ORDER_EXIST_REFUND("当前发货中有抖音用户申请售后的商品，请处理完后再发货！！"),

    RFID_NO_AUTH("rfid无法查看总部权限"),

    TOC_SALE_LOCATION_ID_ERROR("商品位置不同，无法发货"),

    ONLINE_SALE_STOCK_ERROR("二手表必须选择商品才能下单，如果是抖音订单确认抖音商品资料是否有维护商家编码信息"),

    LOGISTICS_DELIVERY_STOCK_ID_NULL("未确认表身号，请按操作规范导入表身号后再发货"),
    WORK_COLLECT_DATA_OPT_ERROR("订单已集单，无法在当前入口发货，请回到集单页面操作"),
    WORK_COLLECT_DATA_CHANGES("集单数据已经发生变化，请刷新后重新操作"),
    MUST_QT_VIDEO("发货绑定质检视频失败,请在质检完成后发货！"),
    SALE_EXIST("销售单不存在"),
    BUY_BACK_RECYCLE_EXIST("订单不存在"),
    MALL_CLIENT_NO_UPLOAD("商城客户未上传打款信息"),
    MALL_CLIENT_CLIENT_PAY("商城客户支付订单不需要打款，差额是要收款"),
    MALL_BUY_BACK_ORDER("商城已经做过回购,不允许重复回购"),
    MALL_BUY_BACK_SALE_CANCEL("回购的表销售单不能取消"),
    MODEL_QUANTITY_INSUFFICIENT("{}库存不足"),
    MENU_MUST_FILL_IN("菜单必填字段不能为空"),


    EXPRESS_PRINT_REFUSE("手动录入的快递单号无法补打快递面单！"),

    PURCHASE_NEW_STOCK("表身号已经在库，不允许换货"),

    ALLOCATE_ERROR_1("借货商品无法进行平调"),


    CUSTOMER_BALANCE_LEFT_ZERO("客户可用余额为0"),
    CUSTOMER_BALANCE_GT_CHICKPRICE("总金额必须小于客户的可用余额"),
    CUSTOMER_MERGIN_LEFT_ERROR("客户寄售保证金不足"),
    CUSTOMER_BALANCE_ERR("客户余额不足"),
    CUSTOMER_MERGIN_LEFT_ZERO("客户寄售保证金为0"),
    CUSTOMER_MERGIN_GT_CHICKPRICE("寄售金额必须小于寄售保证金"),
    CUSTOMER_MERGIN_IDENTITYCARD("寄售必须填写身份证号"),
    FORWARD_ORDERS_HAVE_NOT_BEEN_WRITTEN_OFF("正向订单的应收应付未核销！！！"),
    ACCOUNT_RECEIPT_CONFIRM_STATUS_NOT_WAIT("销售单确认收款单不是待确认状态"),
    WAIT_BINDING_AMOUNT_CANNOT_BE_GREATER_THAN_RECEIVABLE_AMOUNT("绑定流水使用总金额不能大于待核销金额,待绑定金额:{}"),
    CANNOT_BE_GREATER_THAN_RECEIVABLE_AMOUNT("绑定流水使用总金额不能大于流水能使用金额,绑定流水金额:{}"),
    DO_NOT_OPERATE("禁止操作"),


    APPLY_PRICING_SYSTEM_REJECTION("商品已售出，自动驳回"),

    CUSTOMER_NOT_EXIT("企业客户名字未找到"),
    SALE_PURCHASE_NOT_EXIT("销售的采购主体关系没有"),
    FIX_NOT_EXIT("维修单不存在"),

    PURCHASE_RETURN_ING("该表不符合补差业务"),


    ILLEGAL_INVOICE("待红冲为空或不符合红冲条件"),


    STEP1("成色是99、98新破价后不能低于结算价卖"),
    STEP2("未破价前不能低于最新零售价售卖"),
    STEP3("成色是95、90新破价后不能低于结算价卖"),
    STEP4("未破价前不能低于最新结算价售卖"),
    STEP5("时间范围冲突"),
    STEP6("调拨额度超出");


    private String errMsg;
}
