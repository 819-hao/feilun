package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/10 14:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrintOptionResult implements Serializable {

    /**
     * 打印类型
     */
    private Integer printOption;

    /**
     * 抖音联系人
     */
    private Integer douYinOption;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 三方单号 ,
     */
    private String bizOrderCode;

    /**
     * 门店id
     */
    private Integer shopId;

    /**
     * 国检码列表
     */
    private String spotCheckCode;
    /**
     * 销售单号
     */
    private String serialNo;
//
//    /**
//     * 商品编码
//     */
//    private String wno;

    /**
     * 打印商品信息
     */
    private String printProductName;

    private List<Integer> stockIdList;

    private String model;
}
