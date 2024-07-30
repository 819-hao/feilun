package com.seeease.flywheel.serve.goods.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品状态
 *
 * @author wbh
 * @date 2023/2/9
 */
@Getter
@AllArgsConstructor
public enum StockStatusEnum implements IStateEnum<Integer> {

    //新的逻辑
    //采购在途 (收完货之后)
    //未收货 (上传完快递单号)
    //退回中 (质检判定.. or 采购退货发货之前)） //770
    //600-770 退回中
    PURCHASE_IN_TRANSIT(600, true, "采购在途"),

    //620-600 正常流程
    //620-9 拒收 不存在
    WAIT_RECEIVED(620, true, "未收货(采购)"),

    WAIT_PRICING(610, true, "待定价"),

    ALLOCATE_IN_TRANSIT(666, true, "调拨在途"),

    MARKETABLE(100, true, "可售"),

    EXCEPTION(777, true, "异常不可售"),
    SCRAPPING(778, true, "报废商品"),

    EXCEPTION_IN(760, true, "异常转出中"),

    //todo 采退不可售 就是退回中 页面上的退货中
    PURCHASE_RETURNED_ING(770, true, "退回中"),

    SOLD_OUT(888, false, "已销售"),

    CONSIGNMENT(880, false, "已寄售"),

    PURCHASE_RETURNED(9, false, "已退回"),

    ON_LOAN(10000, true, "已借出"),

//    USED(999, false, "已使用"),

    ;
    private Integer value;
    /**
     * 表示这个状态的库存是否还在库(货是否还在)
     */
    private Boolean inStore;
    private String desc;

    public static StockStatusEnum fromCode(int value) {
        return Arrays.stream(StockStatusEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

    /**
     * 获取所有在库定义的库存状态
     *
     * @return
     */
    public static List<StockStatusEnum> getInStoreStockStatusEnum() {
        return Arrays.stream(StockStatusEnum.values())
                .filter(StockStatusEnum::getInStore)
                .collect(Collectors.toList());
    }


    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {
        /**
         * 新建调拨
         */
        ALLOCATE(StockStatusEnum.MARKETABLE, StockStatusEnum.ALLOCATE_IN_TRANSIT, "调拨在途"),

        /**
         * 调拨取消或入库
         */
        ALLOCATE_CANCEL_OR_IN_STORAGE(StockStatusEnum.ALLOCATE_IN_TRANSIT, StockStatusEnum.MARKETABLE, "调拨在途"),

        /**
         * 调拨入异常库
         */
        ALLOCATE_IN_EXCEPTION_STORAGE(StockStatusEnum.ALLOCATE_IN_TRANSIT, StockStatusEnum.EXCEPTION, "调拨在途"),

        /**
         * 新建销售
         */
        SALE(StockStatusEnum.MARKETABLE, StockStatusEnum.SOLD_OUT, "销售已售出"),

        /**
         * 取消销售
         */
        SALE_CANCEL(StockStatusEnum.SOLD_OUT, StockStatusEnum.MARKETABLE, "销售已售出"),
        /**
         * 销售异常入库
         */
        SALE_EXCEPTION(StockStatusEnum.SOLD_OUT, StockStatusEnum.EXCEPTION, "销售异常入库"),
        /**
         * 寄售异常入库
         */
        CONSIGNMENT_EXCEPTION(StockStatusEnum.CONSIGNMENT, StockStatusEnum.EXCEPTION, "寄售异常入库"),
        /**
         * 销售正常入库
         */
        SALE_MARKETABLE(StockStatusEnum.SOLD_OUT, StockStatusEnum.MARKETABLE, "售出变入库"),
        /**
         * 寄售正常入库
         */
        CONSIGNMENT_MARKETABLE(StockStatusEnum.CONSIGNMENT, StockStatusEnum.MARKETABLE, "寄售变入库"),
        /**
         * 新建寄售
         */
        SALE_CONSIGNMENT(StockStatusEnum.MARKETABLE, StockStatusEnum.CONSIGNMENT, "销售已寄售"),

        /**
         * 取消寄售
         */
        SALE_CONSIGNMENT_CANCEL(StockStatusEnum.CONSIGNMENT, StockStatusEnum.MARKETABLE, "销售已寄售"),

        /**
         * 寄售结算
         */
        SALE_CONSIGNMENT_SETTLEMENT(StockStatusEnum.CONSIGNMENT, StockStatusEnum.SOLD_OUT, "寄售已售出"),

        /**
         * --------采购开始------
         */

        /**
         * 新建采购退货单
         */
        MARKETABLE_PURCHASE_RETURNED_ING(StockStatusEnum.MARKETABLE, StockStatusEnum.PURCHASE_RETURNED_ING, "采购退货待退货"),

        /**
         * 采购在途中 拒收已退货
         */
//        PURCHASE_IN_TRANSIT_PURCHASE_RETURNED(StockStatusEnum.PURCHASE_IN_TRANSIT, StockStatusEnum.PURCHASE_RETURNED, "采购在途中-已退货"),

        WAIT_PRICING_PURCHASE_RETURNED(StockStatusEnum.WAIT_PRICING, StockStatusEnum.PURCHASE_RETURNED_ING, "采购在途中-已退货"),

        /**
         * 采购在途中 采退不可售
         */
        PURCHASE_IN_TRANSIT_PURCHASE_RETURNED_ING(StockStatusEnum.PURCHASE_IN_TRANSIT, StockStatusEnum.PURCHASE_RETURNED_ING, "采购在途中-退回中"),

        /**
         *  新增 todo
         */
        WAIT_RECEIVED_PURCHASE_IN_TRANSIT(StockStatusEnum.WAIT_RECEIVED, StockStatusEnum.PURCHASE_IN_TRANSIT, "待收货-采购在途中"),

        /**
         * 已退回
         */
        WAIT_RECEIVED_PURCHASE_RETURNED(StockStatusEnum.WAIT_RECEIVED, StockStatusEnum.PURCHASE_RETURNED, "未收货-已退回"),

        /**
         * 采退不可售 已退货
         */
        PURCHASE_RETURNED_ING_PURCHASE_RETURNED(StockStatusEnum.PURCHASE_RETURNED_ING, StockStatusEnum.PURCHASE_RETURNED, "采退不可售-已退货"),

        /**
         * 入库 校验 是否已经
         */
        PURCHASE_IN_TRANSIT_MARKETABLE(StockStatusEnum.PURCHASE_IN_TRANSIT, StockStatusEnum.MARKETABLE, "采购在途中-可售"),
        PURCHASE_IN_TRANSIT_EXCEPTION(StockStatusEnum.PURCHASE_IN_TRANSIT, StockStatusEnum.EXCEPTION, "采购在途中-异常不可售"),
        /**
         * 采购在途 -待定价
         */
        PURCHASE_IN_TRANSIT_WAIT_PRICING(StockStatusEnum.PURCHASE_IN_TRANSIT, StockStatusEnum.WAIT_PRICING, "采购在途中-待定价"),

        /**
         * 待定价-可售
         */
        WAIT_PRICING_MARKETABLE(StockStatusEnum.WAIT_PRICING, StockStatusEnum.MARKETABLE, "待定价-可售"),
        MARKETABLE_WAIT_PRICING(StockStatusEnum.MARKETABLE, StockStatusEnum.WAIT_PRICING, "可售-待定价"),

        /**
         * 采退取消
         */
        PURCHASE_RETURNED_ING_WAIT_PRICING(StockStatusEnum.PURCHASE_RETURNED_ING, StockStatusEnum.WAIT_PRICING, "采退取消-商品待定价"),

        /**
         * --------采购结束------
         */
        /**
         * --- 异常处理流程
         */
        EXCEPTION_EXCEPTION_IN(StockStatusEnum.EXCEPTION, StockStatusEnum.EXCEPTION_IN, "商品异常到异常进行中"),
        EXCEPTION_WAIT_PRICING(StockStatusEnum.EXCEPTION, StockStatusEnum.WAIT_PRICING, "商品异常到待定价"),
        EXCEPTION_MARKETABLE(StockStatusEnum.EXCEPTION, StockStatusEnum.MARKETABLE, "商品异常到可售"),
        EXCEPTION_IN_WAIT_PRICING(StockStatusEnum.EXCEPTION_IN, StockStatusEnum.WAIT_PRICING, "商品异常进行中到待定价"),
        EXCEPTION_IN_MARKETABLE(StockStatusEnum.EXCEPTION_IN, StockStatusEnum.MARKETABLE, "商品异常途中到可售"),
        EXCEPTION_IN_EXCEPTION(StockStatusEnum.EXCEPTION_IN, StockStatusEnum.EXCEPTION, "商品途中异常到不可售"),
        STOCK_IN_EXCEPTION(StockStatusEnum.MARKETABLE, StockStatusEnum.EXCEPTION, "商品可售到异常库"),
        STOCK_IN_EXCEPTION_WAIT_PRICING(StockStatusEnum.WAIT_PRICING, StockStatusEnum.EXCEPTION, "商品待定价到异常库"),
        EXCEPTION_TO_SCRAPPING(StockStatusEnum.EXCEPTION, StockStatusEnum.SCRAPPING, "异常商品转报废"),
        SCRAPPING_TO_EXCEPTION(StockStatusEnum.SCRAPPING, StockStatusEnum.EXCEPTION, "报废转异常商品"),

        SOLD_OUT_MARKETABLE(StockStatusEnum.SOLD_OUT, StockStatusEnum.MARKETABLE, "已销售到可售"),
        //采购流程新增节点 todo


        /**
         * --- 异常处理流程
         */
        //TODO 退货入库修改商品状态
        ;
        private StockStatusEnum fromState;
        private StockStatusEnum toState;
        private String desc;

    }
}
