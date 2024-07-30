package com.seeease.flywheel.web.common.context;

import com.seeease.springframework.exception.e.OperationRejectedExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tiro
 * @date 2023/2/1
 */
@Getter
@AllArgsConstructor
public enum OperationExceptionCodeEnum implements OperationRejectedExceptionCode {

    PREVIOUS_STEP_WAIT_COMPLETE("上一步操作进行中或无权限操作，请刷新后重试！"),
    TASK_REPEAT_SUBMISSION("任务处理中～"),
    TASK_BATCH_SIZE("服务资源有限，请将批处理数量控制在{}内！"),
    FINANCIAL_STATEMENT_SERIAL_NO_NON_NULL("财务流水号不能为空"),
    STATEMENT_SERIAL_NO_REPEAT("重复流水号:{}"),
    FINANCIAL_INVOICE_BELONG_NAME_DISAGREE("商品归属不一致"),
    FINANCIAL_STATEMENT_SHOP_NAME_NON_NULL("流水归属不能为空"),
    FINANCIAL_STATEMENT_SUBJECT_NAME_NON_NULL("收款主体不能为空"),
    FINANCIAL_STATEMENT_PAYER_NON_NULL("付款人不能为空"),
    FINANCIAL_STATEMENT_COLLECTION_TIME_NON_NULL("收款时间不能为空"),
    FINANCIAL_STATEMENT_PRICE_NON_NULL("财务流水价格出现空值"),
    STOCK_SN_REQUIRE_NON_NULL("表身号不能为空"),
    STOCK_PROMOTION_PRICE_NON_NULL("活动价格不能为空"),
    STOCK_PROMOTION_RATIO_PRICE_NON_NULL("活动寄售价比例不能为空"),
    CUSTOMER_REQUIRE_NON_NULL("供应商不能为空"),
    BRAND_NOT_ALLOW("不合理的品牌:{}"),
    CUSTOMER_NON_EXISTENT("[{}]供应商不存在"),
    MODEL_NON_EXISTENT("[{}]型号不存在"),
    MODEL_REQUIRE_NON_NULL("型号不能为空"),
    MODEL_CODE_REQUIRE_NON_NULL("型号编码不能为空"),
    SERIES_REQUIRE_NON_NULL("系列不能为空"),
    BRAND_REQUIRE_NON_NULL("品牌不能为空"),
    NUMBER_REQUIRE_NON_NULL("数量不能为空"),
    PURCHASE_PRICE_REQUIRE_NON_NULL("采购价不能为空"),
    CONSIGNMENT_PRICE_REQUIRE_NON_NULL("寄售价不能为空"),
    CONSIGNMENT_PRICE_ILLEGALITY("寄售价不能小于采购价"),
    SIZE_ILLEGAL("尺寸参数异常"),
    SHAPE_ILLEGAL("形状参数异常"),
    SIZE_GROUP_ILLEGAL("参数异常，（尺寸/形状/机芯号/电池型号）四选一不用同时存在"),
    SHELVES_SIMPLIFIED_CODE_REQUIRE_NON_NULL("货位流转码不能为空"),
    FINESS_REQUIRE_NON_NULL("成色不能为空"),
    PURCHASE_PRICE_NON_NULL("采购价不能为空"),
    SALES_PRIORITY_NON_NULL("销售优先等级不能为空"),
    SALES_PRIORITY_ERROR("销售优先等级错误"),
    STRAP_MATERIAL_NON_NULL("表带类型不能为空或者不符合规范"),
    GOODS_LEVEL_NON_NULL("商品自主经营类型不能为空"),
    GOODS_LEVEL_ERROR("商品级别错误"),
    ATTACHMENT_NON_NULL("附件不能为空"),
    PRICING_NON_NULL("价格异常"),
    STOCK_SN_REPEAT("重复表身号:{}"),
    DATA_REPEAT("数据存在重复项，请修正表格数据"),
    EXCEL_EXCEPTION("Excel导入模版异常,请重新下载!"),
    EXCEL_DATA_EXCEPTION("Excel导入数据异常,请修正后重试!"),
    INVALID_OPERATION("无效操作,请确认操作数据"),
    INVALID_STOCK_SN("无效表身号,请确认操作数据"),
    STOCK_SN_ERROR("无效表身号:{},请确认操作数据"),
    INVALID_WORK_ERROR("无效作业单:{},请确认操作数据"),
    DECRYPTION_FAILED("解密失败！失败原因:{}"),
    SALESMAN_FAILED("第一/二/三销售人不能相同"),
    PURCHASE_CONFUSION("采购方式混乱"),
    DOU_YIN_ORDER_CONSOLIDATION_FAIL("抖音合单客户姓名必须是一致的"),
    DOU_YIN_ORDER_STATUS_NOT_ALLOW("抖音合单存在不允许的状态"),
    DOU_YIN_ORDER_MUST_UNUSED("抖音合单必须是未审核的订单"),
    DOU_YIN_ORDER_SAMPLING_ADDRESS_ONLY_ONE("抖音合单必须是只有一个抽检地址"),
    ACCOUNT_NON_NULL("存在为空字段"),
    BOX_NUMBER_REQUIRE_NON_NULL("盒号不能为空"),
    ACCOUNT_NON_EXCEPTION("金蝶账号登录异常"),
    SALE_WORK_NOT_OPERATE("销售打单无法操作"),
    SALE_WORK_INTERCEPT("销售售后拦截"),
    QUOTE_RECORD_NOT_EXIST("估价记录不存在"),
    QUOTE_STATE_FAIL("订单处于{}状态，无法进行当前操作！"),
    XIAN_YU_API_FAIL("闲鱼失败:{}"),

    REQUIRE_NON_NULL("需求年份不能为空"),
    RATE_NON_NULL("预估毛利率不能为空"),
    TASK_NON_NULL("任务数量不能为空"),
    SALE_PRICE_NON_NULL("预估价格不能为空"),
    PURCHASE_JOIN__NON_NULL("对接采购人不能为空"),
    PURCHASE_TASK__NON_NULL("当前采购不能由采购需求发起"),
    TO_STORE_REQUIRE_NON_NULL("调入方不能为空"),

    ONLY_SUPPORTED_SINGLE_BRAND("不支持多个品牌同时操作修改数据，请分批操作"),

    ENUM_ERROR("枚举异常"),
    RESTRICT_ERROR("打款账号限制异常"),
    STATE_ERROR("状态限制异常"),

    SETP_1("超出额度控制")
    ;

    private String errMsg;
}
