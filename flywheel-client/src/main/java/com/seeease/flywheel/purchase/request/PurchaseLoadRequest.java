package com.seeease.flywheel.purchase.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/9/7 15:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseLoadRequest implements Serializable {

    /**
     * 采购单号
     */
    private String serialNo;

    /**
     * 采购创建的门店
     */
    private Integer storeId;

    /**
     * 建单门店的销售销售简码
     */
    private String shortcodes;

    /**
     * 采购一维类型
     */
    private Integer businessKey;

    /**
     * 采购行对应的商品编码
     */
    private List<String> line;
}
