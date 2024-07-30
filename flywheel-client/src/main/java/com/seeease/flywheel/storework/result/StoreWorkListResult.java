package com.seeease.flywheel.storework.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 列表页返回
 *
 * @Auther Gilbert
 * @Date 2023/1/17 18:48
 */
@Data
public class StoreWorkListResult implements Serializable {

    private Integer id;

    /**
     * 工作类型：1-出库，2-入库
     */
    private Integer workType;

    /**
     * 作业由来
     */
    private Integer workSource;

    /**
     * 作业单状态
     */
    private Integer workState;

    /**
     * 预作业单号
     */
    private String serialNo;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 库存id
     */
    private Integer stockId;


    /**
     * 商品情况：0-正常，1-缺货，2-商品实物不符
     */
    private Integer commoditySituation;

    /**
     * 任务到手时间
     */
    private String taskArriveTime;

    /**
     * 异常标记
     */
    private Integer exceptionMark;

    /**
     * 备注
     */
    private String remarks;
    /**
     * 3号楼退货的备注
     */
    private String remark;

    private String finess;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 数量
     */
    private Integer number;


    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 主图
     */
    private String image;

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 系列
     */
    private String seriesName;

    private Integer seriesType;
    /**
     * 型号
     */
    private String model;

    /**
     * 机芯类型
     */
    private String movement;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 附件详情
     */
    private String attachment;

    /**
     * 0 无 1 空白 1 有
     */
    private Integer isCard;

    private String warrantyDate;

    /**
     * 采购附件详情
     */
    private Map<String, List<Integer>> dictChildList;

    /**
     * 表径
     */
    private String watchSize;

    /**
     * 公价
     */
    private BigDecimal pricePub;

    /**
     * 吊牌价
     */
    private BigDecimal tagPrice;

    /**
     * 商品编码
     */
    private String wno;

    /**
     * 盒子编号
     */
    private String boxNumber;

    /**
     * 调拨类型:1-寄售,2-寄售归还,3-平调,4-借调
     */
    private Integer allocateType;

    /**
     * 调出方
     */
    private String fromName;

    /**
     * 配件参数
     */
    private ExtAttachmentStockVO extAttachmentStock;

    /**
     * 客户公司名称
     */
    private String customerName;

    @Data
    public static class ExtAttachmentStockVO implements Serializable {
        /**
         * 颜色
         */
        private String colour;

        /**
         * 材质
         */
        private String material;

        /**
         * 尺寸
         */
        private String size;

        /**
         * 适用腕表型号
         */
        private String gwModel;
    }
}
