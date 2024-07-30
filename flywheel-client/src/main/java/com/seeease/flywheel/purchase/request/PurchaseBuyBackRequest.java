package com.seeease.flywheel.purchase.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 采购回购查询
 * @Date create in 2023/4/18 10:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseBuyBackRequest implements Serializable {

    /**
     * 销售单号列表
     */
    private List<String> saleSerialNoList;
}
