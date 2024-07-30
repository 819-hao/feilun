package com.seeease.flywheel.storework.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/8/31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WmsWorkListRequest extends PageRequest {

    /**
     * 开始时间
     */
    private String beginCreateTime;
    /**
     * 结束时间
     */
    private String endCreateTime;

    /**
     * 发货物流单号
     */
    private String deliveryExpressNumber;

    /**
     * 不同的场景对应不同的table标签页
     */
    private UseScenario useScenario;

    /**
     * 销售位置
     */
    private Integer saleStoreId;

    /**
     * 发货位置
     */
    private Integer deliveryStoreId;

    /**
     * 型号
     */
    private String model;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;

    /**
     * 0 不质检 1-线下质检 2-线上质检
     */
    private Integer inspectionType;

    private Integer shopId;
    private List<Integer> workStateList;
    private List<Integer> stockIdList;
    private List<Integer> goodsIdList;
    private List<Integer> belongingStoreIdList;
    /**
     * 作业拦截
     */
    private Integer workIntercept;

    /**
     * 场景
     */
    public enum UseScenario {
        /**
         * 待集单
         */
        WAIT_COLLECT,

        /**
         * 待打单
         */
        WAIT_PRINT,

        /**
         * 待发货
         */
        WAIT_DELIVERY,

        /**
         * 已发货
         */
        COMPLETE,

        /**
         * 拦截
         */
        INTERCEPT,

        /**
         * 取消
         */
        CANCEL,
    }
}
