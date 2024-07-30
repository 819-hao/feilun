package com.seeease.flywheel.storework.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 仓库作业创建请求
 *
 * @Auther Gilbert
 * @Date 2023/1/17 17:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorKCreateRequest implements Serializable {

    /**
     * 归属门店id
     */
    private Integer belongingStoreId;

    /**
     * 工作类型: 1-出库，2-入库
     */
    private Integer workType;
    /**
     * 来源
     */
    private Integer workSource;

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
     * 配对标记
     */
    private String mateMark;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 客户联系id
     */
    private Integer customerContactId;

    /**
     * 保卡管理-是否已调拨
     */
    private Integer guaranteeCardManage;

}
