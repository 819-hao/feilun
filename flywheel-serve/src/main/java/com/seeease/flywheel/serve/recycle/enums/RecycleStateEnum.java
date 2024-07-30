package com.seeease.flywheel.serve.recycle.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Auther Gilbert
 * @Date 2023/9/4 19:48
 */
@Getter
@AllArgsConstructor
public enum RecycleStateEnum implements IStateEnum<Integer> {

    UN_CONFIRMED(0, "待报价", "待开始"),

    CUSTOMER_RECEIVE(1, "待确认", "进行中"),

    WAY_OFFER(3, "待再次报价", "进行中"),

    CUSTOMER_RECEIVE_SURE(4, "待再次确认", "进行中"),

    WAIT_UPLOAD_CUSTOMER(6, "待上传打款信息", "进行中"),

    MAKE_ORDER(7, "待建单", "进行中"),

    COMPLETE(8, "已完成", "已完成"),

    CANCEL_WHOLE(9, "全部取消", "已取消"),

    WAIT_DELIVER_LOGISTICS(10, "待打款", "进行中"),

    ;
    private Integer value;
    private String desc;
    private String remark;

    public static RecycleStateEnum fromCode(int value) {
        return Arrays.stream(RecycleStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {
        /**
         * 回收客户经理是否接受
         */
        UN_CONFIRMED_CUSTOMER_ACCEPT(RecycleStateEnum.UN_CONFIRMED, RecycleStateEnum.CUSTOMER_RECEIVE, "回收第一次报价后客户经理确认"),
        /**
         * 回收客户取消订单
         */
        UN_CONFIRMED_CANCEL_WHOLE(RecycleStateEnum.UN_CONFIRMED, RecycleStateEnum.CANCEL_WHOLE, "取消订单"),

        /**
         * 第一次上传
         */
        CUSTOMER_RECEIVE_WAY_OFFER(RecycleStateEnum.CUSTOMER_RECEIVE, RecycleStateEnum.WAY_OFFER, "第一次接受上传收货信息"),

        /**
         * 回收客户经理第一次不接受
         */
        CUSTOMER_RECEIVE_CANCEL_ORDER(RecycleStateEnum.CUSTOMER_RECEIVE, RecycleStateEnum.CANCEL_WHOLE, "第一次不接受待取消"),

        /**
         * 回收客户经理上传快递信息等待二次报价
         */
//        WAIT_LOGISTICS_WAY_OFFER(RecycleStateEnum.WAIT_LOGISTICS, RecycleStateEnum.WAY_OFFER, "上传收货信息待二次报价"),


        /**
         * 客户经理取消订单
         */
//        CANCEL_ORDER_CANCEL_WHOLE(RecycleStateEnum.CANCEL_ORDER, RecycleStateEnum.CANCEL_WHOLE, "客户经理取消订单"),

        /**
         * 二次报价
         */
        WAY_OFFER_CUSTOMER_RECEIVE_SURE(RecycleStateEnum.WAY_OFFER, RecycleStateEnum.CUSTOMER_RECEIVE_SURE, "二次报价到二次确认"),


        /**
         * 二次确认
         */
        CUSTOMER_RECEIVE_SURE_MAKE_ORDER(RecycleStateEnum.CUSTOMER_RECEIVE_SURE, RecycleStateEnum.MAKE_ORDER, "二次确认待上传打款信息"),
        /**
         *
         */
//        CUSTOMER_RECEIVE_SURE_CLIENT_PAYMENT(RecycleStateEnum.CUSTOMER_RECEIVE_SURE, RecycleStateEnum.CLIENT_PAYMENT, "二次报价确认待客户付款"),
        /**
         * 待建单
         */
//        CUSTOMER_RECEIVE_SURE_COMPLETE(RecycleStateEnum.CUSTOMER_RECEIVE_SURE, RecycleStateEnum.MAKE_ORDER, "二次报价确认到已完成平账"),

        /**
         *
         */
        CUSTOMER_RECEIVE_SURE_CANCEL_WHOLE(RecycleStateEnum.CUSTOMER_RECEIVE_SURE, RecycleStateEnum.CANCEL_WHOLE, "取消"),

        /**
         * 二次确认
         */
        WAIT_UPLOAD_CUSTOMER_MAKE_ORDER(RecycleStateEnum.WAIT_UPLOAD_CUSTOMER, RecycleStateEnum.MAKE_ORDER, "上传打款信息待建单"),
        /**
         *
         */
//        CLIENT_PAYMENT_MAKE_ORDER(RecycleStateEnum.CLIENT_PAYMENT, RecycleStateEnum.MAKE_ORDER, "客户付款到待建单"),

        /**
         * 建单完成
         */
        MAKE_ORDER_COMPLETE(RecycleStateEnum.MAKE_ORDER, RecycleStateEnum.COMPLETE, "建单完成到整单完成"),

        /**
         * 拒绝发货完成
         */
//        WAIT_LOGISTICS_REFUSE_COMPLETE(RecycleStateEnum.WAIT_LOGISTICS_REFUSE, RecycleStateEnum.COMPLETE, "发完货结束"),
//        WAIT_LOGISTICS_REFUSE_CANCEL_WHOLE(RecycleStateEnum.WAIT_LOGISTICS_REFUSE, RecycleStateEnum.CANCEL_WHOLE, "发完货结束"),


        /**
         * 回购
         */
        MAKE_ORDER_CUSTOMER_RECEIVE(RecycleStateEnum.MAKE_ORDER, RecycleStateEnum.CUSTOMER_RECEIVE, "待建单到待确认"),

        /**
         * 待确认
         */
        CUSTOMER_RECEIVE_WAIT_UPLOAD_BANK(RecycleStateEnum.CUSTOMER_RECEIVE, RecycleStateEnum.WAIT_UPLOAD_CUSTOMER, "待上传打款信息"),
        MAKE_ORDER_WAIT_UPLOAD_BANK(RecycleStateEnum.MAKE_ORDER, RecycleStateEnum.WAIT_UPLOAD_CUSTOMER, "待上传打款信息"),

//        CUSTOMER_RECEIVE_CLIENT_PAYMENT(RecycleStateEnum.CUSTOMER_RECEIVE, RecycleStateEnum.CLIENT_PAYMENT, "待客户付款"),

        CUSTOMER_RECEIVE_COMPLETE(RecycleStateEnum.CUSTOMER_RECEIVE, RecycleStateEnum.COMPLETE, "已完成平账"),

        /**
         * 客户不接受，直接流程结束
         */
//        CUSTOMER_RECEIVE_WAIT_LOGISTICS_REFUSE(RecycleStateEnum.CUSTOMER_RECEIVE, RecycleStateEnum.WAIT_LOGISTICS_REFUSE, "拒绝等待发货"),

//        CLIENT_PAYMENT_COMPLETE(RecycleStateEnum.CLIENT_PAYMENT, RecycleStateEnum.COMPLETE, "付完款"),

        WAIT_UPLOAD_CUSTOMER_WAIT_DELIVER_LOGISTICS(RecycleStateEnum.WAIT_UPLOAD_CUSTOMER, RecycleStateEnum.WAIT_DELIVER_LOGISTICS, "上传打款信息待打款"),

        WAIT_DELIVER_LOGISTICS_COMPLETE(RecycleStateEnum.WAIT_DELIVER_LOGISTICS, RecycleStateEnum.COMPLETE, "打完款"),

//        WAIT_DELIVER_LOGISTICS_COMPLETE(RecycleStateEnum.WAIT_DELIVER_LOGISTICS, RecycleStateEnum.COMPLETE, "完成订单"),
        /**
         * 采购取消订单
         */
        PURCEASE_CANCEL(RecycleStateEnum.COMPLETE, RecycleStateEnum.CANCEL_WHOLE, "已完成采购取消"),
        /**
         * 采购取消订单
         */
        WAY_OFFER_PURCEASE_CANCEL(RecycleStateEnum.WAY_OFFER, RecycleStateEnum.CANCEL_WHOLE, "待二次报价采购取消"),

        /**
         * 采购取消订单
         */
        WAIT_UPLOAD_CUSTOMER_CANCEL(RecycleStateEnum.WAIT_UPLOAD_CUSTOMER, RecycleStateEnum.CANCEL_WHOLE, "待上传打款信息采购取消"),

        /**
         * 回购仅回收状态打款
         */
        WAIT_UPLOAD_CUSTOMER_COMPLETE(RecycleStateEnum.WAIT_UPLOAD_CUSTOMER, RecycleStateEnum.COMPLETE, "仅回收上传完打款信息就完成了"),

        /**
         * 待建单取消操作
         */
        MAKE_ORDER_CANCEL(RecycleStateEnum.MAKE_ORDER, RecycleStateEnum.CANCEL_WHOLE, "待建单取消操作"),
        ;
        private RecycleStateEnum fromState;
        private RecycleStateEnum toState;
        private String desc;
    }
}
