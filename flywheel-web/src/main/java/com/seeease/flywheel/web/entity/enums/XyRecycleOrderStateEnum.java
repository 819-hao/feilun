package com.seeease.flywheel.web.entity.enums;

import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Tiro
 * @date 2023/10/19
 */
@AllArgsConstructor
@Getter
public enum XyRecycleOrderStateEnum implements IStateEnum<Integer> {

    /**
     * 估价
     */
    CREATE(1, "待估价"),
    QUOTED(2, "已估价等待卖家确认"),

    /**
     * 订单履约
     */
    WAIT_PICK_UP(3, "待取件"),
    WAIT_RECEIVED(4, "待收货"),
    WAIT_QT(5, "待质检"),
    QT(6, "已质检"),
    WAIT_PAYMENT(7, "待打款"),
    APPLY_REFUND(8, "申请退回"),
    CONFIRM_COMPLETE(9, "卖家确认交易完成"),
    CANCEL(10, "已取消"),
    REFUND(11, "已退回"),
    ;

    private Integer value;
    private String desc;

    public static XyRecycleOrderStateEnum findByValue(Integer value) {
        if (Objects.isNull(value)) {
            return null;
        }
        return Arrays.stream(XyRecycleOrderStateEnum.values())
                .filter(t -> t.getValue().intValue() == value)
                .findFirst()
                .orElse(null);
    }


    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {
        //正向
        QUOTED_TO_ONE(XyRecycleOrderStateEnum.CREATE, XyRecycleOrderStateEnum.QUOTED, "业务完成第一次估价"),
        QUOTED_TO(XyRecycleOrderStateEnum.QUOTED, XyRecycleOrderStateEnum.QUOTED, "估价重入"),
        PLACE_ORDER(XyRecycleOrderStateEnum.QUOTED, XyRecycleOrderStateEnum.WAIT_PICK_UP, "用户下单/或修改地址"),
        UP_ADDRESS(XyRecycleOrderStateEnum.WAIT_PICK_UP, XyRecycleOrderStateEnum.QUOTED, "同意用户修改地址"),
        PICK_UP(XyRecycleOrderStateEnum.WAIT_PICK_UP, XyRecycleOrderStateEnum.WAIT_RECEIVED, "快递揽件"),
        RECEIVED(XyRecycleOrderStateEnum.WAIT_RECEIVED, XyRecycleOrderStateEnum.WAIT_QT, "服务商收货"),
        QT(XyRecycleOrderStateEnum.WAIT_QT, XyRecycleOrderStateEnum.QT, "质检完成"),
        USER_AGREE(XyRecycleOrderStateEnum.QT, XyRecycleOrderStateEnum.WAIT_PAYMENT, "用户同意回收"),
        PAYMENT(XyRecycleOrderStateEnum.WAIT_PAYMENT, XyRecycleOrderStateEnum.CONFIRM_COMPLETE, "成功打款"),


        //逆向取消
        CREATE_CANCEL(XyRecycleOrderStateEnum.CREATE, XyRecycleOrderStateEnum.CANCEL, "估价前取消，用户不接受估价/服务商取消"),
        QUOTED_CANCEL(XyRecycleOrderStateEnum.QUOTED, XyRecycleOrderStateEnum.CANCEL, "用户不接受估价/服务商取消"),
        WAIT_PICK_UP_CANCEL(XyRecycleOrderStateEnum.WAIT_PICK_UP, XyRecycleOrderStateEnum.CANCEL, "待取件->取消"),

        //逆向退回
        WAIT_RECEIVED_REFUND(XyRecycleOrderStateEnum.WAIT_RECEIVED, XyRecycleOrderStateEnum.REFUND, "主动退回：待收货->退回"),
        WAIT_QT_REFUND(XyRecycleOrderStateEnum.WAIT_QT, XyRecycleOrderStateEnum.REFUND, "主动退回：待质检->退回"),
        QT_REFUND(XyRecycleOrderStateEnum.QT, XyRecycleOrderStateEnum.REFUND, "主动退回：质检->退回"),

        QT_APPLY_REFUND(XyRecycleOrderStateEnum.QT, XyRecycleOrderStateEnum.APPLY_REFUND, "卖家申请退回"),
        APPLY_REFUND_RETURN(XyRecycleOrderStateEnum.APPLY_REFUND, XyRecycleOrderStateEnum.QT, "卖家撤销申请退回"),
        REFUND(XyRecycleOrderStateEnum.APPLY_REFUND, XyRecycleOrderStateEnum.REFUND, "已退回"),

        ;
        private XyRecycleOrderStateEnum fromState;
        private XyRecycleOrderStateEnum toState;
        private String desc;

    }

}