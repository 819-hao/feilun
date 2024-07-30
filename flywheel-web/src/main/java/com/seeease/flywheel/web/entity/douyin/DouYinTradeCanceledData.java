package com.seeease.flywheel.web.entity.douyin;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 抖音订单取消消息数据
 *
 * @author Tiro
 * @date 2023/4/25
 */
@Data
public class DouYinTradeCanceledData implements Serializable {
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
     * 父订单状态，订单取消消息的status值为"4"
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

    /**
     * 订单取消时间
     */
    @JsonAlias("cancel_time")
    private Long cancelTime;

    /**
     * 取消原因
     */
    @JsonAlias("cancel_reason")
    private String cancelReason;
}