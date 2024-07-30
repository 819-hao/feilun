package com.seeease.flywheel.purchase.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/10/10 09:42
 */
@Data
public class AutoPurchaseCreateRequest implements Serializable {

    /**
     * 发货列表
     */
    private List<Integer> workIdList;

    //    /**
//     * 采购单号
//     */
//    private String serialNo;
//
//    /**
//     * 表的id
//     */
//    private Integer stockId;
//
//    /**
//     * 返修还是换货
//     */
//    private Integer returnType;

}
