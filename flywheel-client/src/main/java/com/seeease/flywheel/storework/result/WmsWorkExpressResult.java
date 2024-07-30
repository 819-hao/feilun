package com.seeease.flywheel.storework.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/9/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WmsWorkExpressResult implements Serializable {
    /**
     * 源头单号
     */
    private String originSerialNo;
    /**
     * 销售位置
     */
    private Integer saleStoreId;
    /**
     * 销售备注
     */
    private String saleRemarks;
    /**
     * 0 不质检 1-线下质检 2-线上质检
     */
    private Integer inspectionType;
    /**
     * 省
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 区
     */
    private String town;
    /**
     * 街道
     */
    private String street;
    /**
     * 联系地址
     */
    private String contactAddress;
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系电话
     */
    private String contactPhone;
    /**
     * 商品详情
     */
    private List<GoodsInfo> goodsInfos;
    /**
     * 作业拦截
     */
    private Integer workIntercept;

    @Data
    public static class GoodsInfo implements Serializable{
        /**
         * 品牌
         */
        private String brandName;

        /**
         * 系列
         */
        private String seriesName;

        /**
         * 型号
         */
        private String model;

        /**
         * 表身号
         */
        private String stockSn;

        /**
         * 商品编码
         */
        private String wno;
        /**
         * 抖音抽检码
         */
        private String spotCheckCode;
    }

    /**
     * 销售渠道
     */
    private Integer saleOrderChannel;

}
