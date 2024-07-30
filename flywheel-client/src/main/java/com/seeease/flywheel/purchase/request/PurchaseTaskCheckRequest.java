package com.seeease.flywheel.purchase.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 采购任务审核
 * @Date create in 2023/10/25 15:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseTaskCheckRequest implements Serializable {

    private String serialNo;

    private Integer id;

    /**
     * 1 是
     * 0 否
     */
    private Integer check;
}
