package com.seeease.flywheel.web.entity.douyin;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 抖音订单支付消息数据
 *
 * @author Tiro
 * @date 2023/4/25
 */
@Data
public class DouYinTradePaidData implements Serializable {
    /**
     * 店铺ID
     */
    @JsonAlias("shop_id")
    private Long shopId;

    /**
     * 父订单ID
     */
    @JsonAlias("p_id")
    private Long pId;

    /**
     * 子订单ID列表
     */
    @JsonAlias("s_ids")
    private List<Long> sIds;

    /**
     * 订单实付金额
     */
    @JsonAlias("pay_amount")
    private Long payAmount;

    /**
     * 订单支付方式： 0：货到付款，1：微信，2：支付宝，4：银行卡，5：抖音零钱，7：无需支付，8：DOU分期，9：新卡支付，12：先用后付，13：组合支付。
     */
    @JsonAlias("pay_type")
    private Long payType;

    /**
     * 1: 在线订单支付时间 2: 货到付款订单确认时间
     */
    @JsonAlias("pay_time")
    private Long payTime;

    /**
     * 父订单状态，订单支付消息的status值为"2"
     */
    @JsonAlias("order_status")
    private Long orderStatus;

    /**
     * 订单类型： 0: 实物 2: 普通虚拟 4: poi核销 5: 三方核销 6: 服务市场
     */
    @JsonAlias("order_type")
    private Long order_type;

    /**
     * 订单业务类型，表示买家从哪里看到的这个商品、产生了订单: 1: 鲁班广告 2: 联盟 4: 商城 8:自主经营 10: 线索通支付表单 12: 抖音门店 14: 抖+ 15: 穿山甲
     */
    @JsonAlias("biz")
    private Long biz;
}