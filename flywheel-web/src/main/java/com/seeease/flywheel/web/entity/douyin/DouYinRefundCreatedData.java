package com.seeease.flywheel.web.entity.douyin;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;

/**
 * 抖音订单买家退货消息数据
 *
 * @author Tiro
 * @date 2023/4/25
 */
@Data
public class DouYinRefundCreatedData implements Serializable {


    /**
     * 店铺ID
     */
    @JsonAlias("shop_id")
    private Long shopId;

    /**
     * 售后单ID
     */
    @JsonAlias("aftersale_id")
    private Long aftersaleId;


    /**
     * 正向父订单ID
     */
    @JsonAlias("p_id")
    private Long pId;

    /**
     * 正向子订单ID
     */
    @JsonAlias("s_id")
    private Long sId;

    /**
     * 售后状态：6-售后申请；7-售后退货中；8-【补寄\维修返回：售后待商家发货】；11-售后已发货；12-售后成功；13-【换货\补寄\维修返回：售后商家已发货，待用户收货】； 14-【换货\补寄\维修返回：售后用户已收货】 ；27-拒绝售后申请；28-售后失败；29-售后退货拒绝；51-订单取消成功；53-逆向交易已完成；
     */
    @JsonAlias("aftersale_status")
    private Long aftersaleStatus;

    /**
     * 售后类型： 0-售后退货退款；1-售后仅退款；2-发货前退款；3-换货；6-价保；7-补寄；8-维修
     */
    @JsonAlias("aftersale_type")
    private Long aftersaleType;

    /**
     * 售后申请时间
     */
    @JsonAlias("apply_time")
    private Long applyTime;
    /**
     * 售后申请时间
     */
    @JsonAlias("modify_time")
    private Long modifyTime;
    /**
     * 申请退款的金额（含运费）
     */
    @JsonAlias("refund_amount")
    private Long refundAmount;

    /**
     * 原因码；通过【afterSale/rejectReasonCodeList】接口获取
     */
    @JsonAlias("reason_code")
    private Long reasonCode;

    /**
     * true拦截，false拦截取消
     */
    private boolean saleIntercept;
}
